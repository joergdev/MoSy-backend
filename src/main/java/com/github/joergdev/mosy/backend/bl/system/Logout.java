package com.github.joergdev.mosy.backend.bl.system;

import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.security.TokenManagerService;

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