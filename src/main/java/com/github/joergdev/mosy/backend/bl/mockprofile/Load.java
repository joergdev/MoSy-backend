package com.github.joergdev.mosy.backend.bl.mockprofile;

import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class Load extends AbstractBL<Integer, com.github.joergdev.mosy.api.response.mockprofile.LoadResponse>
{
  private final com.github.joergdev.mosy.api.model.MockProfile apiMockProfile = new com.github.joergdev.mosy.api.model.MockProfile();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    com.github.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = findDbEntity(
        com.github.joergdev.mosy.backend.persistence.model.MockProfile.class, request,
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