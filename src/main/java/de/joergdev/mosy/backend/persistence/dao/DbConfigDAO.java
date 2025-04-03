package de.joergdev.mosy.backend.persistence.dao;

import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.DbConfig;

public class DbConfigDAO extends AbstractDAO
{
  public DbConfig get()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from db_config ");

    Query q = entityMgr.createNativeQuery(sql.toString(), DbConfig.class);

    return getSingleResult(q);
  }
}
