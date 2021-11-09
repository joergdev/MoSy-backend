package de.joergdev.mosy.backend.bl.mockdata;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.SAXParseException;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.mockdata.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.utils.BlUtils;
import de.joergdev.mosy.backend.bl.utils.PersistenceUtil;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import de.joergdev.mosy.backend.persistence.model.MockProfile;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<MockData, SaveResponse>
{
  private de.joergdev.mosy.api.model.InterfaceMethod apiInterfaceMethodRequest = null;
  private Interface apiInterfaceRequest = null;

  private de.joergdev.mosy.backend.persistence.model.MockData dbMockData;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getTitle())
            || request.getTitle().length() > de.joergdev.mosy.backend.persistence.model.MockData.LENGTH_TITLE,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("title"));

    apiInterfaceMethodRequest = request.getInterfaceMethod();

    leaveOn(apiInterfaceMethodRequest == null
            || (apiInterfaceMethodRequest.getInterfaceMethodId() == null
                && Utils.isEmpty(apiInterfaceMethodRequest.getName())),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interface method"));

    apiInterfaceRequest = apiInterfaceMethodRequest.getMockInterface();

    leaveOn(apiInterfaceMethodRequest.getInterfaceMethodId() == null
            && (apiInterfaceRequest == null || (apiInterfaceRequest.getInterfaceId() == null
                                                && Utils.isEmpty(apiInterfaceRequest.getName()))),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interface"));

    leaveOn(!Utils.isEmpty(request.getRequest())
            && request.getRequest()
                .length() > de.joergdev.mosy.backend.persistence.model.MockData.LENGTH_REQUEST,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getResponse())
            || request.getResponse()
                .length() > de.joergdev.mosy.backend.persistence.model.MockData.LENGTH_RESPONSE,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response"));

    for (de.joergdev.mosy.api.model.MockProfile apiMockProfile : request.getMockProfiles())
    {
      Integer apiMockProfileID = apiMockProfile.getMockProfileID();

      leaveOn(apiMockProfileID != null && !Utils.isPositive(apiMockProfileID),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfileID"));

      leaveOn(apiMockProfileID == null && Utils.isEmpty(apiMockProfile.getName()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfileID / name"));
    }
  }

  @Override
  protected void execute()
  {
    loadOrCreateDbMockData();

    InterfaceMethod dbMethod = getDbMethod();

    // check if mockData to method
    leaveOn(dbMockData.getInterfaceMethod() != null
            && !dbMethod.getInterfaceMethodId()
                .equals(dbMockData.getInterfaceMethod().getInterfaceMethodId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockdata and interfaceMethod wrong"));

    // get MockProfiles from db
    Map<Integer, MockProfile> dbMockProfiles = getMockProfilesFromRequest();

    // set common flag if no mock profile assigned and add info
    setCommonIfNoMockProfile();

    // check title unique
    checkUniqueData();

    // format request/response
    formatRequestResponse(dbMethod);

    // set hashes request/response
    request.setRequestResponseHash();

    // transfer values
    ObjectUtils.copyValues(request, dbMockData, "interfaceMethod", "mockProfile", "created", "countCalls",
        "mockProfiles");
    dbMockData.setInterfaceMethod(dbMethod);

    // save
    entityMgr.persist(dbMockData);
    entityMgr.flush();

    // save mockProfiles
    saveMockProfiles(dbMockProfiles);
  }

  private void setCommonIfNoMockProfile()
  {
    if (request.getMockProfiles().isEmpty() && !Boolean.TRUE.equals(request.getCommon()))
    {
      request.setCommon(true);

      addResponseMessage(ResponseCode.DATA_SET.withAddtitionalInfo("mockdata set to common"));
    }
  }

  private void formatRequestResponse(InterfaceMethod dbMethod)
  {
    try
    {
      request.formatRequestResponse(BlUtils.getInterfaceTypeId(apiInterfaceMethodRequest, dbMethod));
    }
    catch (RuntimeException ex)
    {
      if (ex.getCause() instanceof SAXParseException)
      {
        leave(ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request / response xml format"));
      }
      else
      {
        throw ex;
      }
    }
  }

  private void saveMockProfiles(Map<Integer, MockProfile> dbMockProfiles)
  {
    boolean dbChanged = false;

    // handle existing mockProfiles (remove from map to add or delete from db)
    for (MockDataMockProfile dbMockDataMockProfile : Utils.nvlCollection(dbMockData.getMockProfiles()))
    {
      Integer dbMockProfileID = dbMockDataMockProfile.getMockProfile().getMockProfileID();

      // MockProfile exists => remove from map to add
      if (dbMockProfiles.containsKey(dbMockProfileID))
      {
        dbMockProfiles.remove(dbMockProfileID);
      }
      // MockProfile doestn exist (anymore) => delete
      else
      {
        entityMgr.remove(
            entityMgr.find(MockDataMockProfile.class, dbMockDataMockProfile.getMockDataMockProfileId()));

        dbChanged = true;
      }
    }

    // handle new mockProfiles (save)
    for (Integer mockProfileID : dbMockProfiles.keySet())
    {
      MockDataMockProfile dbMockDataMockProfile = new MockDataMockProfile();
      dbMockDataMockProfile.setMockData(dbMockData);
      dbMockDataMockProfile.setMockProfile(dbMockProfiles.get(mockProfileID));

      entityMgr.persist(dbMockDataMockProfile);

      dbChanged = true;
    }

    if (dbChanged)
    {
      entityMgr.flush();
    }
  }

  private Map<Integer, MockProfile> getMockProfilesFromRequest()
  {
    Map<Integer, MockProfile> dbMockProfiles = new HashMap<>();

    for (de.joergdev.mosy.api.model.MockProfile apiMockProfile : request.getMockProfiles())
    {
      Integer mockProfileID = apiMockProfile.getMockProfileID();
      MockProfile dbMockProfile = null;

      if (mockProfileID != null)
      {
        dbMockProfile = findDbEntity(MockProfile.class, mockProfileID,
            "mockProfile with id: " + mockProfileID);
      }
      else
      {
        dbMockProfile = getDao(MockProfileDao.class).getByName(apiMockProfile.getName(), null);
        leaveOn(dbMockProfile == null,
            ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("mockProfile " + apiMockProfile.getName()));

        mockProfileID = dbMockProfile.getMockProfileID();
      }

      dbMockProfiles.put(mockProfileID, dbMockProfile);
    }

    return dbMockProfiles;
  }

  private InterfaceMethod getDbMethod()
  {
    Integer interfaceMethodID = apiInterfaceMethodRequest.getInterfaceMethodId();

    if (interfaceMethodID != null)
    {
      return findDbEntity(InterfaceMethod.class, interfaceMethodID,
          "interface method with id " + interfaceMethodID);
    }
    // Name
    else
    {
      InterfaceMethod dbInterfaceMethod = PersistenceUtil.getDbInterfaceMethodByServicePaths(this,
          apiInterfaceRequest.getName(), apiInterfaceMethodRequest.getName());

      apiInterfaceMethodRequest.setInterfaceMethodId(dbInterfaceMethod.getInterfaceMethodId());
      apiInterfaceRequest.setInterfaceId(dbInterfaceMethod.getMockInterface().getInterfaceId());

      return dbInterfaceMethod;
    }
  }

  private void loadOrCreateDbMockData()
  {
    if (request.getMockDataId() != null)
    {
      dbMockData = findDbEntity(de.joergdev.mosy.backend.persistence.model.MockData.class,
          request.getMockDataId(), "mockData with id " + request.getMockDataId());
    }
    else
    {
      dbMockData = new de.joergdev.mosy.backend.persistence.model.MockData();
      dbMockData.setCreated(LocalDateTime.now());
      dbMockData.setCountCalls(0);
    }
  }

  private void checkUniqueData()
  {
    // check unique name
    leaveOn(
        getDao(MockDataDAO.class).existsByTitle(request.getInterfaceMethod().getInterfaceMethodId(),
            request.getTitle(), request.getMockDataId()),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("mockData with title: " + request.getTitle()));
  }

  @Override
  protected void fillOutput()
  {
    MockData apiMockDateResponse = new MockData();
    apiMockDateResponse.setMockDataId(dbMockData.getMockDataId());
    apiMockDateResponse.setCreatedAsLdt(dbMockData.getCreated());
    apiMockDateResponse.setCountCalls(dbMockData.getCountCalls());
    apiMockDateResponse.setActive(dbMockData.getActive());
    apiMockDateResponse.setCommon(dbMockData.getCommon());

    response.setMockData(apiMockDateResponse);
  }
}