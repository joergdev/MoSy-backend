package de.joergdev.mosy.backend.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Query;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.shared.Utils;

public class InterfaceMethodDAO extends AbstractDAO
{
  public void increaseCountCalls(Integer id)
  {
    Objects.requireNonNull(id, "id");

    StringBuilder sql = new StringBuilder();
    sql.append(" update interface_method set COUNT_CALLS = COUNT_CALLS+1 where interface_method_id = :id ");

    Query q = entityMgr.createNativeQuery(sql.toString());
    q.setParameter("id", id);

    executeUpdate(q);
  }

  public void setValuesOnStartup()
  {
    Map<String, Object> params = new HashMap<>();

    StringBuilder sql = new StringBuilder();
    sql.append(" update interface_method set MOCK_ACTIVE = MOCK_ACTIVE_ON_STARTUP, COUNT_CALLS = 0 ");

    if (tenantId != null)
    {
      sql.append(" where tenant_id = :tenant_id ");

      params.put("tenant_id", tenantId);
    }

    Query q = entityMgr.createNativeQuery(sql.toString());

    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    executeUpdate(q);
  }

  public Boolean isRecordEnabled(Integer id)
  {
    Objects.requireNonNull(id, "id");

    StringBuilder sql = new StringBuilder();

    sql.append(" select rc.enabled from RECORD_CONFIG rc ");
    sql.append(" where rc.INTERFACE_METHOD_ID = :id ");
    sql.append(" and rc.INTERFACE_ID is null and rc.REQUEST_DATA is null ");

    Query q = entityMgr.createNativeQuery(sql.toString());
    q.setParameter("id", id);

    @SuppressWarnings("unchecked")
    List<Integer> resultList = q.getResultList();

    return Utils.isCollectionEmpty(resultList) ? null : Integer.valueOf(1).equals(Utils.getFirstElementOfCollection(resultList));
  }

  public InterfaceMethod getByServicePath(Integer interfaceId, String servicePath, boolean nonStrictSearchServicePath, HttpMethod httpMethod)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(interfaceId, null, servicePath, nonStrictSearchServicePath, httpMethod, null);
  }

  public boolean existsByInterfaceIdServicePath(Integer interfaceId, String servicePath, boolean nonStrictSearchServicePath, HttpMethod httpMethod,
                                                Integer exceptID)
  {
    Objects.requireNonNull(servicePath, "servicePath");

    return getBySearchParams(interfaceId, null, servicePath, nonStrictSearchServicePath, httpMethod, exceptID) != null;
  }

  public boolean existsByInterfaceIdName(Integer interfaceId, String name, Integer exceptID)
  {
    Objects.requireNonNull(name, "name");

    return getBySearchParams(interfaceId, name, null, false, null, exceptID) != null;
  }

  public InterfaceMethod getBySearchParams(Integer interfaceId, String name, String servicePath, boolean nonStrictSearchServicePath, HttpMethod httpMethod,
                                           Integer exceptID)
  {
    Objects.requireNonNull(interfaceId, "interfaceId");

    if (name == null && servicePath == null)
    {
      throw new IllegalArgumentException("no search param");
    }

    StringBuilder sql = new StringBuilder();

    Map<String, Object> params = new HashMap<>();
    params.put("i_id", interfaceId);

    sql.append(" select * from INTERFACE_METHOD ");
    sql.append(" where interface_id = :i_id ");

    if (name != null)
    {
      sql.append(" and name = :name ");
      params.put("name", name);
    }

    if (servicePath != null)
    {
      sql.append(" and :svc_path like SERVICE_PATH_INTERN ");
      params.put("svc_path", servicePath);
    }

    if (httpMethod != null)
    {
      sql.append(" and HTTP_METHOD = :http_method ");
      params.put("http_method", httpMethod.name());
    }

    if (exceptID != null)
    {
      sql.append(" and interface_method_id != :id ");
      params.put("id", exceptID);
    }

    Query q = entityMgr.createNativeQuery(sql.toString(), InterfaceMethod.class);
    params.entrySet().forEach(e -> q.setParameter(e.getKey(), e.getValue()));

    // search by name
    if (Utils.isEmpty(servicePath))
    {
      return getSingleResult(q);
    }
    // search by servicePath
    else
    {
      List<InterfaceMethod> methodsFound = getResultList(q);

      int cntPartsSvcPath = servicePath.split("/").length;

      InterfaceMethod methodFound = null;
      int cntPartsSvcPathMethodFound = -1;

      for (InterfaceMethod dbMethod : methodsFound)
      {
        int cntPartsSvcPathDbMethod = dbMethod.getServicePathIntern().split("/").length;

        // non-strict
        // get method most matching
        // for example:
        //   search input servicePath: cars/123/wheels/456
        //   method1 servicePath:      cars/{id}
        //   method2 servicePath:      cars/{id}/wheels/{w-id}0 => match
        if (nonStrictSearchServicePath)
        {
          if (cntPartsSvcPathDbMethod > cntPartsSvcPathMethodFound)
          {
            methodFound = dbMethod;
            cntPartsSvcPathMethodFound = cntPartsSvcPathDbMethod;
          }
        }
        // strict
        // testsvc/123 and testsvc/* matches
        else
        {
          if (cntPartsSvcPathDbMethod == cntPartsSvcPath)
          {
            return dbMethod;
          }
        }
      }

      return methodFound;
    }
  }
}
