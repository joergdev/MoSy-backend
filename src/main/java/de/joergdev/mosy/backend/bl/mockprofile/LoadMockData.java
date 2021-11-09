package de.joergdev.mosy.backend.bl.mockprofile;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.mockprofile.LoadMockDataResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.model.MockDataMockProfile;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class LoadMockData extends AbstractBL<MockProfile, LoadMockDataResponse>
{
  private final List<MockData> apiMockDataList = new ArrayList<>();

  private Integer id;
  private String name;

  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfile"));

    id = request.getMockProfileID();
    leaveOn(id != null && !Utils.isPositive(id),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfileID"));

    name = request.getName();

    leaveOn(id == null && Utils.isEmpty(name),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("mockProfileID / name"));
  }

  protected void execute()
  {
    de.joergdev.mosy.backend.persistence.model.MockProfile dbMockProfile = null;

    if (id != null)
    {
      dbMockProfile = findDbEntity(de.joergdev.mosy.backend.persistence.model.MockProfile.class, request,
          "mockProfile with id " + id);
    }
    else
    {
      dbMockProfile = getDao(MockProfileDao.class).getByName(name, null);
      leaveOn(dbMockProfile == null,
          ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("mockProfile with name " + name));

      request.setMockProfileID(dbMockProfile.getMockProfileID());
    }

    List<MockDataMockProfile> dbMockDataMockProfiles = dbMockProfile.getMockData();

    for (MockDataMockProfile dbMockDataMockProfile : Utils.nvlCollection(dbMockDataMockProfiles))
    {
      de.joergdev.mosy.backend.persistence.model.MockData dbMockData = dbMockDataMockProfile.getMockData();
      MockData apiMockData = new MockData();

      ObjectUtils.copyValues(dbMockData, apiMockData, "created", "interfaceMethod", "request", "response",
          "mockProfiles");
      apiMockData.setCreatedAsLdt(dbMockData.getCreated());

      // set ids to model
      InterfaceMethod apiInterfaceMethod = new InterfaceMethod();
      apiInterfaceMethod.setInterfaceMethodId(dbMockData.getInterfaceMethod().getInterfaceMethodId());
      apiMockData.setInterfaceMethod(apiInterfaceMethod);

      apiMockData.getMockProfiles().add(request);

      apiMockDataList.add(apiMockData);
    }
  }

  protected void fillOutput()
  {
    response.getMockData().addAll(apiMockDataList);
  }
}