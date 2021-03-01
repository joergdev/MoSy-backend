package de.joergdev.mosy.backend.persistence.dao.core;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import de.joergdev.mosy.shared.Utils;

public class AbstractDAO
{
  protected EntityManager entityMgr;

  public EntityManager getEntityMgr()
  {
    return entityMgr;
  }

  public void setEntityMgr(EntityManager entityMgr)
  {
    this.entityMgr = entityMgr;
  }

  public <T> T getSingleResult(Query q)
  {
    @SuppressWarnings("unchecked")
    List<T> result = q.getResultList();

    return Utils.isCollectionEmpty(result)
        ? null
        : result.get(0);
  }

  public int executeUpdate(Query q)
  {
    int result = q.executeUpdate();

    // clear cached entities cause of may changed by update
    entityMgr.clear();

    return result;
  }
}