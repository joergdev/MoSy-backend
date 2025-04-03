package de.joergdev.mosy.backend.bl.core;

import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.backend.persistence.model.TenantScoped;

public class BLEntityManager implements EntityManager
{
  private final EntityManager delegate;
  private final Integer tenantId;

  public BLEntityManager(EntityManager delegate, Integer tenantId)
  {
    this.delegate = delegate;
    this.tenantId = tenantId;
  }

  private Tenant getTenant()
  {
    if (tenantId != null)
    {
      Tenant tenant = new Tenant();
      tenant.setTenantId(tenantId);

      return tenant;
    }

    return null;
  }

  private void setTenantForTenantScopedEntity(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    if (entity instanceof TenantScoped && ((TenantScoped) entity).getTenant() == null)
    {
      ((TenantScoped) entity).setTenant(getTenant());
    }
  }

  @Override
  public void persist(Object entity)
  {
    setTenantForTenantScopedEntity(entity);

    delegate.persist(entity);
  }

  @Override
  public <T> T merge(T entity)
  {
    setTenantForTenantScopedEntity(entity);

    return delegate.merge(entity);
  }

  @Override
  public void remove(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.remove(entity);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.find(entityClass, primaryKey), tenantId);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.find(entityClass, primaryKey, properties), tenantId);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.find(entityClass, primaryKey, lockMode), tenantId);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.find(entityClass, primaryKey, lockMode, properties), tenantId);
  }

  @Override
  public <T> T getReference(Class<T> entityClass, Object primaryKey)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.getReference(entityClass, primaryKey), tenantId);
  }

  @Override
  public void flush()
  {
    delegate.flush();
  }

  @Override
  public void setFlushMode(FlushModeType flushMode)
  {
    delegate.setFlushMode(flushMode);
  }

  @Override
  public FlushModeType getFlushMode()
  {
    return delegate.getFlushMode();
  }

  @Override
  public void lock(Object entity, LockModeType lockMode)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.lock(entity, lockMode);
  }

  @Override
  public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.lock(entity, lockMode, properties);
  }

  @Override
  public void refresh(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.refresh(entity);
  }

  @Override
  public void refresh(Object entity, Map<String, Object> properties)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.refresh(entity, properties);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.refresh(entity, lockMode);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.refresh(entity, lockMode, properties);
  }

  @Override
  public void clear()
  {
    delegate.clear();
  }

  @Override
  public void detach(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    delegate.detach(entity);
  }

  @Override
  public boolean contains(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    return delegate.contains(entity);
  }

  @Override
  public LockModeType getLockMode(Object entity)
  {
    TenancyUtils.checkTenantAccessForDbEntity(entity, tenantId);

    return delegate.getLockMode(entity);
  }

  @Override
  public void setProperty(String propertyName, Object value)
  {
    delegate.setProperty(propertyName, value);
  }

  @Override
  public Map<String, Object> getProperties()
  {
    return delegate.getProperties();
  }

  @Override
  public Query createQuery(String qlString)
  {
    return delegate.createQuery(qlString);
  }

  @Override
  public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery)
  {
    return delegate.createQuery(criteriaQuery);
  }

  @Override
  public Query createQuery(CriteriaUpdate updateQuery)
  {
    return delegate.createQuery(updateQuery);
  }

  @Override
  public Query createQuery(CriteriaDelete deleteQuery)
  {
    return delegate.createQuery(deleteQuery);
  }

  @Override
  public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass)
  {
    return delegate.createQuery(qlString, resultClass);
  }

  @Override
  public Query createNamedQuery(String name)
  {
    return delegate.createNamedQuery(name);
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass)
  {
    return delegate.createNamedQuery(name, resultClass);
  }

  @Override
  public Query createNativeQuery(String sqlString)
  {
    return delegate.createNativeQuery(sqlString);
  }

  @Override
  public Query createNativeQuery(String sqlString, Class resultClass)
  {
    return delegate.createNativeQuery(sqlString, resultClass);
  }

  @Override
  public Query createNativeQuery(String sqlString, String resultSetMapping)
  {
    return delegate.createNativeQuery(sqlString, resultSetMapping);
  }

  @Override
  public StoredProcedureQuery createNamedStoredProcedureQuery(String name)
  {
    return delegate.createNamedStoredProcedureQuery(name);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(String procedureName)
  {
    return delegate.createStoredProcedureQuery(procedureName);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses)
  {
    return delegate.createStoredProcedureQuery(procedureName, resultClasses);
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings)
  {
    return delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
  }

  @Override
  public void joinTransaction()
  {
    delegate.joinTransaction();
  }

  @Override
  public boolean isJoinedToTransaction()
  {
    return delegate.isJoinedToTransaction();
  }

  @Override
  public <T> T unwrap(Class<T> cls)
  {
    return TenancyUtils.checkTenantAccessForDbEntity(delegate.unwrap(cls), tenantId);
  }

  @Override
  public Object getDelegate()
  {
    return delegate.getDelegate();
  }

  @Override
  public void close()
  {
    delegate.close();
  }

  @Override
  public boolean isOpen()
  {
    return delegate.isOpen();
  }

  @Override
  public EntityTransaction getTransaction()
  {
    return delegate.getTransaction();
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory()
  {
    return delegate.getEntityManagerFactory();
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder()
  {
    return delegate.getCriteriaBuilder();
  }

  @Override
  public Metamodel getMetamodel()
  {
    return delegate.getMetamodel();
  }

  @Override
  public <T> EntityGraph<T> createEntityGraph(Class<T> rootType)
  {
    return delegate.createEntityGraph(rootType);
  }

  @Override
  public EntityGraph<?> createEntityGraph(String graphName)
  {
    return delegate.createEntityGraph(graphName);
  }

  @Override
  public EntityGraph<?> getEntityGraph(String graphName)
  {
    return delegate.getEntityGraph(graphName);
  }

  @Override
  public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass)
  {
    return delegate.getEntityGraphs(entityClass);
  }
}
