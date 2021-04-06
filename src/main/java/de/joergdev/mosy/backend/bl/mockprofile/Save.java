package de.joergdev.mosy.backend.bl.mockprofile;

import java.time.LocalDateTime;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.mockprofile.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<de.joergdev.mosy.api.model.MockProfile, SaveResponse>
{
  private de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile;

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(Utils.isEmpty(request.getName())
            || request.getName().length() > de.joergdev.mosy.backend.persistence.model.MockProfile.LENGTH_NAME
            || Utils.isNumeric(request.getName()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("name"));

    leaveOn(!Utils.isEmpty(request.getDescription())
            && request.getDescription()
                .length() > de.joergdev.mosy.backend.persistence.model.MockProfile.LENGTH_DESCRIPTION,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("response"));
  }

  @Override
  protected void execute()
  {
    loadOrCreateDbMockData();

    // check name unique
    checkUniqueData();

    // transfer values
    ObjectUtils.copyValues(request, dbMockProfile, "created");

    // save
    entityMgr.persist(dbMockProfile);
    entityMgr.flush();
  }

  private void loadOrCreateDbMockData()
  {
    if (request.getMockProfileID() != null)
    {
      dbMockProfile = findDbEntity(de.joergdev.mosy.backend.persistence.model.MockProfile.class,
          request.getMockProfileID(), "mockProfile with id " + request.getMockProfileID());
    }
    else
    {
      dbMockProfile = new de.joergdev.mosy.backend.persistence.model.MockProfile();
      dbMockProfile.setCreated(LocalDateTime.now());
    }
  }

  private void checkUniqueData()
  {
    // check unique name
    leaveOn(getDao(MockProfileDao.class).existsByName(request.getName(), request.getMockProfileID()),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("mockProfile with name: " + request.getName()));
  }

  @Override
  protected void fillOutput()
  {
    de.joergdev.mosy.api.model.MockProfile apiMockProfileResponse = new de.joergdev.mosy.api.model.MockProfile();
    ObjectUtils.copyValues(dbMockProfile, apiMockProfileResponse, "created");
    apiMockProfileResponse.setCreatedAsLdt(dbMockProfile.getCreated());

    response.setMockProfile(apiMockProfileResponse);
  }
}