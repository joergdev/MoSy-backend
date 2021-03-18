package de.joergdev.mosy.backend.bl.recordconfig;

import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.RecordConfig;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.recordconfig.LoadResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, LoadResponse>
{
  private final RecordConfig apiRecordConfig = new RecordConfig();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    de.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConfig = findDbEntity(
        de.joergdev.mosy.backend.persistence.model.RecordConfig.class, request,
        "recordConfig with id " + request);

    ObjectUtils.copyValues(dbRecordConfig, apiRecordConfig, "mockInterface", "interfaceMethod");

    if (dbRecordConfig.getMockInterface() != null)
    {
      Interface apiInterface = new Interface();
      apiInterface.setInterfaceId(dbRecordConfig.getMockInterface().getInterfaceId());

      apiRecordConfig.setMockInterface(apiInterface);
    }

    if (dbRecordConfig.getInterfaceMethod() != null)
    {
      InterfaceMethod apiMethod = new InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbRecordConfig.getInterfaceMethod().getInterfaceMethodId());

      apiRecordConfig.setInterfaceMethod(apiMethod);
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setRecordConfig(apiRecordConfig);
  }
}