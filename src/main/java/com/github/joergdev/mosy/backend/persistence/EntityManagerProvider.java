package com.github.joergdev.mosy.backend.persistence;

import javax.persistence.EntityManager;

public interface EntityManagerProvider
{
  EntityManager getEntityManager();

  void releaseEntityManager(EntityManager em);

  boolean isContainerManaged();
}