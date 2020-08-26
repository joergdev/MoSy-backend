package com.github.joergdev.mosy.backend.bl.record;

import java.time.LocalDateTime;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.record.SaveResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.bl.utils.BlUtils;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceType;
import com.github.joergdev.mosy.backend.persistence.model.MockData;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

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

    leaveOn(Utils.isEmpty(request.getRequestData())
            || request.getRequestData().length() > MockData.LENGTH_REQUEST,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request data"));

    leaveOn(Utils.isEmpty(request.getResponse()) || request.getResponse().length() > MockData.LENGTH_RESPONSE,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response data"));
  }

  @Override
  protected void execute()
  {
    com.github.joergdev.mosy.backend.persistence.model.Record dbRecord = null;

    if (request.getRecordId() != null)
    {
      dbRecord = findDbEntity(com.github.joergdev.mosy.backend.persistence.model.Record.class,
          request.getRecordId(), "record with id " + request.getRecordId());
    }
    else
    {
      dbRecord = new com.github.joergdev.mosy.backend.persistence.model.Record();
      dbRecord.setCreated(LocalDateTime.now());
    }

    InterfaceMethod dbMethod = findDbEntity(InterfaceMethod.class,
        request.getInterfaceMethod().getInterfaceMethodId(),
        "interface method with id " + request.getInterfaceMethod().getInterfaceMethodId());

    // if not intern -> only custom allowed
    checkInterfaceType(dbMethod);

    // format request/response
    request.formatRequestResponse(BlUtils.getInterfaceTypeId(request.getInterfaceMethod(), dbMethod));

    // transfer values
    ObjectUtils.copyValues(request, dbRecord, "interfaceMethod", "created");
    dbRecord.setInterfaceMethod(dbMethod);

    // save
    entityMgr.persist(dbRecord);
    entityMgr.flush();

    id = dbRecord.getRecordId();
  }

  private void checkInterfaceType(InterfaceMethod dbMethod)
  {
    if (!isSubcall)
    {
      InterfaceType dbInterfaceType = dbMethod.getMockInterface().getType();

      leaveOn(
          !com.github.joergdev.mosy.api.model.InterfaceType
              .isCustomType(dbInterfaceType.getInterfaceTypeId()),
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