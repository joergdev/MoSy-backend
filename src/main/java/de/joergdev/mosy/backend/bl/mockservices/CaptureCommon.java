package de.joergdev.mosy.backend.bl.mockservices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.model.PathParam;
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
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.MockData;
import de.joergdev.mosy.backend.persistence.model.MockDataPathParam;
import de.joergdev.mosy.backend.persistence.model.MockDataUrlArgument;
import de.joergdev.mosy.backend.persistence.model.MockProfile;
import de.joergdev.mosy.backend.persistence.model.RecordConfig;
import de.joergdev.mosy.backend.util.HttpRouting;
import de.joergdev.mosy.backend.util.MockServicesUtil;
import de.joergdev.mosy.shared.Utils;

public class CaptureCommon extends AbstractBL<CaptureCommonRequest, CaptureCommonResponse>
{
  private MultivaluedMap<String, String> requestHeader;

  private String mockResponse;
  private Integer mockResponseHttpCode;
  private MultivaluedMap<String, Object> mockResponseHeaders;

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

    leaveOn(!request.isRouteOnly() && de.joergdev.mosy.shared.Utils.isEmpty(request.getServicePathMethod()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("servicepath method"));

    requestHeader = request.getHttpHeaders().getRequestHeaders();
    leaveOn(requestHeader == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request header"));
  }

  @Override
  protected void execute()
  {
    TenancyUtils.setInternTokenForTenancy(this, requestHeader);

    Interface dbInterface = PersistenceUtil.getDbInterfaceByServicePath(this, request.getServicePathInterface(), false);
    InterfaceType interfaceType = InterfaceType.getById(dbInterface.getType().getInterfaceTypeId());

    dbMethod = PersistenceUtil.getDbInterfaceMethodByServicePath(this, request.getServicePathMethod(), true, request.getHttpMethod(), dbInterface, false);

    // request must be set (except if rest)
    leaveOn(!InterfaceType.REST.equals(interfaceType) && !request.isRouteOnly() && de.joergdev.mosy.shared.Utils.isEmpty(request.getContent()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("content"));

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

  private void tryMock(Interface dbInterface, InterfaceMethod dbMethod, BaseData baseData, InterfaceType interfaceType)
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
        leaveOn(true, ResponseCode.OPERATION_FAILED_ERROR.withAddtitionalInfo(
            "no mockdata for interface " + dbInterface.getName() + ", method " + dbMethod.getName() + ", request " + request.getContent()));
      }
    }
    // return mock response
    else
    {
      mockResponse = dbMockDataFound.getResponse();
      mockResponseHttpCode = dbMockDataFound.getHttpReturnCode();

      getDao(MockDataDAO.class).increaseCountCalls(dbMockDataFound.getMockDataId());

      sleepOnDelaySet(dbMockDataFound);
    }
  }

