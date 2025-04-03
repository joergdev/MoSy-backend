package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.Interface;

public class InterfaceDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<Interface> getAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from INTERFACE  ");
    sql.append(" where tenant_id = :tenant_id ");
    sql.append(" order by INTERFACE_TYPE_ID, NAME ");

    Query q = entityMgr.createNativeQuery(sql.toString(), Interface.class);

    q.setParameter("tenant_id", tenantId);

    return q.getResultList();
  }

  public void setValuesOnStartup()
  {
    Map<String, Object> params = new HashMap<>();

    StringBuilder sql = new StringBuilder();
    sql.append(" update interface set MOCK_ACTIVE = MOCK_ACTIVE_ON_STARTUP ");

    if (tenantId != null)
    {
      sql.append(" where tenant_id = :tenant_id ");

      params.put("tenant_id", tenantId);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());

    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    executeUpdate(q);
  }

  public Interface getByServicePath(String servicePath, boolean servicePathStartsWith)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(null, servicePath, servicePathStartsWith, null);
  }

  public Interface getByName(String name)
  {
    Objects.requireNonNull(name, "name");

    return getBySearchParams(name, null, false, null);
  }

  public boolean existsByServicePath(String servicePath, boolean servicePathStartsWith, Integer exceptID)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(null, servicePath, servicePathStartsWith, exceptID) != null;
  }

  public boolean existsByName(String name, Integer exceptID)
  {
    Objects.requireNonNull(name, "name");

    return getBySearchParams(name, null, false, exceptID) != null;
  }

  public Interface getBySearchParams(String name, String servicePath, boolean servicePathStartsWith, Integer exceptID)
  {
    if (name == null && servicePath == null)
    {
      throw new IllegalArgumentException("no search param");
    }

    StringBuilder sql = new StringBuilder();
    Map<String, Object> params = new HashMap<>();

    sql.append(" select * from INTERFACE ");
    sql.append(" where tenant_id = :tenant_id ");

    params.put("tenant_id", tenantId);

    if (name != null)
    {
      sql.append(" and name = :name ");
      params.put("name", name);
    }

    if (servicePath != null)
    {
      sql.append(" and ");

      if (servicePathStartsWith)
      {
        // for example: "http://restservice/cars/1/wheels like http://restservice/cars%"
        sql.append(" (:svc_path like SERVICE_PATH || '%' or SERVICE_PATH || '%' like :svc_path) ");
      }
      else
      {
        sql.append(" SERVICE_PATH = :svc_path ");
      }

      params.put("svc_path", servicePath);
    }

    if (exceptID != null)
    {
      sql.append(" and interface_id != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), Interface.class);
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return getSingleResult(q);
  }
}
