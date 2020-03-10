package com.github.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "MOCK_SESSION")
public class MockSession
{
  @Column(name = "MOCK_SESSION_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockSessionID;

  @Column(name = "CREATED")
  private LocalDateTime created;

  @OneToMany(mappedBy = "mockSession", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockData> mockData;

  public Integer getMockSessionID()
  {
    return mockSessionID;
  }

  public void setMockSessionID(Integer mockSessionID)
  {
    this.mockSessionID = mockSessionID;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public List<MockData> getMockData()
  {
    return mockData;
  }

  public void setMockData(List<MockData> mockData)
  {
    this.mockData = mockData;
  }
}