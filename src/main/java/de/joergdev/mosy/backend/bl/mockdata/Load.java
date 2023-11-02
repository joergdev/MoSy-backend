package de.joergdev.mosy.backend.bl.mockdata;

import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.MockData;
import de.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import de.joergdev.mosy.backend.persistence.model.MockDataPathParam;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, de.joergdev.mosy.api.response.mockdata.LoadResponse>
{
  private final de.joergdev.mosy.api.model.MockData apiMockData = new de.joergdev.mosy.api.model.MockData();

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

    ObjectUtils.copyValues(dbMockData, apiMockData, "created", "interfaceMethod", "mockProfiles");
    apiMockData.setCreatedAsLdt(dbMockData.getCreated());

    if (dbMockData.getInterfaceMethod() != null)
    {
      InterfaceMethod apiMethod = new InterfaceMethod();
      apiMethod.setInterfaceMethodId(dbMockData.getInterfaceMethod().getInterfaceMethodId());

      apiMockData.setInterfaceMethod(apiMethod);
    }

    for (MockDataMockProfile dbMockDataMockProfiles : dbMockData.getMockProfiles())
    {
      de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = dbMockDataMockProfiles
          .getMockProfile();
      MockProfile apiMockProfile = new MockProfile();

      ObjectUtils.copyValues(dbMockProfile, apiMockProfile, "created");
      apiMockProfile.setCreatedAsLdt(dbMockProfile.getCreated());

      apiMockData.getMockProfiles().add(apiMockProfile);
    }

    for (MockDataPathParam dbPathParam : dbMockData.getPathParams())
    {
      apiMockData.getPathParams().add(new PathParam(dbPathParam.getKey(), dbPathParam.getValue()));
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setMockData(apiMockData);
  }
}