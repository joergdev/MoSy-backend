package com.github.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;

public class MockDataDAO extends AbstractDAO
{
  public void increaseCountCalls(Integer id)
  {
    Objects.requireNonNull(id, "id");

    StringBuilder sql = new StringBuilder();
    sql.append(" update MOCK_DATA set COUNT_CALLS = COUNT_CALLS+1 where MOCK_DATA_ID = :id ");

    Query q = entityMgr.createNativeQuery(sql.toString());
    q.setParameter("id", id);

    executeUpdate(q);
  }

  public void setValuesOnStartup()
  {
    StringBuilder sql = new StringBuilder();
    sql.append(" update MOCK_DATA set COUNT_CALLS = 0 ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    executeUpdate(q);
  }

  public boolean existsByTitle(Integer interfaceMethodId, String title, Integer exceptID)
  {
    Objects.requireNonNull(interfaceMethodId, "interfaceMethodId");
    Objects.requireNonNull(title, "title");

    StringBuilder sql = new StringBuilder();

    Map<String, Object> params = new HashMap<>();
    params.put("im_id", interfaceMethodId);
    params.put("title", title);

    sql.append(" select 1 from MOCK_DATA ");
    sql.append(" where interface_method_id = :im_id ");
    sql.append(" and title = :title ");

    if (exceptID != null)
    {
      sql.append(" and mock_data_id != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return getSingleResult(q) != null;
  }

  public int deleteMockDataDedicatedForMockProfile(Integer mockProfileID)
  {
    Objects.requireNonNull(mockProfileID, "mockProfileID");

    StringBuilder sql = new StringBuilder();

    sql.append(" delete from MOCK_DATA md ");
    sql.append(" where md.COMMON = 0 ");
    sql.append(" and not exists ( ");
    sql.append("  select 1 from MOCK_DATA_MOCK_PROFILE mdmp ");
    sql.append("  where mdmp.MOCK_DATA_ID = md.MOCK_DATA_ID ");
    sql.append("  and mdmp.MOCK_PROFILE_ID != :mp_id ");
    sql.append(" ) ");

    Map<String, Object> params = new HashMap<>();
    params.put("mp_id", mockProfileID);

    Query q = entityMgr.createNativeQuery(sql.toString());
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    return q.executeUpdate();
  }
}