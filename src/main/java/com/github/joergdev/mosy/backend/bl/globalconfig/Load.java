package com.github.joergdev.mosy.backend.bl.globalconfig;

import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import com.github.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import com.github.joergdev.mosy.backend.persistence.model.GlobalConfig;
import com.github.joergdev.mosy.backend.persistence.model.RecordConfig;

public class Load extends AbstractBL<Void, LoadBaseDataResponse>
{
  private final BaseData baseData = new BaseData();

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    GlobalConfig dbGlobalConf = getDao(GlobalConfigDAO.class).get();

    baseData.setRoutingOnNoMockData(dbGlobalConf.getRoutingOnNoMockData());
    baseData.setMockActive(dbGlobalConf.getMockActive());
    baseData.setMockActiveOnStartup(dbGlobalConf.getMockActiveOnStartup());
    baseData.setTtlMockProfile(dbGlobalConf.getTtlMockProfile());
    baseData.setTtlRecordSession(dbGlobalConf.getTtlRecordSession());

    RecordConfig dbRecordConfig = getDao(RecordConfigDAO.class).getGlobal();
    if (dbRecordConfig != null)
    {
      baseData.setRecord(dbRecordConfig.getEnabled());
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setBaseData(baseData);
  }
}