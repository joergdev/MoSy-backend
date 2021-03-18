package de.joergdev.mosy.backend.bl.mockprofile;

import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, de.joergdev.mosy.api.response.mockprofile.LoadResponse>
{
  private final de.joergdev.mosy.api.model.MockProfile apiMockProfile = new de.joergdev.mosy.api.model.MockProfile();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = findDbEntity(
        de.joergdev.mosy.backend.persistence.model.MockProfile.class, request,
        "mockProfile with id " + request);

    ObjectUtils.copyValues(dbMockProfile, apiMockProfile, "created");
    apiMockProfile.setCreatedAsLdt(dbMockProfile.getCreated());
  }

  @Override
  protected void fillOutput()
  {
    response.setMockProfile(apiMockProfile);
  }
}