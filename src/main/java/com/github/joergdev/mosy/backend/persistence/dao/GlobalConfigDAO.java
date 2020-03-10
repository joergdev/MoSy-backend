package com.github.joergdev.mosy.backend.persistence.dao;

import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.Constraint;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import com.github.joergdev.mosy.backend.persistence.model.GlobalConfig;
import com.github.joergdev.mosy.shared.Utils;

public class GlobalConfigDAO extends AbstractDAO
{
  public GlobalConfig get()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from global_config ");

    Query q = entityMgr.createNativeQuery(sql.toString(), GlobalConfig.class);

    return getSingleResult(q);
  }

  public void setValuesOnStartup()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" update global_config set MOCK_ACTIVE = MOCK_ACTIVE_ON_STARTUP ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }

  @SuppressWarnings("unchecked")
  public Constraint findConstraint(String table, String col)
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT constraint_Name, sql ");
    sql.append(" FROM INFORMATION_SCHEMA.CONSTRAINTS ");
    sql.append(" where table_name = :tbl and COLUMN_LIST = :col ");

    Query q = entityMgr.createNativeQuery(sql.toString());
    q.setParameter("tbl", table);
    q.setParameter("col", col);

    Object[] arr = (Object[]) Utils.getFirstElementOfCollection(q.getResultList());
    if (arr != null)
    {
      Constraint c = new Constraint();
      c.setTable(table);
      c.setName((String) arr[0]);
      c.setSql((String) arr[1]);

      return c;
    }

    return null;
  }

  public void alterConstraint(Constraint constraint)
  {
    // First drop the constraint
    StringBuilder sqlDrop = new StringBuilder();

    // for any reason alter with parameters (table, constraint) doest work here
    sqlDrop.append(" ALTER TABLE ").append(constraint.getTable()).append(" DROP CONSTRAINT ")
        .append(constraint.getName());

    Query q = entityMgr.createNativeQuery(sqlDrop.toString());

    q.executeUpdate();

    // Then recreate with new sql alter command
    q = entityMgr.createNativeQuery(constraint.getSql());

    q.executeUpdate();
  }
}