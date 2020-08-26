package com.github.joergdev.mosy.backend.bl.record;

import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.Record;
import com.github.joergdev.mosy.shared.Utils;

public class Delete extends AbstractBL<Integer, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    Record dbRecord = findDbEntity(Record.class, request, "record with id " + request);

    entityMgr.remove(dbRecord);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}