package de.joergdev.mosy.backend.bl.system;

import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.system.LoginResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.security.TokenManagerService;

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