package de.joergdev.mosy.backend.persistence.dao;

import java.util.List;
import javax.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.RecordSession;
import de.joergdev.mosy.shared.Utils;

public class RecordSessionDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<RecordSession> getAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from RECORD_SESSION ");

    Query q = entityMgr.createNativeQuery(sql.toString(), RecordSession.class);

    return q.getResultList();
  }

  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(record_session_id) from record_session ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    return Utils.bigInteger2Integer(getSingleResult(q));
  }

  public void clearAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" delete from record_session ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }
}