package de.joergdev.mosy.backend.persistence;

import java.util.Objects;
import jakarta.persistence.EntityManager;

public class EntityManagerProviderService
{
  private static final EntityManagerProviderService INSTANCE = new EntityManagerProviderService();

  private EntityManagerProvider entityManagerProvider;

  private EntityManagerProviderService()
  {

  }

  public static EntityManagerProviderService getInstance()
  {
    return INSTANCE;
  }

  public EntityManagerProvider getEntityManagerProvider()
  {
    return entityManagerProvider;
  }

  public void setEntityManagerProvider(EntityManagerProvider entityManagerProvider)
  {
    this.entityManagerProvider = entityManagerProvider;
  }

  public EntityManager getEntityManager()
  {
    Objects.requireNonNull(entityManagerProvider, "entityManagerProvider not set");

    return entityManagerProvider.getEntityManager();
  }

  public void releaseEntityManager(EntityManager em)
  {
    Objects.requireNonNull(entityManagerProvider, "entityManagerProvider not set");

    entityManagerProvider.releaseEntityManager(em);
  }

  public void rollbackEntityManager(EntityManager em)
  {
    Objects.requireNonNull(entityManagerProvider, "entityManagerProvider not set");

    entityManagerProvider.rollbackEntityManager(em);
  }

  public boolean isContainerManaged()
  {
    Objects.requireNonNull(entityManagerProvider, "entityManagerProvider not set");

    return entityManagerProvider.isContainerManaged();
  }
}