package com.github.joergdev.mosy.backend.bl.mockprofile;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.mockprofile.LoadMockDataResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class LoadMockData extends AbstractBL<Integer, LoadMockDataResponse>
{
  private final List<MockData> apiMockDataList = new ArrayList<>();

  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfileID"));
  }

  protected void execute()
  {
    com.github.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = findDbEntity(
        com.github.joergdev.mosy.backend.persistence.model.MockProfile.class, request,
        "mockProfile with id " + request);

    List<MockDataMockProfile> dbMockDataMockProfiles = dbMockProfile.getMockData();

    for (MockDataMockProfile dbMockDataMockProfile : Utils.nvlCollection(dbMockDataMockProfiles))
    {
      com.github.joergdev.mosy.backend.persistence.model.MockData dbMockData = dbMockDataMockProfile
          .getMockData();
      MockData apiMockData = new MockData();

      ObjectUtils.copyValues(dbMockData, apiMockData, "created", "interfaceMethod", "request", "response",
          "mockProfiles");
      apiMockData.setCreatedAsLdt(dbMockData.getCreated());

      apiMockDataList.add(apiMockData);
    }
  }

  protected void fillOutput()
  {
    response.getMockData().addAll(apiMockDataList);
  }
}