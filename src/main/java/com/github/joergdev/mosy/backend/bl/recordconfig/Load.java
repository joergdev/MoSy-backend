package com.github.joergdev.mosy.backend.bl.recordconfig;

import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.RecordConfig;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.recordconfig.LoadResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

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
    com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConfig = findDbEntity(
        com.github.joergdev.mosy.backend.persistence.model.RecordConfig.class, request,
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