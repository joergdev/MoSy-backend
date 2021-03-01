package de.joergdev.mosy.backend.bl.record.session;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.response.record.session.LoadSessionsResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.RecordSessionDao;
import de.joergdev.mosy.shared.ObjectUtils;

public class LoadSessions extends AbstractBL<Void, LoadSessionsResponse>
{
  private final List<RecordSession> apiRecordSessions = new ArrayList<>();

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    for (de.joergdev.mosy.backend.persistence.model.RecordSession dbRecordSession : getDao(
        RecordSessionDao.class).getAll())
    {
      RecordSession apiRecordSession = new RecordSession();
      apiRecordSessions.add(apiRecordSession);

      ObjectUtils.copyValues(dbRecordSession, apiRecordSession, "created");
      apiRecordSession.setCreatedAsLdt(dbRecordSession.getCreated());
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setRecordSessions(apiRecordSessions);
  }
}