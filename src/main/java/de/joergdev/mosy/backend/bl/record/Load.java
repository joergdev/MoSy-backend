package de.joergdev.mosy.backend.bl.record;

import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.record.LoadResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

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
    de.joergdev.mosy.backend.persistence.model.Record dbRecord = findDbEntity(
        de.joergdev.mosy.backend.persistence.model.Record.class, request,
        "record with id " + request);

    ObjectUtils.copyValues(dbRecord, apiRecord, "created", "interfaceMethod", "recordSession");
    apiRecord.setCreatedAsLdt(dbRecord.getCreated());

    // Method / interface
    de.joergdev.mosy.backend.persistence.model.InterfaceMethod dbMethod = dbRecord
        .getInterfaceMethod();
    Interface dbInterface = dbMethod.getMockInterface();

    InterfaceMethod apiMethod = new InterfaceMethod();
    apiMethod.setInterfaceMethodId(dbMethod.getInterfaceMethodId());
    apiMethod.setName(dbMethod.getName());

    de.joergdev.mosy.api.model.Interface apiInterface = new de.joergdev.mosy.api.model.Interface();
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