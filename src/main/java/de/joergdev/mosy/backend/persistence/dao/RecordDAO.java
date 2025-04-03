package de.joergdev.mosy.backend.persistence.dao;

import java.util.List;
import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.Record;
import de.joergdev.mosy.shared.Utils;

public class RecordDAO extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<Record> getAll(Integer loadCount, Integer lastLoadedId, Integer recordSessionID)
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from record r ");
    sql.append(" where tenant_id = :tenant_id ");

    if (recordSessionID != null)
    {
      sql.append(" and r.RECORD_SESSION_ID = :rs_id ");
    }

    if (loadCount != null || lastLoadedId != null)
    {
      sql.append(" and ");

      sql.append(" r.record_id in ( ");
      sql.append("    select r2.record_id from record r2 ");

      if (lastLoadedId != null)
      {
        sql.append("  where r2.record_id < :last_load_id ");
      }

      sql.append("    order by r2.record_id desc ");
      sql.append(" ) ");

      if (loadCount != null)
      {
        sql.append(" and ROWNUM() <= :load_count ");
      }
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), Record.class);

    q.setParameter("tenant_id", tenantId);

    if (recordSessionID != null)
    {
      q.setParameter("rs_id", recordSessionID);
    }

    if (lastLoadedId != null)
    {
      q.setParameter("last_load_id", lastLoadedId);
    }

    if (loadCount != null)
    {
      q.setParameter("load_count", loadCount);
    }

    return q.getResultList();
  }

  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select count(record_id) from record ");
    sql.append(" where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.setParameter("tenant_id", tenantId);

    return Utils.numberToInteger(getSingleResult(q));
  }

  public void deleteAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" delete from record ");
    sql.append(" where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.setParameter("tenant_id", tenantId);

    executeUpdate(q);
  }
}
