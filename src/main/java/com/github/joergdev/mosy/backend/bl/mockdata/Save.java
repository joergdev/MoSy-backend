package com.github.joergdev.mosy.backend.bl.mockdata;

import java.time.LocalDateTime;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.mockdata.SaveResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.bl.utils.PersistenceUtil;
import com.github.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.backend.persistence.model.MockSession;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<MockData, SaveResponse>
{
  private com.github.joergdev.mosy.api.model.InterfaceMethod apiInterfaceMethodRequest = null;
  private Interface apiInterfaceRequest = null;

  private com.github.joergdev.mosy.backend.persistence.model.MockData dbMockData;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getTitle())
            || request.getTitle()
                .length() > com.github.joergdev.mosy.backend.persistence.model.MockData.LENGTH_TITLE,
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
                .length() > com.github.joergdev.mosy.backend.persistence.model.MockData.LENGTH_REQUEST,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getResponse()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response"));

    leaveOn(request.getMockSession() != null && request.getMockSession().getMockSessionID() == null,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mocksession"));
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

    MockSession dbMockSession = null;
    if (request.getMockSession() != null)
    {
      dbMockSession = findDbEntity(MockSession.class, request.getMockSession().getMockSessionID(),
          "mocksession with id: " + request.getMockSession().getMockSessionID());
    }

    // check title unique
    checkUniqueData();

    // transfer values
    ObjectUtils.copyValues(request, dbMockData, "interfaceMethod", "mockSession", "created", "countCalls");
    dbMockData.setInterfaceMethod(dbMethod);
    dbMockData.setMockSession(dbMockSession);

    // save
    entityMgr.persist(dbMockData);
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
      return PersistenceUtil.getDbInterfaceMethodByServicePaths(this, apiInterfaceRequest.getName(),
          apiInterfaceMethodRequest.getName());
    }
  }

  private void loadOrCreateDbMockData()
  {
    if (request.getMockDataId() != null)
    {
      dbMockData = findDbEntity(com.github.joergdev.mosy.backend.persistence.model.MockData.class,
          request.getMockDataId(), "mockData with id " + request.getMockDataId());
    }
    else
    {
      dbMockData = new com.github.joergdev.mosy.backend.persistence.model.MockData();
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

    response.setMockData(apiMockDateResponse);
  }
}