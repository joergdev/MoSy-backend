package com.github.joergdev.mosy.backend.bl.record;

import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.model.RecordSession;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.record.LoadResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.Interface;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, LoadResponse>
{
  private final Record apiRecord = new Record();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    com.github.joergdev.mosy.backend.persistence.model.Record dbRecord = findDbEntity(
        com.github.joergdev.mosy.backend.persistence.model.Record.class, request,
        "record with id " + request);

    ObjectUtils.copyValues(dbRecord, apiRecord, "created", "interfaceMethod", "recordSession");
    apiRecord.setCreatedAsLdt(dbRecord.getCreated());

    // Method / interface
    com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = dbRecord
        .getInterfaceMethod();
    Interface dbInterface = dbMethod.getMockInterface();

    InterfaceMethod apiMethod = new InterfaceMethod();
    apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
    apiMethod.setName(dbMethod.getName());

    com.github.joergdev.mosy.api.model.Interface apiInterface = new com.github.joergdev.mosy.api.model.Interface();
    apiInterface.setInterfaceId(dbInterface.getInterfaceId());
    apiInterface.setName(dbInterface.getName());

    apiMethod.setMockInterface(apiInterface);

    apiRecord.setInterfaceMethod(apiMethod);

    // RecordSession
    if (dbRecord.getRecordSession() != null)
    {
      RecordSession apiRecordSession = new RecordSession();
      apiRecordSession.setRecordSessionID(dbRecord.getRecordSession().getRecordSessionID());
      apiRecordSession.setCreatedAsLdt(dbRecord.getRecordSession().getCreated());

      apiRecord.setRecordSession(apiRecordSession);
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setRecord(apiRecord);
  }
}