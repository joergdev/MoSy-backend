package de.joergdev.mosy.backend.bl.mockprofile;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.model.MockProfile;
import de.joergdev.mosy.shared.Utils;

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