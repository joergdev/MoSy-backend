package de.joergdev.mosy.backend.persistence.model;

public interface TenantScoped
{
  Tenant getTenant();

  void setTenant(Tenant tenant);
}
