package de.joergdev.mosy.backend.bl.globalconfig;

import java.time.LocalDateTime;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.recordconfig.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import de.joergdev.mosy.backend.persistence.dao.RecordConfigDAO;
import de.joergdev.mosy.backend.persistence.model.GlobalConfig;
import de.joergdev.mosy.backend.persistence.model.RecordConfig;

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
    dbGlobalConfig.setTtlMockProfile(request.getTtlMockProfile());
    dbGlobalConfig.setTtlRecordSession(request.getTtlRecordSession());

    entityMgr.persist(dbGlobalConfig);
    entityMgr.flush();

    saveRecordConfig();
  }

  private void saveRecordConfig()
  {
    RecordConfig dbRecordConfig = getDao(RecordConfigDAO.class).getGlobal();

    if (request.getRecord() == null)
    {
      if (dbRecordConfig != null)
      {
        invokeSubBL(new de.joergdev.mosy.backend.bl.recordconfig.Delete(),
            dbRecordConfig.getRecordConfigId(), new EmptyResponse());
      }
    }
    else
    {
      de.joergdev.mosy.api.model.RecordConfig apiRecordConfig = new de.joergdev.mosy.api.model.RecordConfig();

      if (dbRecordConfig != null)
      {
        apiRecordConfig.setRecordConfigId(dbRecordConfig.getRecordConfigId());
      }

      apiRecordConfig.setEnabled(request.getRecord());

      invokeSubBL(new de.joergdev.mosy.backend.bl.recordconfig.Save(), apiRecordConfig,
          new SaveResponse());
    }
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}