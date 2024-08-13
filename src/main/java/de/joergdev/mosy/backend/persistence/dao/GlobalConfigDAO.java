package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.GlobalConfig;

public class GlobalConfigDAO extends AbstractDAO
{
  public GlobalConfig get()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from global_config where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString(), GlobalConfig.class);

    q.setParameter("tenant_id", tenantId);

    return getSingleResult(q);
  }

  /**
   * Reset mockActive Flag to startup Flag.
   */
  public void setValuesOnStartup()
  {
    Map<String, Object> params = new HashMap<>();

    StringBuilder sql = new StringBuilder();
    sql.append(" update global_config set MOCK_ACTIVE = MOCK_ACTIVE_ON_STARTUP ");

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
