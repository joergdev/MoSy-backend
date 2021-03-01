package de.joergdev.mosy.backend.bl.mockprofile;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.mockprofile.LoadAllResponse;
import de.joergdev.mosy.backend.api.intern.request.mockprofile.LoadAllRequest;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

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
    List<de.joergdev.mosy.backend.persistence.model.MockProfile> dbMockProfiles = getDao(
        MockProfileDao.class).getAll(request.getLoadCount(), request.getLastLoadedId());

    for (de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile : Utils
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