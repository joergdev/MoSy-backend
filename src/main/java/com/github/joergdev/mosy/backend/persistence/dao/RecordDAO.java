package com.github.joergdev.mosy.backend.persistence.dao;

import java.util.List;
import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import com.github.joergdev.mosy.backend.persistence.model.Record;
import com.github.joergdev.mosy.shared.Utils;

public class RecordDAO extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<Record> getAll(Integer loadCount, Integer lastLoadedId, Integer recordSessionID)
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from record r ");

    boolean needsWhere = true;

    if (recordSessionID != null)
    {
      sql.append(" where r.RECORD_SESSION_ID = :rs_id ");

      needsWhere = false;
    }

    if (loadCount != null || lastLoadedId != null)
    {
      sql.append(needsWhere
          ? " where "
          : " and ");

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

      needsWhere = false;
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), Record.class);

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
    sql.append("select count(record_id) from record ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    return Utils.bigInteger2Integer(getSingleResult(q));
  }
}