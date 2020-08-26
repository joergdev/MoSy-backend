package com.github.joergdev.mosy.backend.persistence.dao;

import java.util.List;
import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import com.github.joergdev.mosy.backend.persistence.model.MockSession;
import com.github.joergdev.mosy.shared.Utils;

public class MockSessionDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<MockSession> getAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from MOCK_SESSION ");

    Query q = entityMgr.createNativeQuery(sql.toString(), MockSession.class);

    return q.getResultList();
  }

  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(mock_session_id) from mock_session ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    return Utils.bigInteger2Integer(getSingleResult(q));
  }

  public void clearAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" delete from mock_session ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }
}