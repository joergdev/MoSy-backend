package de.joergdev.mosy.backend.persistence.dao;

import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;

public class MigrationDao extends AbstractDAO
{
  public void migrateServicePathIntern()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" update interface_method ");
    sql.append(" set SERVICE_PATH_INTERN = SERVICE_PATH ");
    sql.append(" where SERVICE_PATH_INTERN is null ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }

  public void setMockDataResponseNullable()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" ALTER TABLE MOCK_DATA ALTER COLUMN RESPONSE DROP NOT NULL ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.executeUpdate();
  }

  public void setRecordRequestNullable()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" ALTER TABLE RECORD ALTER COLUMN REQUEST_DATA DROP NOT NULL ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.executeUpdate();
  }

  public void setRecordResponseNullable()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" ALTER TABLE RECORD ALTER COLUMN RESPONSE DROP NOT NULL ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.executeUpdate();
  }
}
