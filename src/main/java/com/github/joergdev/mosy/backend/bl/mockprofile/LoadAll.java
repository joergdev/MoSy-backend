package com.github.joergdev.mosy.backend.bl.mockprofile;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.MockProfile;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.mockprofile.LoadAllResponse;
import com.github.joergdev.mosy.backend.api.intern.request.mockprofile.LoadAllRequest;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class LoadAll extends AbstractBL<LoadAllRequest, LoadAllResponse>
{
  private final List<MockProfile> apiMockProfiles = new ArrayList<>();

  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(request.getLoadCount() != null && request.getLoadCount() <= 0,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("loadCount"));

    leaveOn(request.getLastLoadedId() != null && !Utils.isPositive(request.getLastLoadedId()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("lastLoadedId"));
  }

  protected void execute()
  {
    List<com.github.joergdev.mosy.backend.persistence.model.MockProfile> dbMockProfiles = getDao(
        MockProfileDao.class).getAll(request.getLoadCount(), request.getLastLoadedId());

    for (com.github.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile : Utils
        .nvlCollection(dbMockProfiles))
    {
      MockProfile apiMockProfile = new MockProfile();

      ObjectUtils.copyValues(dbMockProfile, apiMockProfile, "created");
      apiMockProfile.setCreatedAsLdt(dbMockProfile.getCreated());

      apiMockProfiles.add(apiMockProfile);
    }
  }

  protected void fillOutput()
  {
    response.getMockProfiles().addAll(apiMockProfiles);
  }
}