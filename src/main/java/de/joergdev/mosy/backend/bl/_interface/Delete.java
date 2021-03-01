package de.joergdev.mosy.backend.bl._interface;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.shared.Utils;

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
    Interface dbInterface = findDbEntity(Interface.class, request, "no interface with name: " + request);

    entityMgr.remove(dbInterface);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}