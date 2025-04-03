package de.joergdev.mosy.backend.persistence.dao.core;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.shared.Utils;

public class AbstractDAO
{
  protected EntityManager entityMgr;
  protected Integer tenantId;

  public EntityManager getEntityMgr()
  {
    return entityMgr;
  }

  public void setEntityMgr(EntityManager entityMgr)
  {
    this.entityMgr = entityMgr;
  }

  public Integer getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(Integer tenantId)
  {
    this.tenantId = tenantId;
  }

  public <T> T getSingleResult(Query q)
  {
    List<T> result = getResultList(q);

    return Utils.isCollectionEmpty(result) ? null : result.get(0);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getResultList(Query q)
  {
    List<T> resultList = q.getResultList();

    if (resultList != null)
    {
      for (T result : resultList)
      {
        // No Check if List of Tenants => in this case we assume its correct to return tenants that are not the current tenant
        // for example to check if tenant exists by name
        if (result instanceof Tenant == false)
        {
          TenancyUtils.checkTenantAccessForDbEntity(result, tenantId);
        }
      }
    }

    return resultList;
  }

  public int executeUpdate(Query q)
  {
    int result = q.executeUpdate();

    // clear cached entities cause of may changed by update
    entityMgr.clear();

    return result;
  }
}
