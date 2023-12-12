package de.joergdev.mosy.backend.bl.mockservices;

import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.utils.PersistenceUtil;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.shared.Utils;

public class CaptureRest extends AbstractBL<CaptureCommonRequest, CaptureCommonResponse>
{
  private String path;

  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    path = request.getServicePathInterface();
    leaveOn(de.joergdev.mosy.shared.Utils.isEmpty(path),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("path"));
  }

  @Override
  protected void execute()
  {
    setServicePathInterface();
    setServicePathMethod();

    String svcPathMethod = request.getServicePathMethod();
    if (!Utils.isEmpty(svcPathMethod))
    {
      request.setRouteAddition("/" + svcPathMethod);
    }

    invokeSubBL(new CaptureCommon(), request, response);
  }

  private void setServicePathInterface()
  {
    Interface dbInterface = PersistenceUtil.getDbInterfaceByServicePath(this, path, true);
    request.setServicePathInterface(dbInterface.getServicePath());
  }

  private void setServicePathMethod()
  {
    String methodPath = Utils.removeFromStringStart(path, request.getServicePathInterface());
    methodPath = Utils.removeFromStringStart(methodPath, "/");

    request.setServicePathMethod(methodPath);
  }

  @Override
  protected void fillOutput()
  {
    // nothing to do, response get filled by subcall
  }
}