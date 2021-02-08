package com.github.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GLOBAL_CONFIG")
public class GlobalConfig
{
  @Column(name = "CREATED", updatable = false)
  @Id
  private LocalDateTime created;

  @Column(name = "MOCK_ACTIVE_ON_STARTUP", columnDefinition = "INT(1)")
  private Boolean mockActiveOnStartup;

  @Column(name = "MOCK_ACTIVE", columnDefinition = "INT(1)")
  private Boolean mockActive;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, columnDefinition = "INT(1)")
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
}