package de.joergdev.mosy.backend.bl.record;

import java.time.LocalDateTime;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.record.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.utils.BlUtils;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.InterfaceType;
import de.joergdev.mosy.backend.persistence.model.MockData;
import de.joergdev.mosy.backend.persistence.model.RecordPathParam;
import de.joergdev.mosy.backend.persistence.model.RecordSession;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<Record, SaveResponse>
{
  private Integer id;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(
        request.getInterfaceMethod() == null || request.getInterfaceMethod().getInterfaceMethodId() == null,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interface method"));

    leaveOn(!Utils.isEmpty(request.getRequestData())
            && request.getRequestData().length() > MockData.LENGTH_REQUEST,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request data length"));

    leaveOn(
        !Utils.isEmpty(request.getResponse()) && request.getResponse().length() > MockData.LENGTH_RESPONSE,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response data length"));

    leaveOn(request.getRecordSession() != null && request.getRecordSession().getRecordSessionID() == null,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("recordSession"));
  }

  @Override
  protected void execute()
  {
    de.joergdev.mosy.backend.persistence.model.Record dbRecord = getDbRecord();

    InterfaceMethod dbMethod = findDbEntity(InterfaceMethod.class,
        request.getInterfaceMethod().getInterfaceMethodId(),
        "interface method with id " + request.getInterfaceMethod().getInterfaceMethodId());

    // some validations can not be done until data for method is load, do them now
    validateAfterLoad(dbMethod);

    // RecordSession
    RecordSession dbRecordSession = getDbRecordSession();

    // format request/response
    request.formatRequestResponse(BlUtils.getInterfaceTypeId(request.getInterfaceMethod(), dbMethod));

    // transfer values
    ObjectUtils.copyValues(request, dbRecord, "interfaceMethod", "created", "recordSession", "pathParams");
    dbRecord.setInterfaceMethod(dbMethod);
    dbRecord.setRecordSession(dbRecordSession);

    // save
    entityMgr.persist(dbRecord);
    entityMgr.flush();

    id = dbRecord.getRecordId();

    // save path params
    savePathParams(dbRecord);
  }

  private void validateAfterLoad(InterfaceMethod dbMethod)
  {
    // if not intern -> only custom allowed
    checkInterfaceType(dbMethod);

    // check if request and response set (except rest)
    if (!de.joergdev.mosy.api.model.InterfaceType.REST.id
        .equals(BlUtils.getInterfaceTypeId(request.getInterfaceMethod(), dbMethod)))
    {
      leaveOn(Utils.isEmpty(request.getRequestData()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request data"));
      leaveOn(Utils.isEmpty(request.getResponse()),
          ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response"));
    }
  }

  private RecordSession getDbRecordSession()
  {
    if (request.getRecordSession() != null)
    {
      return findDbEntity(RecordSession.class, request.getRecordSession().getRecordSessionID(),
          "recordSession with id: " + request.getRecordSession().getRecordSessionID());
    }

    return null;
  }

  private de.joergdev.mosy.backend.persistence.model.Record getDbRecord()
  {
    de.joergdev.mosy.backend.persistence.model.Record dbRecord = null;

    if (request.getRecordId() != null)
    {
      dbRecord = findDbEntity(de.joergdev.mosy.backend.persistence.model.Record.class, request.getRecordId(),
          "record with id " + request.getRecordId());
    }
    else
    {
      dbRecord = new de.joergdev.mosy.backend.persistence.model.Record();
      dbRecord.setCreated(LocalDateTime.now());
    }

    return dbRecord;
  }

  private void savePathParams(de.joergdev.mosy.backend.persistence.model.Record dbRecord)
  {
    boolean dbChanged = false;

    // delete all existing params
    for (RecordPathParam dbPathParam : Utils.nvlCollection(dbRecord.getPathParams()))
    {
      entityMgr.remove(entityMgr.find(RecordPathParam.class, dbPathParam.getRecordPathParamId()));

      dbChanged = true;
    }

    for (PathParam pathParam : request.getPathParams())
    {
      RecordPathParam dbPathParam = new RecordPathParam();
      dbPathParam.setKey(pathParam.getKey());
      dbPathParam.setValue(pathParam.getValue());
      dbPathParam.setRecord(dbRecord);

      entityMgr.persist(dbPathParam);

      dbChanged = true;
    }

    if (dbChanged)
    {
      entityMgr.flush();
    }
  }

  private void checkInterfaceType(InterfaceMethod dbMethod)
  {
    if (!isSubcall)
    {
      InterfaceType dbInterfaceType = dbMethod.getMockInterface().getType();

      leaveOn(!de.joergdev.mosy.api.model.InterfaceType.isCustomType(dbInterfaceType.getInterfaceTypeId()),
          ResponseCode.OPERATION_NOT_POSSIBLE
              .withAddtitionalInfo("record can only be saved for custom interfaces"));
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setRecordID(id);
  }
}