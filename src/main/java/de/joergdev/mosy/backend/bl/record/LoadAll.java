package de.joergdev.mosy.backend.bl.record;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.record.LoadAllResponse;
import de.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.RecordDAO;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

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

      leaveOn(request.getLoadCount() != null || request.getRecordSessionID() != null,
          ResponseCode.OPERATION_NOT_POSSIBLE.withAddtitionalInfo("load by method, filtering not supported"));
    }

    leaveOn(request.getLoadCount() != null && request.getLoadCount() <= 0,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("loadCount"));

    leaveOn(request.getLastLoadedId() != null && !Utils.isPositive(request.getLastLoadedId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("lastLoadedId"));

    leaveOn(request.getRecordSessionID() != null && !Utils.isPositive(request.getRecordSessionID()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("recordSessionID"));
  }

  @Override
  protected void execute()
  {
    List<de.joergdev.mosy.backend.persistence.model.Record> dbRecords = null;

    if (request.getInterfaceMethod() != null)
    {
      de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = findDbEntity(
          de.joergdev.mosy.backend.persistence.model.InterfaceMethod.class,
          request.getInterfaceMethod().getInterfaceMethodId(),
          "interface method with id " + request.getInterfaceMethod().getInterfaceMethodId());

      checkInterface(dbMethod);

      dbRecords = dbMethod.getRecords();
    }
    else
    {
      dbRecords = getDao(RecordDAO.class).getAll(request.getLoadCount(), request.getLastLoadedId(),
          request.getRecordSessionID());
    }

    for (de.joergdev.mosy.backend.persistence.model.Record dbRecord : Utils.nvlCollection(dbRecords))
    {
      Record apiRecord = new Record();

      ObjectUtils.copyValues(dbRecord, apiRecord, "requestData", "response", "created", "interfaceMethod",
          "recordSession");
      apiRecord.setCreatedAsLdt(dbRecord.getCreated());

      // Method / interface
      de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = dbRecord
          .getInterfaceMethod();
      de.joergdev.mosy.backend.persistence.model.Interface dbInterface = dbMethod.getMockInterface();

      InterfaceMethod apiMethod = new InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
      apiMethod.setName(dbMethod.getName());

      de.joergdev.mosy.api.model.Interface apiInterface = new de.joergdev.mosy.api.model.Interface();
      apiInterface.setInterfaceId(dbInterface.getInterfaceId());
      apiInterface.setName(dbInterface.getName());

      apiMethod.setMockInterface(apiInterface);

      apiRecord.setInterfaceMethod(apiMethod);

      apiRecords.add(apiRecord);

      // RecordSession
      if (dbRecord.getRecordSession() != null)
      {
        RecordSession apiRecordSession = new RecordSession();
        apiRecordSession.setRecordSessionID(dbRecord.getRecordSession().getRecordSessionID());
        apiRecordSession.setCreatedAsLdt(dbRecord.getRecordSession().getCreated());

        apiRecord.setRecordSession(apiRecordSession);
      }
    }
  }

  private void checkInterface(de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod)
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