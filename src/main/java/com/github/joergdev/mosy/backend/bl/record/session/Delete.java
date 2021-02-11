package com.github.joergdev.mosy.backend.bl.record.session;

import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.RecordSession;
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
    RecordSession dbRecordSession = findDbEntity(RecordSession.class, request,
        "recordSession with id " + request);

    entityMgr.remove(dbRecordSession);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}