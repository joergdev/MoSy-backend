package com.github.joergdev.mosy.backend.bl._interface.method;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.RecordConfig;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response._interface.method.LoadRecordConfigsResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class LoadRecordConfigs
    extends AbstractBL<com.github.joergdev.mosy.api.model.InterfaceMethod, LoadRecordConfigsResponse>
{
  private final List<RecordConfig> recordConfigList = new ArrayList<>();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(request.getInterfaceMethodId() == null || !Utils.isPositive(request.getInterfaceMethodId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));

    leaveOn(request.getMockInterface() != null && request.getMockInterface().getInterfaceId() != null
            && !Utils.isPositive(request.getMockInterface().getInterfaceId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("interfaceId"));
  }

  @Override
  protected void execute()
  {
    InterfaceMethod dbMethod = findDbEntity(InterfaceMethod.class, request.getInterfaceMethodId(),
        "interface method with id " + request.getInterfaceMethodId());

    checkInterface(dbMethod);

    for (com.github.joergdev.mosy.backend.persistence.model.RecordConfig dbRecordConf : dbMethod
        .getRecordConfig())
    {
      // dont transfer method global rc
      if (dbRecordConf.getRequestData() == null)
      {
        continue;
      }

      RecordConfig apiRecordConf = new RecordConfig();
      apiRecordConf.setInterfaceMethod(request);

      ObjectUtils.copyValues(dbRecordConf, apiRecordConf, "requestData", "mockInterface", "interfaceMethod");

      recordConfigList.add(apiRecordConf);
    }
  }

  private void checkInterface(InterfaceMethod dbMethod)
  {
    Interface apiInterface = request.getMockInterface();
    if (apiInterface != null)
    {
      Integer interfaceId = apiInterface.getInterfaceId();

      leaveOn(interfaceId != null && !interfaceId.equals(dbMethod.getMockInterface().getInterfaceId()),
          ResponseCode.INVALID_INPUT_PARAMS
              .withAddtitionalInfo("interface method with id " + request.getInterfaceMethodId()
                                   + " not exisiting for interface with id " + interfaceId));
    }
  }

  @Override
  protected void fillOutput()
  {
    response.getRecordConfigs().addAll(recordConfigList);
  }
}