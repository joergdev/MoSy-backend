package de.joergdev.mosy.backend.bl.mockprofile;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.mockprofile.LoadMockDataResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

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
    de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = findDbEntity(
        de.joergdev.mosy.backend.persistence.model.MockProfile.class, request,
        "mockProfile with id " + request);

    List<MockDataMockProfile> dbMockDataMockProfiles = dbMockProfile.getMockData();

    for (MockDataMockProfile dbMockDataMockProfile : Utils.nvlCollection(dbMockDataMockProfiles))
    {
      de.joergdev.mosy.backend.persistence.model.MockData dbMockData = dbMockDataMockProfile
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