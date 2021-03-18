package de.joergdev.mosy.backend.bl.system;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;

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