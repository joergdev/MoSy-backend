package com.github.joergdev.mosy.backend.bl.mockservices;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.model.InterfaceType;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.record.SaveResponse;
import com.github.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import com.github.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import com.github.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.bl.globalconfig.Load;
import com.github.joergdev.mosy.backend.bl.record.Save;
import com.github.joergdev.mosy.backend.bl.utils.PersistenceUtil;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import com.github.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import com.github.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import com.github.joergdev.mosy.backend.persistence.model.Interface;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.backend.persistence.model.MockData;
import com.github.joergdev.mosy.backend.persistence.model.MockSession;
import com.github.joergdev.mosy.backend.persistence.model.RecordConfig;
import com.github.joergdev.mosy.backend.util.MockServicesUtil;
import com.github.joergdev.mosy.backend.util.SoapRouting;
import com.github.joergdev.mosy.shared.Utils;

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

    leaveOn(com.github.joergdev.mosy.shared.Utils.isEmpty(request.getServicePathInterface()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("servicepath interface"));

    leaveOn(!request.isRouteOnly()
            && com.github.joergdev.mosy.shared.Utils.isEmpty(request.getServicePathMethod()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("servicepath method"));

    leaveOn(!request.isRouteOnly() && com.github.joergdev.mosy.shared.Utils.isEmpty(request.getContent()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("content"));
  }

  @Override
  protected void execute()
  {
    Interface dbInterface = PersistenceUtil.getDbInterfaceByServicePath(this,
        request.getServicePathInterface());
    InterfaceType interfaceType = InterfaceType.getById(dbInterface.getType().getInterfaceTypeId());
    dbMethod = PersistenceUtil.getDbInterfaceMethodByServicePath(this, request.getServicePathMethod(),
        dbInterface);

    checkMockSession();

    BaseData baseData = invokeSubBL(new Load(), null, new LoadBaseDataResponse()).getBaseData();

    if (mockEnabled(dbInterface, dbMethod, baseData) && !request.isRouteOnly())
    {
      tryMock(dbInterface, dbMethod, baseData, interfaceType);
    }
    // mock NOT enabled -> Routing
    else
    {
      if (interfaceType.routingPossible)
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
        if (interfaceType.routingPossible)
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
    return Boolean.TRUE.equals(baseData.getRoutingOnNoMockData())
           && Boolean.TRUE.equals(dbInterface.getRoutingOnNoMockData())
           && Boolean.TRUE.equals(dbMethod.getRoutingOnNoMockData());
  }

  private MockData getMockDataForRequest(InterfaceMethod dbMethod, InterfaceType interfaceType)
  {
    MockData dbMockDataFound = null;
    MockData dbMockDataMethodGlobal = null;

    for (MockData dbMockData : getMockDataSorted(dbMethod.getMockData()))
    {
      if (!Boolean.TRUE.equals(dbMockData.getActive())
          || (request.getMockSessionID() == null && dbMockData.getMockSession() != null)
          || (request.getMockSessionID() != null
              && (dbMockData.getMockSession() != null
                  && !dbMockData.getMockSession().getMockSessionID().equals(request.getMockSessionID()))))
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

  private List<MockData> getMockDataSorted(List<MockData> mockData)
  {
    // first with mockSession then without

    return mockData.stream()
        .sorted((md1, md2) -> getMockSessionIdForSort(md1).compareTo(getMockSessionIdForSort(md2)))
        .collect(Collectors.toList());
  }

  private Integer getMockSessionIdForSort(MockData md)
  {
    return md.getMockSession() != null
        ? md.getMockSession().getMockSessionID()
        : Integer.MAX_VALUE;
  }

  private boolean mockEnabled(Interface dbInterface, InterfaceMethod dbMethod, BaseData baseData)
  {
    return Boolean.TRUE.equals(baseData.getMockActive())
           && !Boolean.TRUE.equals(dbInterface.getMockDisabled()) && dbMethod != null
           && !Boolean.TRUE.equals(dbMethod.getMockDisabled());
  }

  private void checkMockSession()
  {
    leaveOn(request.getMockSessionID() != null
            && entityMgr.find(MockSession.class, request.getMockSessionID()) == null,
        ResponseCode.DATA_DOESNT_EXIST
            .withAddtitionalInfo("mocksession with id " + request.getMockSessionID()));
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
    apiRecord.setInterfaceMethod(new com.github.joergdev.mosy.api.model.InterfaceMethod());
    apiRecord.getInterfaceMethod().setInterfaceMethodId(dbMethod.getInterfaceMethodId());
    apiRecord.setRequestData(requestContent);
    apiRecord.setResponse(mockResponse);
    apiRecord.setCreatedAsLdt(LocalDateTime.now());

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

    if (Boolean.TRUE.equals(baseData.getRecord()))
    {
      return true;
    }

    RecordConfig rcInterface = dbInterface.getRecordConfig();
    if (rcInterface != null && Boolean.TRUE.equals(rcInterface.getEnabled()))
    {
      return true;
    }

    RecordConfig rcMethod = getDao(RecordConfigDAO.class)
        .getByInterfaceMethodId(dbMethod.getInterfaceMethodId());
    if (rcMethod != null && Boolean.TRUE.equals(rcMethod.getEnabled()))
    {
      return true;
    }

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
      com.github.joergdev.mosy.api.model.InterfaceMethod apiMethod = new com.github.joergdev.mosy.api.model.InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
      apiMethod.setName(dbMethod.getName());

      com.github.joergdev.mosy.api.model.Interface apiInterface = new com.github.joergdev.mosy.api.model.Interface();
      apiInterface.setInterfaceId(dbMethod.getMockInterface().getInterfaceId());
      apiInterface.setName(dbMethod.getMockInterface().getName());

      apiMethod.setMockInterface(apiInterface);
      response.setInterfaceMethod(apiMethod);
    }
  }
}