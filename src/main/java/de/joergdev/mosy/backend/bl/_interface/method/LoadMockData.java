package de.joergdev.mosy.backend.bl._interface.method;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response._interface.method.LoadMockDataResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import de.joergdev.mosy.backend.persistence.model.MockDataPathParam;
import de.joergdev.mosy.backend.persistence.model.MockDataUrlArgument;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class LoadMockData extends AbstractBL<de.joergdev.mosy.api.model.InterfaceMethod, LoadMockDataResponse>
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

    de.joergdev.mosy.api.model.InterfaceMethod apiMethod = new de.joergdev.mosy.api.model.InterfaceMethod();
    apiMethod.setInterfaceMethodId(request.getInterfaceMethodId());

    transferMockProfiles(dbMethod, apiMethod);
  }

  private void transferMockProfiles(InterfaceMethod dbMethod,
                                    de.joergdev.mosy.api.model.InterfaceMethod apiMethod)
  {
    for (de.joergdev.mosy.backend.persistence.model.MockData dbMockData : dbMethod.getMockData())
    {
      MockData apiMockData = new MockData();

      ObjectUtils.copyValues(dbMockData, apiMockData, "request", "response", "created", "interfaceMethod",
          "mockProfiles");
      apiMockData.setCreatedAsLdt(dbMockData.getCreated());
      apiMockData.setInterfaceMethod(apiMethod);

      // MockProfiles
      for (MockDataMockProfile dbMockDataMockProfile : dbMockData.getMockProfiles())
      {
        de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = dbMockDataMockProfile
            .getMockProfile();
        MockProfile apiMockProfile = new MockProfile();

        ObjectUtils.copyValues(dbMockProfile, apiMockProfile, "created");
        apiMockProfile.setCreatedAsLdt(dbMockProfile.getCreated());

        apiMockData.getMockProfiles().add(apiMockProfile);
      }

      // Path params
      for (MockDataPathParam dbPathParams : dbMockData.getPathParams())
      {
        apiMockData.getPathParams().add(new PathParam(dbPathParams.getKey(), dbPathParams.getValue()));
      }

      // url args
      for (MockDataUrlArgument dbUrlArg : dbMockData.getUrlArguments())
      {
        apiMockData.getUrlArguments().add(new UrlArgument(dbUrlArg.getKey(), dbUrlArg.getValue()));
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