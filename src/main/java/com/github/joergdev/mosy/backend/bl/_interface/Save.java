package com.github.joergdev.mosy.backend.bl._interface;

import java.util.HashMap;
import java.util.Map;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.InterfaceType;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response._interface.SaveResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import com.github.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import com.github.joergdev.mosy.backend.persistence.model.RecordConfig;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<Interface, SaveResponse>
{
  private Integer id;
  private final Map<String, Integer> method_ids = new HashMap<>();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getName())
            || request.getName()
                .length() > com.github.joergdev.mosy.backend.persistence.model.Interface.LENGTH_NAME,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("name"));

    leaveOn(request.getType() == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("type"));

    leaveOn(Boolean.TRUE.equals(request.getRoutingOnNoMockData()) && request.getType().directRoutingPossible
            && Utils.isEmpty(request.getRoutingUrl()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL"));

    leaveOn(!Utils.isEmpty(request.getRoutingUrl()) && !request.getType().directRoutingPossible,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL not allowed"));

    leaveOn(!Utils.isEmpty(request.getRoutingUrl())
            && request.getRoutingUrl()
                .length() > com.github.joergdev.mosy.backend.persistence.model.Interface.LENGTH_ROUTING_URL,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("routingURL"));

    leaveOn(!Utils.isEmpty(request.getServicePath())
            && request.getServicePath()
                .length() > com.github.joergdev.mosy.backend.persistence.model.Interface.LENGTH_SERVICE_PATH,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("service path"));

    for (InterfaceMethod method : request.getMethods())
    {
      leaveOn(method.isDelete() && method.getInterfaceMethodId() == null, ResponseCode.INVALID_INPUT_PARAMS
          .withAddtitionalInfo("method delete set but not id. Name: " + method.getName()));

      leaveOn(!method.isDelete() && Utils.isEmpty(method.getName()), ResponseCode.INVALID_INPUT_PARAMS
          .withAddtitionalInfo("method name may not be empty. ID: " + method.getInterfaceMethodId()));

      leaveOn(!Utils.isEmpty(method.getName())
              && method.getName()
                  .length() > com.github.joergdev.mosy.backend.persistence.model.Interface.LENGTH_NAME,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("method name too long: " + method.getName()));

      leaveOn(
          !Utils.isEmpty(method.getServicePath()) && method.getServicePath()
              .length() > com.github.joergdev.mosy.backend.persistence.model.Interface.LENGTH_SERVICE_PATH,
          ResponseCode.INVALID_INPUT_PARAMS
              .withAddtitionalInfo("method service path too long: " + method.getName()));
    }
  }

  @Override
  protected void execute()
  {
    id = request.getInterfaceId();

    com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new com.github.joergdev.mosy.api.model.RecordConfig();

    com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface = loadOrCreateDbInterface(
        apiRecordConfig);

    checkUniqueDataInterface();

    // Custom interfaces do not have a service path, for equal handling set service path = name
    setServicePathsForCustomInterfaceToName();

    // Copy base data
    apiInterface2dbInterface(dbInterface);

    // save
    entityMgr.persist(dbInterface);
    entityMgr.flush();

    id = dbInterface.getInterfaceId();
    request.setInterfaceId(id);

    // save methods
    saveMethods(dbInterface);

    // Save record Config
    saveRecordConfig(apiRecordConfig, request, null, request.getRecord());
  }

  private void apiInterface2dbInterface(com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface)
  {
    ObjectUtils.copyValues(request, dbInterface, "type", "methods");

    if (request.getType() != null)
    {
      Integer reqTypID = request.getType().id;
      Integer dbTypID = dbInterface.getType() == null
          ? null
          : dbInterface.getType().getInterfaceTypeId();

      if (!reqTypID.equals(dbTypID))
      {
        dbInterface.setType(
            entityMgr.find(com.github.joergdev.mosy.backend.persistence.model.InterfaceType.class, reqTypID));
      }
    }
    else
    {
      dbInterface.setType(null);
    }
  }

  private void setServicePathsForCustomInterfaceToName()
  {
    if (InterfaceType.isCustomType(request.getType()))
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
  }

  private com.github.joergdev.mosy.backend.persistence.model.Interface loadOrCreateDbInterface(com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig)
  {
    com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface;

    if (id != null)
    {
      dbInterface = findDbEntity(com.github.joergdev.mosy.backend.persistence.model.Interface.class, id,
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
      dbInterface = new com.github.joergdev.mosy.backend.persistence.model.Interface();
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
            && getDao(InterfaceDao.class).existsByServicePath(request.getServicePath(), id),
        ResponseCode.DATA_ALREADY_EXISTS
            .withAddtitionalInfo("interface with service path: " + request.getServicePath()));
  }

  private void saveMethods(com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface)
  {
    for (InterfaceMethod apiMethod : request.getMethods())
    {
      com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new com.github.joergdev.mosy.api.model.RecordConfig();

      com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = loadOrCreateDbMethod(
          dbInterface, apiMethod, apiRecordConfig);

      if (apiMethod.isDelete())
      {
        entityMgr.remove(dbMethod);
      }
      else
      {
        checkUniqueDataInterfaceMethod(apiMethod);

        // base data
        ObjectUtils.copyValues(apiMethod, dbMethod, "mockInterface", "mockData", "recordConfigs");

        // save
        entityMgr.persist(dbMethod);
        entityMgr.flush();

        method_ids.put(dbMethod.getName(), dbMethod.getInterfaceMethodId());
        apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());

        // Save record Config
        saveRecordConfig(apiRecordConfig, null, apiMethod, apiMethod.getRecord());
      }
    }
  }

  private com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod loadOrCreateDbMethod(com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface,
                                                                                                  InterfaceMethod apiMethod,
                                                                                                  com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig)
  {
    com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod;

    if (apiMethod.getInterfaceMethodId() != null)
    {
      dbMethod = findDbEntity(com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod.class,
          apiMethod.getInterfaceMethodId(), "interface method with id " + apiMethod.getInterfaceMethodId());

      // load via dao and not via model (lazy loading list) for not loading all recordconfigs (performance)
      RecordConfig dbRecordConf = getDao(RecordConfigDAO.class)
          .getByInterfaceMethodId(apiMethod.getInterfaceMethodId());
      if (dbRecordConf != null)
      {
        apiRecordConfig.setRecordConfigId(dbRecordConf.getRecordConfigId());
        apiRecordConfig.setEnabled(dbRecordConf.getEnabled());
      }
    }
    else
    {
      dbMethod = new com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod();
      dbMethod.setMockInterface(dbInterface);
    }

    return dbMethod;
  }

  private void checkUniqueDataInterfaceMethod(InterfaceMethod apiMethod)
  {
    // check unique name
    leaveOn(
        getDao(InterfaceMethodDAO.class).existsByInterfaceIdName(id, apiMethod.getName(),
            apiMethod.getInterfaceMethodId()),
        ResponseCode.DATA_ALREADY_EXISTS
            .withAddtitionalInfo("interface method with name: " + apiMethod.getName()));

    // check unique service path
    leaveOn(apiMethod.getServicePath() != null
            && getDao(InterfaceMethodDAO.class).existsByInterfaceIdServicePath(id, apiMethod.getServicePath(),
                apiMethod.getInterfaceMethodId()),
        ResponseCode.DATA_ALREADY_EXISTS
            .withAddtitionalInfo("interface method with service path: " + apiMethod.getServicePath()));
  }

  /**
   * saves interface / method global RecordConfig.
   * Detailed RecordConfigs can be saved/deleted via recordconfig.Save/Delete
   * 
   * @param apiRecordConfig
   * @param mockInterface
   * @param interfaceMethod
   * @param apiRecordEnabled
   */
  private void saveRecordConfig(com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig,
                                Interface mockInterface, InterfaceMethod interfaceMethod,
                                Boolean apiRecordEnabled)
  {
    if (apiRecordEnabled == null)
    {
      if (apiRecordConfig.getRecordConfigId() != null)
      {
        invokeSubBL(new com.github.joergdev.mosy.backend.bl.recordconfig.Delete(),
            apiRecordConfig.getRecordConfigId(), new EmptyResponse());
      }
    }
    else
    {
      if (Utils.isEqual(apiRecordEnabled, apiRecordConfig.getEnabled())
          && apiRecordConfig.getRecordConfigId() != null)
      {
        return;
      }

      apiRecordConfig.setEnabled(apiRecordEnabled);

      apiRecordConfig.setMockInterface(mockInterface);
      apiRecordConfig.setInterfaceMethod(interfaceMethod);

      invokeSubBL(new com.github.joergdev.mosy.backend.bl.recordconfig.Save(), apiRecordConfig,
          new com.github.joergdev.mosy.api.response.recordconfig.SaveResponse());
    }

  }

  @Override
  protected void fillOutput()
  {
    Interface apiInterfaceResp = new Interface();
    apiInterfaceResp.setInterfaceId(id);

    for (String methodName : method_ids.keySet())
    {
      InterfaceMethod apiMethodResp = new InterfaceMethod();
      apiMethodResp.setName(methodName);
      apiMethodResp.setInterfaceMethodId(method_ids.get(methodName));

      apiInterfaceResp.getMethods().add(apiMethodResp);
    }

    response.setInterface(apiInterfaceResp);
  }
}