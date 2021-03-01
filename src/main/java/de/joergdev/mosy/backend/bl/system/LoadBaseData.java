package de.joergdev.mosy.backend.bl.system;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.globalconfig.Load;
import de.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.dao.RecordDAO;
import de.joergdev.mosy.backend.persistence.dao.RecordSessionDao;
import de.joergdev.mosy.shared.ObjectUtils;

public class LoadBaseData extends AbstractBL<Void, LoadBaseDataResponse>
{
  private BaseData baseDataGlobalConf;
  private int countRecordSessions = 0;
  private int countMockProfiles = 0;
  private int countRecords = 0;
  private final List<Interface> apiInterfaces = new ArrayList<>();

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    // Load GlobalConfig
    baseDataGlobalConf = invokeSubBL(new Load(), null, new LoadBaseDataResponse()).getBaseData();

    // Interfaces
    loadInterfaces();

    // count mock session
    countRecordSessions = getDao(RecordSessionDao.class).getCount();

    // count mock profiles
    countMockProfiles = getDao(MockProfileDao.class).getCount();

    // count records
    countRecords = getDao(RecordDAO.class).getCount();
  }

  private void loadInterfaces()
  {
    for (de.joergdev.mosy.backend.persistence.model.Interface dbInterface : getDao(InterfaceDao.class)
        .getAll())
    {
      Interface apiInterface = new Interface();
      apiInterfaces.add(apiInterface);

      // Basedata
      ObjectUtils.copyValues(dbInterface, apiInterface, "type");

      if (dbInterface.getType() != null)
      {
        apiInterface.setType(InterfaceType.getById(dbInterface.getType().getInterfaceTypeId()));
      }
      else
      {
        apiInterface.setType(null);
      }

      // Record Y/N
      apiInterface.setRecord(
          dbInterface.getRecordConfig() == null || dbInterface.getRecordConfig().getEnabled() == null
              ? null
              : Boolean.TRUE.equals(dbInterface.getRecordConfig().getEnabled()));
    }
  }

  @Override
  protected void fillOutput()
  {
    BaseData baseData = new BaseData();
    response.setBaseData(baseData);

    // copy values of GlobalConfig
    ObjectUtils.copyValues(baseDataGlobalConf, baseData);

    baseData.setCountRecordSessions(countRecordSessions);
    baseData.setCountRecords(countRecords);
    baseData.setCountMockProfiles(countMockProfiles);

    baseData.getInterfaces().addAll(apiInterfaces);
  }
}