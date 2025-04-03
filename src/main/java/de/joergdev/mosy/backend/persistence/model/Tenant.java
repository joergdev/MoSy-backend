package de.joergdev.mosy.backend.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @since 4.0
 */
@Entity
@Table(name = "TENANT")
public class Tenant
{
  public static final int LENGTH_NAME = 100;

  @Column(name = "TENANT_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer tenantId;

  @Column(name = "NAME", length = LENGTH_NAME, nullable = false)
  private String name;

  @Column(name = "SECRET_HASH", nullable = false)
  private Integer secretHash;

  public Integer getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(Integer tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Integer getSecretHash()
  {
    return secretHash;
  }

  public void setSecretHash(Integer secretHash)
  {
    this.secretHash = secretHash;
  }
}
