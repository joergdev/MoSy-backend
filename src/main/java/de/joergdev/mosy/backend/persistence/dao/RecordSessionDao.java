package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.Query;
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
    sql.append(" where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString(), RecordSession.class);

    q.setParameter("tenant_id", tenantId);

    return q.getResultList();
  }

  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(record_session_id) from record_session ");
    sql.append(" where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.setParameter("tenant_id", tenantId);

    return Utils.numberToInteger(getSingleResult(q));
  }

  public void clearAll()
  {
    Map<String, Object> params = new HashMap<>();

    StringBuilder sql = new StringBuilder();
    sql.append(" delete from record_session ");

    if (tenantId != null)
    {
      sql.append(" where tenant_id = :tenant_id ");

      params.put("tenant_id", tenantId);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());

    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    executeUpdate(q);
  }
}
