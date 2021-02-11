package com.github.joergdev.mosy.backend.bl.mockprofile;

import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import com.github.joergdev.mosy.backend.persistence.model.MockProfile;
import com.github.joergdev.mosy.shared.Utils;

public class Delete extends AbstractBL<Integer, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("id"));
  }

  @Override
  protected void execute()
  {
    MockProfile dbMockProfile = findDbEntity(MockProfile.class, request, "mockProfile with id " + request);

    entityMgr.remove(dbMockProfile);

    getDao(MockDataDAO.class).deleteMockDataDedicatedForMockProfile(request);
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}