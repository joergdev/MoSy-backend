package de.joergdev.mosy.backend.bl.tenant;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.shared.Utils;

public class Delete extends AbstractBL<Integer, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request), ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    Tenant dbTenant = findDbEntity(Tenant.class, request, "tenant with id " + request);

    entityMgr.remove(dbTenant);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}
