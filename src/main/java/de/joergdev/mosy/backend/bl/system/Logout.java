package de.joergdev.mosy.backend.bl.system;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.security.TokenManagerService;

public class Logout extends AbstractBL<Void, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    boolean success = TokenManagerService.invalidateToken(getToken());

    if (!success)
    {
      addResponseMessage(ResponseCode.OPERATION_FAILED_INFO.withAddtitionalInfo("logout"));
    }
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}