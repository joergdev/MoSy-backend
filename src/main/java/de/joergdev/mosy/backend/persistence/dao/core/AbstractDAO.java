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
    List<T> result = getResultList(q);

    return Utils.isCollectionEmpty(result)
        ? null
        : result.get(0);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getResultList(Query q)
  {
    return q.getResultList();
  }

  public int executeUpdate(Query q)
  {
    int result = q.executeUpdate();

    // clear cached entities cause of may changed by update
    entityMgr.clear();

    return result;
  }
}