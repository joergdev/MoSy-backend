package de.joergdev.mosy.backend.bl._interface;

import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response._interface.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import de.joergdev.mosy.backend.persistence.model.RecordConfig;
import de.joergdev.mosy.backend.util.MockServicesUtil;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<Interface, SaveResponse>
{
  private de.joergdev.mosy.backend.persistence.model.Interface dbInterface;
  private Integer id;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getName()) || request.getName().length() > de.joergdev.mosy.backend.persistence.model.Interface.LENGTH_NAME,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("name"));

    leaveOn(request.getType() == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("type"));

    leaveOn(Boolean.TRUE.equals(request.getRoutingOnNoMockData()) && request.getType().directRoutingPossible && Utils.isEmpty(request.getRoutingUrl()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL"));

    leaveOn(!Utils.isEmpty(request.getRoutingUrl()) && !request.getType().directRoutingPossible,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL not allowed"));

    leaveOn(
        !Utils.isEmpty(request.getRoutingUrl()) && request.getRoutingUrl().length() > de.joergdev.mosy.backend.persistence.model.Interface.LENGTH_ROUTING_URL,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL"));

    String servicePath = request.getServicePath();
    if (!Utils.isEmpty(servicePath))
    {
      leaveOn(servicePath.length() > de.joergdev.mosy.backend.persistence.model.Interface.LENGTH_SERVICE_PATH,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("service path"));

      leaveOn(InterfaceType.REST.equals(request.getType()) && (servicePath.contains("{") || servicePath.contains("}")),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("service path"));
    }

    for (InterfaceMethod method : request.getMethods())
    {
      leaveOn(method.isDelete() && method.getInterfaceMethodId() == null,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("method delete set but not id. Name: " + method.getName()));

      leaveOn(!method.isDelete() && Utils.isEmpty(method.getName()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("method name may not be empty. ID: " + method.getInterfaceMethodId()));

      leaveOn(!Utils.isEmpty(method.getName()) && method.getName().length() > de.joergdev.mosy.backend.persistence.model.Interface.LENGTH_NAME,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("method name too long: " + method.getName()));

      leaveOn(!Utils.isEmpty(method.getServicePath())
              && method.getServicePath().length() > de.joergdev.mosy.backend.persistence.model.Interface.LENGTH_SERVICE_PATH,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("method service path too long: " + method.getName()));

      leaveOn(!method.isDelete() && InterfaceType.REST.equals(request.getType()) && method.getHttpMethod() == null,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("httpMethod"));
    }
  }

  @Override
  protected void execute()
  {
    id = request.getInterfaceId();

    de.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new de.joergdev.mosy.api.model.RecordConfig();

    dbInterface = loadOrCreateDbInterface(apiRecordConfig);

    formatServicePath();

    checkUniqueDataInterface();

    // Copy base data
    apiInterface2dbInterface();

    // save
    entityMgr.persist(dbInterface);
    entityMgr.flush();

    id = dbInterface.getInterfaceId();
    request.setInterfaceId(id);

    // save methods
    saveMethods();

    // Save record Config
    saveRecordConfig(apiRecordConfig, request, null, request.getRecord());
  }

  private void apiInterface2dbInterface()
  {
    ObjectUtils.copyValues(request, dbInterface, "type", "methods");

    if (request.getType() != null)
    {
      Integer reqTypID = request.getType().id;
      Integer dbTypID = dbInterface.getType() == null ? null : dbInterface.getType().getInterfaceTypeId();

      if (!reqTypID.equals(dbTypID))
      {
        dbInterface.setType(entityMgr.find(de.joergdev.mosy.backend.persistence.model.InterfaceType.class, reqTypID));
      }
    }
    else
    {
      dbInterface.setType(null);
    }
  }

  private void formatServicePath()
  {
    // Custom interfaces do not have a service path, for equal handling set service path = name
    if (InterfaceType.isCustomType(request.getType()))
    {
      formatServicePathCustom();
    }
    // Rest: Cut http(s):// from service path
    else if (InterfaceType.REST.equals(request.getType()))
    {
      formatServicePathRest();
    }
  }

  private void formatServicePathRest()
  {
    request.setServicePath(formatServicePath(request.getServicePath()));

    // Format servicePath of methods
    for (InterfaceMethod apiMethod : request.getMethods())
    {
      if (!apiMethod.isDelete())
      {
        apiMethod.setServicePath(formatServicePath(apiMethod.getServicePath()));
      }
    }
  }

  private String formatServicePath(String servicePath)
  {
    if (servicePath != null)
    {
      servicePath = servicePath.trim();

      if (servicePath.startsWith("http"))
      {
        servicePath = servicePath.substring(servicePath.indexOf("//") + 2);
      }

      if (servicePath.startsWith("/"))
      {
        servicePath = servicePath.substring(1);
      }

      if (servicePath.endsWith("/"))
      {
        servicePath = servicePath.substring(0, servicePath.length() - 1);
      }
    }

    return servicePath;
  }

  private void formatServicePathCustom()
  {
    request.setServicePath(request.getName());

    for (InterfaceMethod apiMethod : request.getMethods())
    {
      if (!apiMethod.isDelete())
      {
        apiMethod.setServicePath(apiMethod.getName());
      }
    }
  }

  private de.joergdev.mosy.backend.persistence.model.Interface loadOrCreateDbInterface(de.joergdev.mosy.api.model.RecordConfig apiRecordConfig)
  {
    de.joergdev.mosy.backend.persistence.model.Interface dbInterface;

    if (id != null)
    {
      dbInterface = findDbEntity(de.joergdev.mosy.backend.persistence.model.Interface.class, id,
          "no interface with id: " + id + ", name: " + request.getName());

      RecordConfig dbRecordConfig = dbInterface.getRecordConfig();
      if (dbRecordConfig != null)
      {
        apiRecordConfig.setRecordConfigId(dbRecordConfig.getRecordConfigId());
        apiRecordConfig.setEnabled(dbRecordConfig.getEnabled());
      }
    }
    else
    {
      dbInterface = new de.joergdev.mosy.backend.persistence.model.Interface();
    }

    return dbInterface;
  }

  private void checkUniqueDataInterface()
  {
    // check unique name
    leaveOn(getDao(InterfaceDao.class).existsByName(request.getName(), id),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("interface with name: " + request.getName()));

    // check unique service path
    leaveOn(request.getServicePath() != null
            && getDao(InterfaceDao.class).existsByServicePath(request.getServicePath(), InterfaceType.REST.equals(request.getType()), id),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("interface with service path: " + request.getServicePath()));
  }

  private void saveMethods()
  {
    for (InterfaceMethod apiMethod : request.getMethods())
    {
      de.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new de.joergdev.mosy.api.model.RecordConfig();

      de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = loadOrCreateDbMethod(dbInterface, apiMethod, apiRecordConfig);

      if (apiMethod.isDelete())
      {
        entityMgr.remove(dbMethod);
        entityMgr.flush();
      }
      else
      {
        checkUniqueDataInterfaceMethod(apiMethod);

        // base data
        ObjectUtils.copyValues(apiMethod, dbMethod, "mockInterface", "mockData", "recordConfigs", "httpMethod");
        setMethodHttpMethod(apiMethod, dbMethod);
        setMethodServicePathIntern(apiMethod, dbMethod);

        // save
        entityMgr.persist(dbMethod);
        entityMgr.flush();

        apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());

        // Save record Config
        saveRecordConfig(apiRecordConfig, null, apiMethod, apiMethod.getRecord());
      }
    }
  }

  private void setMethodServicePathIntern(InterfaceMethod apiMethod, de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod)
  {
    if (InterfaceType.REST.equals(request.getType()))
    {
      dbMethod.setServicePathIntern(MockServicesUtil.getUrlWithReplacedDynVars(apiMethod.getServicePath()));
    }
    else
    {
      dbMethod.setServicePathIntern(apiMethod.getServicePath());
    }
  }

  private void setMethodHttpMethod(InterfaceMethod apiMethod, de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod)
  {
    HttpMethod httpMethod = apiMethod.getHttpMethod();
    if (httpMethod != null)
    {
      dbMethod.setHttpMethod(httpMethod.name());
    }
  }

  private de.joergdev.mosy.backend.persistence.model.InterfaceMethod loadOrCreateDbMethod(de.joergdev.mosy.backend.persistence.model.Interface dbInterface,
                                                                                          InterfaceMethod apiMethod,
                                                                                          de.joergdev.mosy.api.model.RecordConfig apiRecordConfig)
  {
    de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod;

    if (apiMethod.getInterfaceMethodId() != null)
    {
      dbMethod = findDbEntity(de.joergdev.mosy.backend.persistence.model.InterfaceMethod.class, apiMethod.getInterfaceMethodId(),
          "interface method with id " + apiMethod.getInterfaceMethodId());

      // load via dao and not via model (lazy loading list) for not loading all
      // recordconfigs (performance)
      RecordConfig dbRecordConf = getDao(RecordConfigDAO.class).getByInterfaceMethodId(apiMethod.getInterfaceMethodId());
      if (dbRecordConf != null)
      {
        apiRecordConfig.setRecordConfigId(dbRecordConf.getRecordConfigId());
        apiRecordConfig.setEnabled(dbRecordConf.getEnabled());
      }
    }
    else
    {
      dbMethod = new de.joergdev.mosy.backend.persistence.model.InterfaceMethod();
      dbMethod.setMockInterface(dbInterface);
    }

    return dbMethod;
  }

  private void checkUniqueDataInterfaceMethod(InterfaceMethod apiMethod)
  {
    // check unique name
    leaveOn(getDao(InterfaceMethodDAO.class).existsByInterfaceIdName(id, apiMethod.getName(), apiMethod.getInterfaceMethodId()),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("interface method with name: " + apiMethod.getName()));

    // check unique service path
    String servicePath = apiMethod.getServicePath();
    leaveOn(servicePath != null
            && getDao(InterfaceMethodDAO.class).existsByInterfaceIdServicePath(id, servicePath, false, apiMethod.getHttpMethod(),
                apiMethod.getInterfaceMethodId()),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("interface method with service path: " + apiMethod.getServicePath()));
  }

  /**
   * saves interface / method global RecordConfig. Detailed RecordConfigs can be
   * saved/deleted via recordconfig.Save/Delete
   * 
   * @param apiRecordConfig
   * @param mockInterface
   * @param interfaceMethod
   * @param apiRecordEnabled
   */
  private void saveRecordConfig(de.joergdev.mosy.api.model.RecordConfig apiRecordConfig, Interface mockInterface, InterfaceMethod interfaceMethod,
                                Boolean apiRecordEnabled)
  {
    if (apiRecordEnabled == null)
    {
      if (apiRecordConfig.getRecordConfigId() != null)
      {
        // remove recordConfig from dbInterface, otherwise recordConfig cannot be deleted (if needed).
        // cause: recordConfig instance is part of dbInterface instance, hibernate dont like this then...
        dbInterface.setRecordConfig(null);

        invokeSubBL(new de.joergdev.mosy.backend.bl.recordconfig.Delete(), apiRecordConfig.getRecordConfigId(), new EmptyResponse());
      }
    }
    else
    {
      if (Utils.isEqual(apiRecordEnabled, apiRecordConfig.getEnabled()) && apiRecordConfig.getRecordConfigId() != null)
      {
        return;
      }

      apiRecordConfig.setEnabled(apiRecordEnabled);

      apiRecordConfig.setMockInterface(mockInterface);
      apiRecordConfig.setInterfaceMethod(interfaceMethod);

      invokeSubBL(new de.joergdev.mosy.backend.bl.recordconfig.Save(), apiRecordConfig, new de.joergdev.mosy.api.response.recordconfig.SaveResponse());
    }

  }

  @Override
  protected void fillOutput()
  {
    Interface apiInterfaceResp = new Interface();
    apiInterfaceResp.setInterfaceId(dbInterface.getInterfaceId());
    apiInterfaceResp.setServicePath(dbInterface.getServicePath());

    for (InterfaceMethod apiMethod : request.getMethods())
    {
      if (!apiMethod.isDelete())
      {
        InterfaceMethod apiMethodResp = new InterfaceMethod();
        apiMethodResp.setInterfaceMethodId(apiMethod.getInterfaceMethodId());
        apiMethodResp.setName(apiMethod.getName());
        apiMethodResp.setServicePath(apiMethod.getServicePath());

        apiInterfaceResp.getMethods().add(apiMethodResp);
      }
    }

    response.setInterface(apiInterfaceResp);
  }
}
