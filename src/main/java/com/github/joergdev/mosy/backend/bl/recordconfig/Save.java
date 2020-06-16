package com.github.joergdev.mosy.backend.bl.recordconfig;

import com.github.joergdev.mosy.api.model.RecordConfig;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.recordconfig.SaveResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<RecordConfig, SaveResponse>
{
  private Integer id;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    // if not intern: only recordConfigs for interface methods with requestData can be saved
    if (!isSubcall)
    {
      leaveOn(request.getInterfaceMethod() == null,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockInterface"));

      leaveOn(Utils.isEmpty(request.getTitle())
              || request.getTitle()
                  .length() > com.github.joergdev.mosy.backend.persistence.model.RecordConfig.LENGTH_TITLE,
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("title"));

      leaveOn(Utils.isEmpty(request.getRequestData()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("requestData"));
    }

    leaveOn(!Utils.isEmpty(request.getRequestData())
            && request.getRequestData()
                .length() > com.github.joergdev.mosy.backend.persistence.model.MockData.LENGTH_REQUEST,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("requestData too long"));

    leaveOn(request.getMockInterface() != null && request.getInterfaceMethod() != null,
        ResponseCode.INVALID_INPUT_PARAMS
            .withAddtitionalInfo("interface and interfaceMethod may not be set at same time"));

    leaveOn(request.getMockInterface() == null && request.getInterfaceMethod() == null
            && !Utils.isEmpty(request.getRequestData()),
        ResponseCode.INVALID_INPUT_PARAMS
            .withAddtitionalInfo("requestData must be null on global recordConfig"));

    leaveOn(request.getMockInterface() != null && request.getMockInterface().getInterfaceId() == null,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockInterfaceID"));

    leaveOn(
        request.getInterfaceMethod() != null && request.getInterfaceMethod().getInterfaceMethodId() == null,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interfaceMethodID"));
  }

  @Override
  protected void execute()
  {
    com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConfig = null;

    if (request.getRecordConfigId() != null)
    {
      dbRecordConfig = findDbEntity(com.github.joergdev.mosy.backend.persistence.model.RecordConfig.class,
          request.getRecordConfigId(), "recordConfig with id " + request.getRecordConfigId());
    }
    else
    {
      dbRecordConfig = new com.github.joergdev.mosy.backend.persistence.model.RecordConfig();
    }

    // check unique
    checkUniqueData(dbRecordConfig);

    // transfer values
    transferValues(dbRecordConfig);

    // save
    entityMgr.persist(dbRecordConfig);
    entityMgr.flush();

    id = dbRecordConfig.getRecordConfigId();
  }

  private void transferValues(com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConfig)
  {
    ObjectUtils.copyValues(request, dbRecordConfig, "mockInterface", "interfaceMethod");

    // Interface
    if (request.getMockInterface() != null)
    {
      Integer reqInterfaceID = request.getMockInterface().getInterfaceId();
      Integer dbInterfaceID = dbRecordConfig.getMockInterface() == null
          ? null
          : dbRecordConfig.getMockInterface().getInterfaceId();

      if (!reqInterfaceID.equals(dbInterfaceID))
      {
        dbRecordConfig
            .setMockInterface(findDbEntity(com.github.joergdev.mosy.backend.persistence.model.Interface.class,
                reqInterfaceID, "interface with id: " + reqInterfaceID));
      }
    }
    else
    {
      dbRecordConfig.setMockInterface(null);
    }

    // Method
    if (request.getInterfaceMethod() != null)
    {
      Integer reqMethodID = request.getInterfaceMethod().getInterfaceMethodId();
      Integer dbMethodID = dbRecordConfig.getInterfaceMethod() == null
          ? null
          : dbRecordConfig.getInterfaceMethod().getInterfaceMethodId();

      if (!reqMethodID.equals(dbMethodID))
      {
        dbRecordConfig.setInterfaceMethod(
            findDbEntity(com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod.class,
                reqMethodID, "interface-method with id: " + reqMethodID));
      }
    }
    else
    {
      dbRecordConfig.setInterfaceMethod(null);
    }
  }

  private void checkUniqueData(com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConfig)
  {
    // check unique name
    if (request.getInterfaceMethod() != null && request.getTitle() != null)
    {
      leaveOn(
          getDao(RecordConfigDAO.class).existsByTitle(request.getInterfaceMethod().getInterfaceMethodId(),
              request.getTitle(), request.getRecordConfigId()),
          ResponseCode.DATA_ALREADY_EXISTS
              .withAddtitionalInfo("recordConfig with title: " + request.getTitle()));
    }

    // global recordConfig
    if (request.getMockInterface() == null && request.getInterfaceMethod() == null)
    {
      if (dbRecordConfig == null)
      {
        com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbExisting = getDao(
            RecordConfigDAO.class).getGlobal();

        leaveOn(dbExisting != null && !dbExisting.getRecordConfigId().equals(request.getRecordConfigId()),
            ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo(
                "global recordConfig already exists with id: " + dbExisting.getRecordConfigId()));
      }
      // dbRecordConfig != null
      else
      {
        leaveOn(dbRecordConfig.getMockInterface() != null || dbRecordConfig.getInterfaceMethod() != null,
            ResponseCode.OPERATION_FAILED_INFO.withAddtitionalInfo("cannot change recordConfig to global"));
      }
    }
    // interface global
    else if (request.getMockInterface() != null && request.getInterfaceMethod() == null)
    {
      if (dbRecordConfig == null || dbRecordConfig.getRecordConfigId() == null)
      {
        com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbExisting = getDao(
            RecordConfigDAO.class).getByInterfaceId(request.getMockInterface().getInterfaceId());

        leaveOn(dbExisting != null && !dbExisting.getRecordConfigId().equals(request.getRecordConfigId()),
            () -> ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo(
                "interface global recordConfig already exists with id: " + dbExisting.getRecordConfigId()));
      }
      // dbRecordConfig != null
      else
      {
        leaveOn(dbRecordConfig.getMockInterface() == null
                || !dbRecordConfig.getMockInterface().getInterfaceId()
                    .equals(request.getMockInterface().getInterfaceId())
                || dbRecordConfig.getInterfaceMethod() != null,
            ResponseCode.OPERATION_FAILED_INFO
                .withAddtitionalInfo("cannot change recordConfig to interface global for interface with id "
                                     + request.getMockInterface().getInterfaceId()));
      }
    }
    else if (request.getMockInterface() == null && request.getInterfaceMethod() != null)
    {
      // interface method global
      if (Utils.isEmpty(request.getRequestData()))
      {
        if (dbRecordConfig == null || dbRecordConfig.getRecordConfigId() == null)
        {
          com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbExisting = getDao(
              RecordConfigDAO.class)
                  .getByInterfaceMethodId(request.getInterfaceMethod().getInterfaceMethodId());

          leaveOn(dbExisting != null && !dbExisting.getRecordConfigId().equals(request.getRecordConfigId()),
              () -> ResponseCode.DATA_ALREADY_EXISTS
                  .withAddtitionalInfo("interfaceMethod global recordConfig already exists with id: "
                                       + dbExisting.getRecordConfigId()));
        }
        // dbRecordConfig != null
        else
        {
          leaveOn(dbRecordConfig.getMockInterface() != null || dbRecordConfig.getInterfaceMethod() == null
                  || !dbRecordConfig.getInterfaceMethod().getInterfaceMethodId()
                      .equals(request.getInterfaceMethod().getInterfaceMethodId())
                  || !Utils.isEmpty(dbRecordConfig.getRequestData()),
              ResponseCode.OPERATION_FAILED_INFO
                  .withAddtitionalInfo(
                      "cannot change recordConfig to interfaceMethod global for interfaceMethod with id: "
                                       + request.getInterfaceMethod().getInterfaceMethodId()));
        }
      }
      // interface method with request data
      else
      {
        // check unique requestData for method
        leaveOn(
            getDao(RecordConfigDAO.class).getByInterfaceMethodIdRequestData(
                request.getInterfaceMethod().getInterfaceMethodId(), request.getRequestData(),
                request.getRecordConfigId()) != null,
            ResponseCode.DATA_ALREADY_EXISTS
                .withAddtitionalInfo("recordConfig with requestData for interfaceMethodID: "
                                     + request.getInterfaceMethod().getInterfaceMethodId()));
      }
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setRecordConfigID(id);
  }
}