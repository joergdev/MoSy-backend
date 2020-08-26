package com.github.joergdev.mosy.backend.bl.mockdata;

import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.MockData;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, com.github.joergdev.mosy.api.response.mockdata.LoadResponse>
{
  private final com.github.joergdev.mosy.api.model.MockData apiMockData = new com.github.joergdev.mosy.api.model.MockData();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    MockData dbMockData = findDbEntity(MockData.class, request, "mockData with id " + request);

    ObjectUtils.copyValues(dbMockData, apiMockData, "created", "interfaceMethod", "mockSession");
    apiMockData.setCreatedAsLdt(dbMockData.getCreated());

    if (dbMockData.getInterfaceMethod() != null)
    {
      InterfaceMethod apiMethod = new InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMockData.getInterfaceMethod().getInterfaceMethodId());

      apiMockData.setInterfaceMethod(apiMethod);
    }

    if (dbMockData.getMockSession() != null)
    {
      MockSession apiMockSession = new MockSession();
      apiMockSession.setMockSessionID(dbMockData.getMockSession().getMockSessionID());
      apiMockSession.setCreatedAsLdt(dbMockData.getMockSession().getCreated());

      apiMockData.setMockSession(apiMockSession);
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setMockData(apiMockData);
  }
}