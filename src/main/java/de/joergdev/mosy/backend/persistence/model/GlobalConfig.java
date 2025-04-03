package de.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GLOBAL_CONFIG")
public class GlobalConfig implements TenantScoped
{
  @Column(name = "CREATED", updatable = false)
  @Id
  private LocalDateTime created;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

  @Column(name = "MOCK_ACTIVE_ON_STARTUP", length = 1, columnDefinition = "INTEGER")
  private Boolean mockActiveOnStartup;

  @Column(name = "MOCK_ACTIVE", length = 1, columnDefinition = "INTEGER")
  private Boolean mockActive;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, columnDefinition = "INTEGER")
  private Boolean routingOnNoMockData;

  @Column(name = "TTL_RECORD_SESSION")
  private Integer ttlRecordSession;

  @Column(name = "TTL_MOCK_PROFILE")
  private Integer ttlMockProfile;

  public Boolean getMockActiveOnStartup()
  {
    return mockActiveOnStartup;
  }

  public void setMockActiveOnStartup(Boolean mockActiveOnStartup)
  {
    this.mockActiveOnStartup = mockActiveOnStartup;
  }

  public Boolean getMockActive()
  {
    return mockActive;
  }

  public void setMockActive(Boolean mockActive)
  {
    this.mockActive = mockActive;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public Boolean getRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(Boolean routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  /**
   * @return the ttlRecordSession
   */
  public Integer getTtlRecordSession()
  {
    return ttlRecordSession;
  }

  /**
   * @param ttlRecordSession the ttlRecordSession to set
   */
  public void setTtlRecordSession(Integer ttlRecordSession)
  {
    this.ttlRecordSession = ttlRecordSession;
  }

  /**
   * @return the ttlMockProfile
   */
  public Integer getTtlMockProfile()
  {
    return ttlMockProfile;
  }

  /**
   * @param ttlMockProfile the ttlMockProfile to set
   */
  public void setTtlMockProfile(Integer ttlMockProfile)
  {
    this.ttlMockProfile = ttlMockProfile;
  }

  @Override
  public Tenant getTenant()
  {
    return tenant;
  }

  @Override
  public void setTenant(Tenant tenant)
  {
    this.tenant = tenant;
  }
}
