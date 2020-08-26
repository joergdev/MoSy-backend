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

  @Column(name = "TTL_MOCK_SESSION")
  private Integer ttlMockSession;

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

  public Integer getTtlMockSession()
  {
    return ttlMockSession;
  }

  public void setTtlMockSession(Integer ttlMockSession)
  {
    this.ttlMockSession = ttlMockSession;
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
}