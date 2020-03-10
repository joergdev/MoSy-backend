package com.github.joergdev.mosy.backend.bl.record;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.record.LoadAllResponse;
import com.github.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.RecordDAO;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class LoadAll extends AbstractBL<LoadAllRequest, LoadAllResponse>
{
  private final List<Record> apiRecords = new ArrayList<>();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    InterfaceMethod interfaceMethod = request.getInterfaceMethod();
    if (interfaceMethod != null)
    {
      leaveOn(interfaceMethod.getInterfaceMethodId() == null
              || !Utils.isPositive(interfaceMethod.getInterfaceMethodId()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interface method id"));

      leaveOn(interfaceMethod.getMockInterface() != null
              && interfaceMethod.getMockInterface().getInterfaceId() != null
              && !Utils.isPositive(interfaceMethod.getMockInterface().getInterfaceId()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interfaceId"));
    }

    leaveOn(request.getLoadCount() != null && request.getLoadCount() <= 0,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("loadCount"));

    leaveOn(request.getLastLoadedId() != null && !Utils.isPositive(request.getLastLoadedId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("lastLoadedId"));
  }

  @Override
  protected void execute()
  {
    List<com.github.joergdev.mosy.backend.persistence.model.Record> dbRecords = null;

    if (request.getInterfaceMethod() != null)
    {
      com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = findDbEntity(
          com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod.class,
          request.getInterfaceMethod().getInterfaceMethodId(),
          "interface method with id " + request.getInterfaceMethod().getInterfaceMethodId());

      checkInterface(dbMethod);

      dbRecords = dbMethod.getRecords();
    }
    else
    {
      dbRecords = getDao(RecordDAO.class).getAll(request.getLoadCount(), request.getLastLoadedId());
    }

    for (com.github.joergdev.mosy.backend.persistence.model.Record dbRecord : Utils.nvlCollection(dbRecords))
    {
      Record apiRecord = new Record();

      ObjectUtils.copyValues(dbRecord, apiRecord, "requestData", "response", "created", "interfaceMethod");
      apiRecord.setCreatedAsLdt(dbRecord.getCreated());

      // Method / interface
      com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = dbRecord
          .getInterfaceMethod();
      com.github.joergdev.mosy.backend.persistence.model.Interface dbInterface = dbMethod.getMockInterface();

      InterfaceMethod apiMethod = new InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
      apiMethod.setName(dbMethod.getName());

      com.github.joergdev.mosy.api.model.Interface apiInterface = new com.github.joergdev.mosy.api.model.Interface();
      apiInterface.setInterfaceId(dbInterface.getInterfaceId());
      apiInterface.setName(dbInterface.getName());

      apiMethod.setMockInterface(apiInterface);

      apiRecord.setInterfaceMethod(apiMethod);

      apiRecords.add(apiRecord);
    }
  }

  private void checkInterface(com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod)
  {
    Interface apiInterface = request.getInterfaceMethod().getMockInterface();
    if (apiInterface != null)
    {
      Integer interfaceId = apiInterface.getInterfaceId();

      leaveOn(interfaceId != null && !interfaceId.equals(dbMethod.getMockInterface().getInterfaceId()),
          ResponseCode.INVALID_INPUT_PARAMS
              .withAddtitionalInfo(
                  "interface method with id " + request.getInterfaceMethod()
                      .getInterfaceMethodId() + " not exisiting for interface with id " + interfaceId));
    }
  }

  @Override
  protected void fillOutput()
  {
    response.getRecords().addAll(apiRecords);
  }
}