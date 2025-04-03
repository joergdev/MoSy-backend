package de.joergdev.mosy.backend.persistence;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider
{
  EntityManager getEntityManager();

  void releaseEntityManager(EntityManager em);

  void rollbackEntityManager(EntityManager em);

  boolean isContainerManaged();
}