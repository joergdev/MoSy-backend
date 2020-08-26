package com.github.joergdev.mosy.backend.bl._interface.method;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response._interface.method.LoadMockDataResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class LoadMockData
    extends AbstractBL<com.github.joergdev.mosy.api.model.InterfaceMethod, LoadMockDataResponse>
{
  private final List<MockData> mockDataList = new ArrayList<>();

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

    com.github.joergdev.mosy.api.model.InterfaceMethod apiMethod = new com.github.joergdev.mosy.api.model.InterfaceMethod();
    apiMethod.setInterfaceMethodId(request.getInterfaceMethodId());

    for (com.github.joergdev.mosy.backend.persistence.model.MockData dbMockData : dbMethod.getMockData())
    {
      MockData apiMockData = new MockData();

      ObjectUtils.copyValues(dbMockData, apiMockData, "request", "response", "created", "interfaceMethod",
          "mockSession");
      apiMockData.setCreatedAsLdt(dbMockData.getCreated());
      apiMockData.setInterfaceMethod(apiMethod);

      if (dbMockData.getMockSession() != null)
      {
        apiMockData.setMockSession(new MockSession());
        apiMockData.getMockSession().setMockSessionID(dbMockData.getMockSession().getMockSessionID());
      }

      mockDataList.add(apiMockData);
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
    response.getMockData().addAll(mockDataList);
  }
}