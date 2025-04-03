package de.joergdev.mosy.backend.bl.system;

import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response._interface.SaveResponse;
import de.joergdev.mosy.backend.bl._interface.Save;
import de.joergdev.mosy.backend.bl.core.AbstractBL;

public class ImportData extends AbstractBL<BaseData, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));
  }

  @Override
  protected void execute()
  {
    for (Interface apiInterface : request.getInterfaces())
    {
      invokeSubBL(new Save(), apiInterface, new SaveResponse());
    }
  }

  @Override
  protected void fillOutput()
  {
    // nothing to do
  }
}
