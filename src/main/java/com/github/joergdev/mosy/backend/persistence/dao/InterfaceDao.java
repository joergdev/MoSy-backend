package com.github.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import com.github.joergdev.mosy.backend.persistence.model.Interface;

public class InterfaceDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<Interface> getAll()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from INTERFACE order by INTERFACE_TYPE_ID, NAME ");

    Query q = entityMgr.createNativeQuery(sql.toString(), Interface.class);

    return q.getResultList();
  }

  public void setValuesOnStartup()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" update interface set MOCK_DISABLED = MOCK_DISABLED_ON_STARTUP ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }

  public Interface getByServicePath(String servicePath)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(null, servicePath, null);
  }

  public boolean existsByServicePath(String servicePath, Integer exceptID)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(null, servicePath, exceptID) != null;
  }

  public boolean existsByName(String name, Integer exceptID)
  {
    Objects.requireNonNull(name, "name");

    return getBySearchParams(name, null, exceptID) != null;
  }

  public Interface getBySearchParams(String name, String servicePath, Integer exceptID)
  {
    if (name == null && servicePath == null)
    {
      throw new IllegalArgumentException("no search param");
    }

    StringBuilder sql = new StringBuilder();
    Map<String, Object> params = new HashMap<>();
    boolean needsAnd = false;

    sql.append(" select * from INTERFACE ");
    sql.append(" where ");

    if (name != null)
    {
      sql.append(" name = :name ");
      params.put("name", name);

      needsAnd = true;
    }

    if (servicePath != null)
    {
      if (needsAnd)
      {
        sql.append(" and ");
      }

      sql.append(" SERVICE_PATH = :svc_path ");
      params.put("svc_path", servicePath);

      needsAnd = true;
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