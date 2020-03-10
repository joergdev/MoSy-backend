package com.github.joergdev.mosy.backend.bl.mocksession;

import java.time.LocalDateTime;
import com.github.joergdev.mosy.api.response.mocksession.CreateResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.model.MockSession;

public class Create extends AbstractBL<Void, CreateResponse>
{
  private MockSession dbMockSession;

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    dbMockSession = new MockSession();
    dbMockSession.setCreated(LocalDateTime.now());

    entityMgr.persist(dbMockSession);
  }

  @Override
  protected void fillOutput()
  {
    com.github.joergdev.mosy.api.model.MockSession apiMockSession = new com.github.joergdev.mosy.api.model.MockSession();
    apiMockSession.setMockSessionID(dbMockSession.getMockSessionID());
    apiMockSession.setCreatedAsLdt(dbMockSession.getCreated());

    response.setMockSession(apiMockSession);
  }
}