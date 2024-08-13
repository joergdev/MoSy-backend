package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Query;
import de.joergdev.mosy.backend.Config;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.Tenant;

public class TenantDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<Tenant> getAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from TENANT ");
    sql.append(" where NAME != :dummy_name_non_multi_tanency ");
    sql.append(" order by NAME ");

    Query q = entityMgr.createNativeQuery(sql.toString(), Tenant.class);
    q.setParameter("dummy_name_non_multi_tanency", Config.DUMMY_TENANT_NAME_NON_MULTI_TENANCY);

    return q.getResultList();
  }

  public boolean existsByName(String name, Integer exceptID)
  {
    return getByName(name, exceptID) != null;
  }

  public Tenant getByName(String name, Integer exceptID)
  {
    Objects.requireNonNull(name, "name");

    Map<String, Object> params = new HashMap<>();
    params.put("name", name);

    StringBuilder sql = new StringBuilder();
    sql.append(" select * from TENANT ");
    sql.append(" where name = :name ");

    if (exceptID != null)
    {
      sql.append(" and tenant_id != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), Tenant.class);

    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return getSingleResult(q);
  }
}
