package de.joergdev.mosy.backend.bl.record.session;

import java.time.LocalDateTime;
import de.joergdev.mosy.api.response.record.session.CreateResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.model.RecordSession;

public class Create extends AbstractBL<Void, CreateResponse>
{
  private RecordSession dbRecordSession;

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    dbRecordSession = new RecordSession();
    dbRecordSession.setCreated(LocalDateTime.now());

    entityMgr.persist(dbRecordSession);
    entityMgr.flush();
  }

  @Override
  protected void fillOutput()
  {
    de.joergdev.mosy.api.model.RecordSession apiRecordSession = new de.joergdev.mosy.api.model.RecordSession();
    apiRecordSession.setRecordSessionID(dbRecordSession.getRecordSessionID());
    apiRecordSession.setCreatedAsLdt(dbRecordSession.getCreated());

    response.setRecordSession(apiRecordSession);
  }
}