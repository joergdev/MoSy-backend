package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.MockProfile;
import de.joergdev.mosy.shared.Utils;

public class MockProfileDao extends AbstractDAO
{
  @SuppressWarnings("unchecked")
  public List<MockProfile> getAll(Integer loadCount, Integer lastLoadedId)
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" select * from MOCK_PROFILE mp ");
    sql.append(" where tenant_id = :tenant_id ");

    if (loadCount != null || lastLoadedId != null)
    {
      sql.append(" where mp.MOCK_PROFILE_ID in ( ");
      sql.append("    select mp2.MOCK_PROFILE_ID from MOCK_PROFILE mp2 ");

      if (lastLoadedId != null)
      {
        sql.append("  where mp2.MOCK_PROFILE_ID < :last_load_id ");
      }

      sql.append("    order by mp2.MOCK_PROFILE_ID desc ");
      sql.append(" ) ");

      if (loadCount != null)
      {
        sql.append(" and ROWNUM() <= :load_count ");
      }
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), MockProfile.class);

    q.setParameter("tenant_id", tenantId);

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
    sql.append("select count(mock_profile_id) from mock_profile ");
    sql.append(" where tenant_id = :tenant_id ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    q.setParameter("tenant_id", tenantId);

    return Utils.bigInteger2Integer(getSingleResult(q));
  }

  public void clearAllNonPersistent()
  {
    Map<String, Object> params = new HashMap<>();

    StringBuilder sql = new StringBuilder();
    sql.append(" delete from mock_profile where persistent = 0 ");

    if (tenantId != null)
    {
      sql.append(" and tenant_id = :tenant_id ");

      params.put("tenant_id", tenantId);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());

    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    executeUpdate(q);
  }

  public boolean existsByName(String name, Integer exceptID)
  {
    return getByName(name, exceptID) != null;
  }

  public MockProfile getByName(String name, Integer exceptID)
  {
    Objects.requireNonNull(name, "name");

    StringBuilder sql = new StringBuilder();

    Map<String, Object> params = new HashMap<>();
    params.put("tenant_id", tenantId);
    params.put("name", name.toUpperCase());

    sql.append(" select * from MOCK_PROFILE");
    sql.append(" where tenant_id = :tenant_id and UPPER(name) = :name ");

    if (exceptID != null)
    {
      sql.append(" and mock_profile_id != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), MockProfile.class);
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return getSingleResult(q);
  }
}
