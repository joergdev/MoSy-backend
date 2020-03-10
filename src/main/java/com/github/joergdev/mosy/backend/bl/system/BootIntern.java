package com.github.joergdev.mosy.backend.bl.system;

import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;

/**
 * Intern BL for booting system without token
 * 
 * @author Andreas Joerg
 */
public class BootIntern extends AbstractBL<Void, EmptyResponse>
{
  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    // no validation
  }

  @Override
  protected void execute()
  {
    invokeSubBL(new Boot(), request, response);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}