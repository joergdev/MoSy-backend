package de.joergdev.mosy.backend.bl.mockservices;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.record.SaveResponse;
import de.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.globalconfig.Load;
import de.joergdev.mosy.backend.bl.record.Save;
import de.joergdev.mosy.backend.bl.utils.PersistenceUtil;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.MockData;
import de.joergdev.mosy.backend.persistence.model.MockProfile;
import de.joergdev.mosy.backend.persistence.model.RecordConfig;
import de.joergdev.mosy.backend.util.MockServicesUtil;
import de.joergdev.mosy.backend.util.SoapRouting;
import de.joergdev.mosy.shared.Utils;

public class CaptureCommon extends AbstractBL<CaptureCommonRequest, CaptureCommonResponse>
{
  private String mockResponse;
  private InterfaceMethod dbMethod;

  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(de.joergdev.mosy.shared.Utils.isEmpty(request.getServicePathInterface()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("servicepath interface"));

    leaveOn(!request.isRouteOnly()
            && de.joergdev.mosy.shared.Utils.isEmpty(request.getServicePathMethod()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("servicepath method"));

    leaveOn(!request.isRouteOnly() && de.joergdev.mosy.shared.Utils.isEmpty(request.getContent()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("content"));
  }

  @Override
  protected void execute()
  {
    Interface dbInterface = PersistenceUtil.getDbInterfaceByServicePath(this,
        request.getServicePathInterface());
    InterfaceType interfaceType = InterfaceType.getById(dbInterface.getType().getInterfaceTypeId());

    dbMethod = PersistenceUtil.getDbInterfaceMethodByServicePath(this, request.getServicePathMethod(),
        dbInterface, false);

    checkMockProfile();
    checkRecordSession();

    BaseData baseData = invokeSubBL(new Load(), null, new LoadBaseDataResponse()).getBaseData();

    if (mockEnabled(dbInterface, dbMethod, baseData) && !request.isRouteOnly())
    {
      tryMock(dbInterface, dbMethod, baseData, interfaceType);
    }
    // mock NOT enabled -> Routing
    else
    {
      if (interfaceType.directRoutingPossible)
      {
        doRouting(request.getContent(), dbInterface, baseData, dbMethod, interfaceType);
      }
      else
      {
        response.setRoute(true);
        response.setRecord(recordRequestResponse(baseData, dbInterface, dbMethod, interfaceType));
      }
    }

    // increase calls on method
    if (dbMethod != null)
    {
      getDao(InterfaceMethodDAO.class).increaseCountCalls(dbMethod.getInterfaceMethodId());
    }
  }

  private void tryMock(Interface dbInterface, InterfaceMethod dbMethod, BaseData baseData,
                       InterfaceType interfaceType)
  {
    MockData dbMockDataFound = getMockDataForRequest(dbMethod, interfaceType);

    if (dbMockDataFound == null)
    {
      if (routingOnNoMockData(dbInterface, dbMethod, baseData))
      {
        if (interfaceType.directRoutingPossible)
        {
          doRouting(request.getContent(), dbInterface, baseData, dbMethod, interfaceType);
        }
        else
        {
          response.setRoute(true);
          response.setRecord(recordRequestResponse(baseData, dbInterface, dbMethod, interfaceType));
        }
      }
      else
      {
        leaveOn(true,
            ResponseCode.OPERATION_FAILED_ERROR
                .withAddtitionalInfo("no mockdata for interface " + dbInterface.getName() + ", method "
                                     + dbMethod.getName() + ", request " + request.getContent()));
      }
    }
    // return mock response
    else
    {
      mockResponse = dbMockDataFound.getResponse();

      getDao(MockDataDAO.class).increaseCountCalls(dbMockDataFound.getMockDataId());
    }
  }

  private boolean routingOnNoMockData(Interface dbInterface, InterfaceMethod dbMethod, BaseData baseData)
  {
    if (Boolean.TRUE.equals(baseData.getRoutingOnNoMockData()))
    {
      return true;
    }
    else if (Boolean.FALSE.equals(baseData.getRoutingOnNoMockData()))
    {
      return false;
    }

    if (Boolean.TRUE.equals(dbInterface.getRoutingOnNoMockData()))
    {
      return true;
    }
    else if (Boolean.FALSE.equals(dbInterface.getRoutingOnNoMockData()))
    {
      return false;
    }

    return dbMethod != null && Boolean.TRUE.equals(dbMethod.getRoutingOnNoMockData());
  }

  private MockData getMockDataForRequest(InterfaceMethod dbMethod, InterfaceType interfaceType)
  {
    MockData dbMockDataFound = null;
    MockData dbMockDataMethodGlobal = null;

    boolean useCommonMockdata = useCommonMockdata();

    for (MockData dbMockData : getMockDataSorted(dbMethod.getMockData()))
    {
      if (!isMockDataRelevant(dbMockData, useCommonMockdata))
      {
        continue;
      }

      if (Utils.isEmpty(dbMockData.getRequest()))
      {
        dbMockDataMethodGlobal = dbMockData;
      }
      else
      {
        if (dataMatchesRequestContent(interfaceType, dbMockData.getRequest()))
        {
          dbMockDataFound = dbMockData;
          break;
        }
      }
    }

    if (dbMockDataFound == null)
    {
      dbMockDataFound = dbMockDataMethodGlobal;
    }

    return dbMockDataFound;
  }

  private boolean isMockDataRelevant(MockData dbMockData, boolean useCommonMockdata)
  {
    if (!Boolean.TRUE.equals(dbMockData.getActive()))
    {
      return false;
    }

    boolean commonDbMockData = Boolean.TRUE.equals(dbMockData.getCommon())
                               || dbMockData.getMockProfiles().isEmpty();

    String mockProfileNameReq = request.getMockProfileName();
    if (mockProfileNameReq == null)
    {
      if (!commonDbMockData)
      {
        return false;
      }
    }
    else
    {
      if (!dbMockData.getMockProfiles().stream()
          .anyMatch(mp -> mockProfileNameReq.equals(mp.getMockProfile().getName())))
      {
        if (commonDbMockData)
        {
          if (!useCommonMockdata)
          {
            return false;
          }
        }
        else
        {
          return false;
        }
      }
    }

    return true;
  }

  private boolean useCommonMockdata()
  {
    if (request.getMockProfileName() != null)
    {
      MockProfile dbMockProfile = getDao(MockProfileDao.class).getByName(request.getMockProfileName(), null);

      leaveOn(dbMockProfile == null, ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("mockprofile"));

      return Boolean.TRUE.equals(dbMockProfile.getUseCommonMocks());
    }

    return true;
  }

  private List<MockData> getMockDataSorted(List<MockData> mockData)
  {
    // first with mockProfile then without

    return mockData.stream()
        .sorted((md1, md2) -> getMockProfileFlagForSort(md1).compareTo(getMockProfileFlagForSort(md2)))
        .collect(Collectors.toList());
  }

  private Integer getMockProfileFlagForSort(MockData md)
  {
    return !md.getMockProfiles().isEmpty()
        ? 0
        : 1;
  }

  private boolean mockEnabled(Interface dbInterface, InterfaceMethod dbMethod, BaseData baseData)
  {
    if (Boolean.TRUE.equals(baseData.getMockActive()))
    {
      return true;
    }
    else if (Boolean.FALSE.equals(baseData.getMockActive()))
    {
      return false;
    }

    if (Boolean.TRUE.equals(dbInterface.getMockActive()))
    {
      return true;
    }
    else if (Boolean.FALSE.equals(dbInterface.getMockActive()))
    {
      return false;
    }

    return dbMethod != null && Boolean.TRUE.equals(dbMethod.getMockActive());
  }

  private void checkMockProfile()
  {
    leaveOn(request.getMockProfileName() != null
            && !getDao(MockProfileDao.class).existsByName(request.getMockProfileName(), null),
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("mockProfile " + request.getMockProfileName()));
  }

  private void checkRecordSession()
  {
    leaveOn(request.getRecordSessionID() != null
            && entityMgr.find(de.joergdev.mosy.backend.persistence.model.RecordSession.class,
                request.getRecordSessionID()) == null,
        ResponseCode.DATA_DOESNT_EXIST
            .withAddtitionalInfo("recordSession with id " + request.getRecordSessionID()));
  }

  private void doRouting(String requestContent, Interface dbInterface, BaseData baseData,
                         InterfaceMethod dbMethod, InterfaceType interfaceType)
  {
    String routingURL = dbInterface.getRoutingUrl();
    leaveOn(Utils.isEmpty(routingURL), ResponseCode.OPERATION_NOT_POSSIBLE.withAddtitionalInfo(
        "mock not enabled and no routing configured for interface " + dbInterface.getName()));

    if (!Utils.isEmpty(request.getRouteAddition()))
    {
      routingURL += request.getRouteAddition();
    }

    // route soap request
    if (InterfaceType.SOAP.equals(interfaceType))
    {
      mockResponse = SoapRouting.doRouting(routingURL, request.getAbsolutePath(), requestContent,
          request.getHttpHeaders().getRequestHeaders());
    }
    // route rest request
    else if (InterfaceType.REST.equals(interfaceType))
    {
      // TODO mosy route rest request
    }

    // if should be recorded then save
    if (recordRequestResponse(baseData, dbInterface, dbMethod, interfaceType))
    {
      saveRecord(requestContent, dbMethod);
    }
  }

  private void saveRecord(String requestContent, InterfaceMethod dbMethod)
  {
    Record apiRecord = new Record();
    apiRecord.setInterfaceMethod(new de.joergdev.mosy.api.model.InterfaceMethod());
    apiRecord.getInterfaceMethod().setInterfaceMethodId(dbMethod.getInterfaceMethodId());
    apiRecord.setRequestData(requestContent);
    apiRecord.setResponse(mockResponse);
    apiRecord.setCreatedAsLdt(LocalDateTime.now());

    Integer recordSessionID = request.getRecordSessionID();
    if (recordSessionID != null)
    {
      apiRecord.setRecordSession(new RecordSession(recordSessionID));
    }

    invokeSubBL(new Save(), apiRecord, new SaveResponse());
  }

  private boolean recordRequestResponse(BaseData baseData, Interface dbInterface, InterfaceMethod dbMethod,
                                        InterfaceType interfaceType)
  {
    // no record without method
    if (dbMethod == null)
    {
      return false;
    }

    // If recordSession set => Allways record
    if (request.getRecordSessionID() != null)
    {
      return true;
    }

    // Global config yes/no
    if (Boolean.TRUE.equals(baseData.getRecord()))
    {
      return true;
    }
    else if (Boolean.FALSE.equals(baseData.getRecord()))
    {
      return false;
    }

    // Interface global config yes/no
    RecordConfig rcInterface = dbInterface.getRecordConfig();
    if (rcInterface != null)
    {
      if (Boolean.TRUE.equals(rcInterface.getEnabled()))
      {
        return true;
      }
      else if (Boolean.FALSE.equals(rcInterface.getEnabled()))
      {
        return false;
      }
    }

    // Method global config yes/no
    RecordConfig rcMethod = getDao(RecordConfigDAO.class)
        .getByInterfaceMethodId(dbMethod.getInterfaceMethodId());
    if (rcMethod != null)
    {
      if (Boolean.TRUE.equals(rcMethod.getEnabled()))
      {
        return true;
      }
      else if (Boolean.FALSE.equals(rcMethod.getEnabled()))
      {
        return false;
      }
    }

    // Record by RecordConfig
    for (RecordConfig rc : dbMethod.getRecordConfig())
    {
      if (Boolean.TRUE.equals(rc.getEnabled())
          && dataMatchesRequestContent(interfaceType, rc.getRequestData()))
      {
        return true;
      }
    }

    return false;
  }

  private boolean dataMatchesRequestContent(InterfaceType interfaceType, String needle)
  {
    if (isXmlRequest(interfaceType))
    {
      if (MockServicesUtil.xmlContainsXml(request.getContent(), needle))
      {
        return true;
      }
    }
    else if (isJsonRequest(interfaceType))
    {
      if (MockServicesUtil.jsonContainsJson(request.getContent(), needle))
      {
        return true;
      }
    }
    // Plain
    else
    {
      if (MockServicesUtil.stringContainsString(request.getContent(), needle))
      {
        return true;
      }
    }

    return false;
  }

  private boolean isXmlRequest(InterfaceType interfaceType)
  {
    if (Arrays.asList(InterfaceType.SOAP, InterfaceType.CUSTOM_XML).contains(interfaceType))
    {
      return true;
    }

    if (!InterfaceType.CUSTOM_JSON.equals(interfaceType))
    {
      String reqContent = request.getContent().trim();

      if (reqContent.startsWith("<") && reqContent.endsWith(">"))
      {
        return true;
      }
    }

    return false;
  }

  private boolean isJsonRequest(InterfaceType interfaceType)
  {
    if (Arrays.asList(InterfaceType.CUSTOM_JSON).contains(interfaceType))
    {
      return true;
    }

    if (!Arrays.asList(InterfaceType.SOAP, InterfaceType.CUSTOM_XML).contains(interfaceType))
    {
      String reqContent = request.getContent().trim();

      if (reqContent.startsWith("{") && reqContent.endsWith("}"))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  protected void fillOutput()
  {
    response.setResponse(mockResponse);

    if (dbMethod != null)
    {
      de.joergdev.mosy.api.model.InterfaceMethod apiMethod = new de.joergdev.mosy.api.model.InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
      apiMethod.setName(dbMethod.getName());

      de.joergdev.mosy.api.model.Interface apiInterface = new de.joergdev.mosy.api.model.Interface();
      apiInterface.setInterfaceId(dbMethod.getMockInterface().getInterfaceId());
      apiInterface.setName(dbMethod.getMockInterface().getName());

      apiMethod.setMockInterface(apiInterface);
      response.setInterfaceMethod(apiMethod);
    }
  }
}