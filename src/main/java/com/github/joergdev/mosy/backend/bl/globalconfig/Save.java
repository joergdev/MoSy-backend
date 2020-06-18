package com.github.joergdev.mosy.backend.bl.globalconfig;

import java.time.LocalDateTime;
import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.recordconfig.SaveResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import com.github.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import com.github.joergdev.mosy.backend.persistence.model.GlobalConfig;
import com.github.joergdev.mosy.backend.persistence.model.RecordConfig;

public class Save extends AbstractBL<BaseData, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));
  }

  @Override
  protected void execute()
  {
    GlobalConfig dbGlobalConfig = getDao(GlobalConfigDAO.class).get();

    if (dbGlobalConfig == null)
    {
      dbGlobalConfig = new GlobalConfig();
      dbGlobalConfig.setCreated(LocalDateTime.now());
    }

    dbGlobalConfig.setRoutingOnNoMockData(request.getRoutingOnNoMockData());
    dbGlobalConfig.setMockActive(request.getMockActive());
    dbGlobalConfig.setMockActiveOnStartup(request.getMockActiveOnStartup());
    dbGlobalConfig.setTtlMockSession(request.getTtlMockSession());

    entityMgr.persist(dbGlobalConfig);
    entityMgr.flush();

    saveRecordConfig();
  }

  private void saveRecordConfig()
  {
    com.github.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new com.github.joergdev.mosy.api.model.RecordConfig();

    RecordConfig dbRecordConfig = getDao(RecordConfigDAO.class).getGlobal();
    if (dbRecordConfig != null)
    {
      apiRecordConfig.setRecordConfigId(dbRecordConfig.getRecordConfigId());
    }

    apiRecordConfig.setEnabled(request.getRecord());

    invokeSubBL(new com.github.joergdev.mosy.backend.bl.recordconfig.Save(), apiRecordConfig,
        new SaveResponse());
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}