  private void sleepOnDelaySet(MockData dbMockDataFound)
  {
    if (dbMockDataFound.getDelay() != null)
    {
      try
      {
        Thread.sleep(dbMockDataFound.getDelay());
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();

        throw new IllegalStateException(ex);
      }
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

      if (Utils.isEmpty(dbMockData.getRequest()) && dbMockData.getPathParams().isEmpty() && dbMockData.getUrlArguments().isEmpty())
      {
        dbMockDataMethodGlobal = dbMockData;
      }
      else
      {
        if (dataMatchesRequestContent(interfaceType, dbMockData.getRequest(), getPathParamsMap(dbMockData.getPathParams()),
            getUrlArgumentsMap(dbMockData.getUrlArguments())))
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

  private Map<String, String> getPathParamsMap(List<MockDataPathParam> dbMockDataPathParams)
  {
    Map<String, String> mapPathParams = new HashMap<>();

    for (MockDataPathParam dbPathParam : dbMockDataPathParams)
    {
      mapPathParams.put(dbPathParam.getKey(), dbPathParam.getValue());
    }

    return mapPathParams;
  }

  private Map<String, String> getUrlArgumentsMap(List<MockDataUrlArgument> dbMockDataUrlArguments)
  {
    Map<String, String> mapUrlArguments = new HashMap<>();

    for (MockDataUrlArgument dbUrlArg : dbMockDataUrlArguments)
    {
      mapUrlArguments.put(dbUrlArg.getKey(), dbUrlArg.getValue());
    }

    return mapUrlArguments;
  }

  private boolean isMockDataRelevant(MockData dbMockData, boolean useCommonMockdata)
  {
    if (!Boolean.TRUE.equals(dbMockData.getActive()))
    {
      return false;
    }

    HttpMethod httpMethodRequest = request.getHttpMethod();
    if (httpMethodRequest != null && !httpMethodRequest.toString().equals(dbMockData.getInterfaceMethod().getHttpMethod()))
    {
      return false;
    }

    boolean commonDbMockData = Boolean.TRUE.equals(dbMockData.getCommon()) || dbMockData.getMockProfiles().isEmpty();

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
      if (!dbMockData.getMockProfiles().stream().anyMatch(mp -> mockProfileNameReq.equalsIgnoreCase(mp.getMockProfile().getName())))
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
    return mockData.stream().sorted(getComparatorMockData()).collect(Collectors.toList());
  }

  private Comparator<MockData> getComparatorMockData()
  {
    return new Comparator<MockData>()
    {
      @Override
      public int compare(MockData md1, MockData md2)
      {
        // first sort by mockProfile exists => if exists then first
        if (!md1.getMockProfiles().isEmpty() && md2.getMockProfiles().isEmpty())
        {
          return -1;
        }
        else if (md1.getMockProfiles().isEmpty() && !md2.getMockProfiles().isEmpty())
        {
          return 1;
        }
        else
        {
          // sort by path variables

          // example:
          //      mockdata1 api/cars/123
          //      mockdata2 api/cars/{x}
          //
          //      path = cars/123 => mockdata1
          //      path = cars/444 => mockdata2

          // example2:
          // => the mockdata with the last dynamic path param ({..}) must be sorted on top because its the most restrictive
          //  mockdata1: api/cars/123     /parts/{partID}
          //  mockdata2: api/cars/{cartID}/parts/456
          //  mockdata3: api/cars/{cartID}/parts/{partID}
          //
          //  path =  cars/123/parts/999 => mockdata1
          //          cars/777/parts/456 -> mockdata2
          //          cars/321/oarts/987 -> mockdata3

          String servicePathMethod = md1.getInterfaceMethod().getServicePath();

          String servicePathMd1 = getServicePathWithPathParams(servicePathMethod, md1.getPathParams());
          String servicePathMd2 = getServicePathWithPathParams(servicePathMethod, md2.getPathParams());

          int idx1WildcardParam = servicePathMd1.indexOf("{");
          int idx2WildcardParam = servicePathMd2.indexOf("{");

          if (idx1WildcardParam == idx2WildcardParam)
          {
            // sort by urlargs, the request with the most args must be sorted on top because its the most restrictive
            int cntUrlArguments1 = Utils.nvlCollection(md1.getUrlArguments()).size();
            int cntUrlArguments2 = Utils.nvlCollection(md2.getUrlArguments()).size();

            if (cntUrlArguments1 == cntUrlArguments2)
            {
              // sort by length request
              // the request with the longest string must be sorted on top because its the most restrictive

              if (Utils.nvl(md1.getRequest()).length() > Utils.nvl(md2.getRequest()).length())
              {
                return -1;
              }
              // length1 <= length2
              else
              {
                return 1;
              }
            }
            else if (cntUrlArguments1 > cntUrlArguments2)
            {
              return -1;
            }
            // cntUrlArguments1 < cntUrlArguments2
            else
            {
              return 1;
            }
          }
          else if (idx1WildcardParam < 0 || idx1WildcardParam > idx2WildcardParam)
          {
            return -1;
          }
          else // idx2WildcardParam < 0 || idx2WildcardParam > idx1WildcardParam
          {
            return 1;
          }
        }
      }
    };
  }

  private String getServicePathWithPathParams(String servicePath, List<MockDataPathParam> pathParams)
  {
    for (MockDataPathParam pathParam : pathParams)
    {
      servicePath = servicePath.replace("{" + pathParam.getKey() + "}", pathParam.getValue());
    }

    return servicePath;
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
    leaveOn(request.getMockProfileName() != null && !getDao(MockProfileDao.class).existsByName(request.getMockProfileName(), null),
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("mockProfile " + request.getMockProfileName()));
  }

  private void checkRecordSession()
  {
    leaveOn(request.getRecordSessionID() != null
            && entityMgr.find(de.joergdev.mosy.backend.persistence.model.RecordSession.class, request.getRecordSessionID()) == null,
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("recordSession with id " + request.getRecordSessionID()));
  }

  private void doRouting(String requestContent, Interface dbInterface, BaseData baseData, InterfaceMethod dbMethod, InterfaceType interfaceType)
  {
    String routingURL = dbInterface.getRoutingUrl();
    leaveOn(Utils.isEmpty(routingURL),
        ResponseCode.OPERATION_NOT_POSSIBLE.withAddtitionalInfo("mock not enabled and no routing configured for interface " + dbInterface.getName()));

    if (!Utils.isEmpty(request.getRouteAddition()))
    {
      routingURL += request.getRouteAddition();
    }

    Response response = getRoutingResponse(requestContent, interfaceType, routingURL);

    mockResponse = (String) response.getEntity();
    mockResponseHttpCode = response.getStatus();
    mockResponseHeaders = filterResponseHeaders(response.getHeaders());

    // if should be recorded then save
    if (recordRequestResponse(baseData, dbInterface, dbMethod, interfaceType))
    {
      saveRecord(requestContent, interfaceType, dbMethod);
    }
  }

  private Response getRoutingResponse(String requestContent, InterfaceType interfaceType, String routingURL)
  {
    // route soap request
    if (InterfaceType.SOAP.equals(interfaceType))
    {
      return HttpRouting.doRouting(routingURL, request.getAbsolutePath(), requestContent, HttpMethod.POST, requestHeader, true, false);
    }
    // route rest request
    else if (InterfaceType.REST.equals(interfaceType))
    {
      return HttpRouting.doRouting(routingURL, request.getAbsolutePath(), requestContent, request.getHttpMethod(), requestHeader, false, false);
    }

    throw new IllegalArgumentException("routing not possible for interfaceType " + interfaceType);
  }

  private void saveRecord(String requestContent, InterfaceType interfaceType, InterfaceMethod dbMethod)
  {
    Record apiRecord = new Record();
    apiRecord.setInterfaceMethod(new de.joergdev.mosy.api.model.InterfaceMethod());
    apiRecord.getInterfaceMethod().setInterfaceMethodId(dbMethod.getInterfaceMethodId());
    apiRecord.getPathParams().addAll(getRequestPathParams(interfaceType, dbMethod));
    apiRecord.getUrlArguments().addAll(request.getUrlArguments());
    apiRecord.setRequestData(requestContent);
    apiRecord.setHttpReturnCode(mockResponseHttpCode);
    apiRecord.setResponse(mockResponse);
    apiRecord.setCreatedAsLdt(LocalDateTime.now());

    Integer recordSessionID = request.getRecordSessionID();
    if (recordSessionID != null)
    {
      apiRecord.setRecordSession(new RecordSession(recordSessionID));
    }

    invokeSubBL(new Save(), apiRecord, new SaveResponse());
  }

  private Collection<PathParam> getRequestPathParams(InterfaceType interfaceType, InterfaceMethod dbMethod2)
  {
    Collection<PathParam> pathParams = new ArrayList<>();

    if (InterfaceType.REST.equals(interfaceType))
    {
      String[] pathPartsMethod = dbMethod.getServicePath().split("/"); // cars/{cid}/parts/{pid}
      String[] pathPartsRequest = request.getServicePathMethod().split("/"); // cars/123/parts/456

      for (int x = 0; x < pathPartsMethod.length && x < pathPartsRequest.length; x++)
      {
        String pathPartMethod = pathPartsMethod[x];
        if (pathPartMethod.startsWith("{") && pathPartMethod.endsWith("}"))
        {
          pathParams.add(new PathParam(pathPartMethod.substring(1, pathPartMethod.length() - 1), pathPartsRequest[x]));
        }
      }
    }

    return pathParams;
  }

  private boolean recordRequestResponse(BaseData baseData, Interface dbInterface, InterfaceMethod dbMethod, InterfaceType interfaceType)
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
    RecordConfig rcMethod = getDao(RecordConfigDAO.class).getByInterfaceMethodId(dbMethod.getInterfaceMethodId());
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
      if (Boolean.TRUE.equals(rc.getEnabled()) && dataMatchesRequestContent(interfaceType, rc.getRequestData(), null, null))
      {
        return true;
      }
    }

    return false;
  }

  private boolean dataMatchesRequestContent(InterfaceType interfaceType, String needle, Map<String, String> pathParams, Map<String, String> urlArguments)
  {
    // first check if pathParams (not) match (if set)
    if (!Utils.nvlMap(pathParams).isEmpty())
    {
      String servicePathMethod = dbMethod.getServicePath();

      for (Entry<String, String> pathParam : pathParams.entrySet())
      {
        servicePathMethod = servicePathMethod.replace("{" + pathParam.getKey() + "}", pathParam.getValue());
      }

      servicePathMethod = replaceInBracketsWith(servicePathMethod, ".*");

      Pattern servicePathPattern = Pattern.compile(servicePathMethod);

      if (!servicePathPattern.matcher(request.getServicePathMethod()).matches())
      {
        return false;
      }
    }

    // second check url arguments
    if (!Utils.nvlMap(urlArguments).isEmpty())
    {
      for (Entry<String, String> urlArgEntry : urlArguments.entrySet())
      {
        if (request.getUrlArguments().stream().noneMatch(ua -> ua.getKey().equals(urlArgEntry.getKey()) && ua.getValue().equals(urlArgEntry.getValue())))
        {
          return false;
        }
      }
    }

    if (Utils.isEmpty(needle))
    {
      return true;
    }

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

  public static String replaceInBracketsWith(String input, String toReplace)
  {
    StringBuilder result = new StringBuilder();
    boolean inBrackets = false;

    for (char c : input.toCharArray())
    {
      if (c == '{')
      {
        inBrackets = true;
        result.append(toReplace);
      }
      else if (c == '}' && inBrackets)
      {
        inBrackets = false;
      }
      else if (!inBrackets)
      {
        result.append(c);
      }
    }

    return result.toString();
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
    response.setResponseHttpCode(mockResponseHttpCode);
    response.setResponseHeaders(mockResponseHeaders);

    if (dbMethod != null)
    {
      de.joergdev.mosy.api.model.InterfaceMethod apiMethod = new de.joergdev.mosy.api.model.InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
      apiMethod.setName(dbMethod.getName());

      de.joergdev.mosy.api.model.Interface apiInterface = new de.joergdev.mosy.api.model.Interface();
      apiInterface.setInterfaceId(dbMethod.getMockInterface().getInterfaceId());
      apiInterface.setName(dbMethod.getMockInterface().getName());

      apiMethod.setMockInterfaceData(apiInterface);
      response.setInterfaceMethod(apiMethod);
    }
  }

  private MultivaluedMap<String, Object> filterResponseHeaders(MultivaluedMap<String, Object> mockResponseHeadersTmp)
  {
    Iterator<Entry<String, List<Object>>> it = mockResponseHeadersTmp.entrySet().iterator();
    while (it.hasNext())
    {
      Entry<String, List<Object>> e = it.next();

      if ("Transfer-encoding".equals(e.getKey()))
      {
        if (e.getValue().remove("chunked") && e.getValue().isEmpty())
        {
          it.remove();
        }
      }
    }

    return mockResponseHeadersTmp;
  }
}
