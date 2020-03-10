package com.github.joergdev.mosy.backend.bl.mocksession;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.response.mocksession.LoadSessionsResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.MockSessionDao;
import com.github.joergdev.mosy.shared.ObjectUtils;

public class LoadSessions extends AbstractBL<Void, LoadSessionsResponse>
{
  private final List<MockSession> apiMockSessions = new ArrayList<>();

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    for (com.github.joergdev.mosy.backend.persistence.model.MockSession dbMockSession : getDao(
        MockSessionDao.class).getAll())
    {
      MockSession apiMockSession = new MockSession();
      apiMockSessions.add(apiMockSession);

      ObjectUtils.copyValues(dbMockSession, apiMockSession, "created");
      apiMockSession.setCreatedAsLdt(dbMockSession.getCreated());
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setMockSessions(apiMockSessions);
  }
}