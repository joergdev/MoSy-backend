package de.joergdev.mosy.backend.persistence.dao;

import javax.persistence.Query;
import de.joergdev.mosy.backend.persistence.Constraint;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.DbConfig;
import de.joergdev.mosy.shared.Utils;

public class DbConfigDAO extends AbstractDAO
{
  public DbConfig get()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from db_config ");

    Query q = entityMgr.createNativeQuery(sql.toString(), DbConfig.class);

    return getSingleResult(q);
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
    sqlDrop.append(" ALTER TABLE ").append(constraint.getTable()).append(" DROP CONSTRAINT ").append(constraint.getName());

    Query q = entityMgr.createNativeQuery(sqlDrop.toString());

    q.executeUpdate();

    // Then recreate with new sql alter command
    q = entityMgr.createNativeQuery(constraint.getSql());

    q.executeUpdate();
  }
}
