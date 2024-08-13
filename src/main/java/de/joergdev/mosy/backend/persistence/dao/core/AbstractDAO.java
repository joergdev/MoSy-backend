package de.joergdev.mosy.backend.persistence.dao.core;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
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
        TenancyUtils.checkTenantAccessForDbEntity(result, tenantId);
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
