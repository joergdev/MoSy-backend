package com.github.joergdev.mosy.backend.bl.system;

import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.system.LoginResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.security.TokenManagerService;

public class Login extends AbstractBL<Integer, LoginResponse>
{
  private String token;

  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || request.intValue() == 0,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));
  }

  @Override
  protected void execute()
  {
    token = TokenManagerService.createToken(request);

    leaveOn(token == null, ResponseCode.INVALID_CREDENTIALS);
  }

  @Override
  protected void fillOutput()
  {
    response.setToken(token);
  }
}