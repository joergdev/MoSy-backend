package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.RecordConfig;

public class RecordConfigDAO extends AbstractDAO
{
  public RecordConfig getGlobal()
  {
    StringBuilder sql = new StringBuilder();

    sql.append(" select rc.* from RECORD_CONFIG rc ");
    sql.append(" where tenant_id = :tenant_id and rc.INTERFACE_ID is null and rc.INTERFACE_METHOD_ID is null ");

    Query q = entityMgr.createNativeQuery(sql.toString(), RecordConfig.class);

    q.setParameter("tenant_id", tenantId);

    return getSingleResult(q);
  }

  public RecordConfig getByInterfaceId(Integer id)
  {
    Objects.requireNonNull(id, "id");

    StringBuilder sql = new StringBuilder();

    sql.append(" select rc.* from RECORD_CONFIG rc ");
    sql.append(" where rc.INTERFACE_ID = :id and rc.INTERFACE_METHOD_ID is null ");

    Query q = entityMgr.createNativeQuery(sql.toString(), RecordConfig.class);
    q.setParameter("id", id);

    return getSingleResult(q);
  }

  public RecordConfig getByInterfaceMethodId(Integer id)
  {
    return getByInterfaceMethodIdRequestdata(id, null, null);
  }

  public RecordConfig getByInterfaceMethodIdRequestData(Integer id, String requestData, Integer exceptID)
  {
    Objects.requireNonNull(requestData, "requestData");

    return getByInterfaceMethodIdRequestdata(id, requestData, exceptID);
  }

  private RecordConfig getByInterfaceMethodIdRequestdata(Integer id, String requestData, Integer exceptID)
  {
    Objects.requireNonNull(id, "id");

    StringBuilder sql = new StringBuilder();

    sql.append(" select rc.* from RECORD_CONFIG rc ");
    sql.append(" where rc.INTERFACE_METHOD_ID = :id ");
    sql.append(" and rc.INTERFACE_ID is null ");

    if (requestData == null)
    {
      sql.append(" and rc.REQUEST_DATA is null ");
    }
    else
    {
      sql.append(" and rc.REQUEST_DATA = :req_data ");
    }

    if (exceptID != null)
    {
      sql.append(" and rc.RECORD_CONFIG_ID != :exc_id ");
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), RecordConfig.class);
    q.setParameter("id", id);

    if (requestData != null)
    {
      q.setParameter("req_data", requestData);
    }

    if (exceptID != null)
    {
      q.setParameter("exc_id", exceptID);
    }

    return getSingleResult(q);
  }

  public boolean existsByTitle(Integer interfaceMethodId, String title, Integer exceptID)
  {
    Objects.requireNonNull(interfaceMethodId, "interfaceMethodId");
    Objects.requireNonNull(title, "title");

    StringBuilder sql = new StringBuilder();

    Map<String, Object> params = new HashMap<>();
    params.put("im_id", interfaceMethodId);
    params.put("title", title);

    sql.append(" select 1 from RECORD_CONFIG ");
    sql.append(" where interface_method_id = :im_id ");
    sql.append(" and title = :title ");

    if (exceptID != null)
    {
      sql.append(" and RECORD_CONFIG_ID != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return getSingleResult(q) != null;
  }
}
