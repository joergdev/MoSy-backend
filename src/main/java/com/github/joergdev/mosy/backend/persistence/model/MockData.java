package com.github.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MOCK_DATA")
public class MockData
{
  public static final int LENGTH_TITLE = 200;
  public static final int LENGTH_REQUEST = 10000;

  @Column(name = "MOCK_DATA_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockDataId;

  @Column(name = "TITLE", length = LENGTH_TITLE)
  private String title;

  @Column(name = "CREATED", nullable = false, updatable = false)
  private LocalDateTime created;

  @Column(name = "ACTIVE", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean active;

  @Column(name = "COUNT_CALLS", nullable = false, updatable = false)
  private Integer countCalls;

  @Column(name = "REQUEST", length = LENGTH_REQUEST, nullable = true)
  private String request;

  @Column(name = "RESPONSE", nullable = false)
  private String response;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  private InterfaceMethod interfaceMethod;

  @ManyToOne
  @JoinColumn(name = "MOCK_SESSION_ID")
  private MockSession mockSession;

  public Integer getMockDataId()
  {
    return mockDataId;
  }

  public void setMockDataId(Integer mockDataId)
  {
    this.mockDataId = mockDataId;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public Boolean getActive()
  {
    return active;
  }

  public void setActive(Boolean active)
  {
    this.active = active;
  }

  public Integer getCountCalls()
  {
    return countCalls;
  }

  public void setCountCalls(Integer countCalls)
  {
    this.countCalls = countCalls;
  }

  public InterfaceMethod getInterfaceMethod()
  {
    return interfaceMethod;
  }

  public void setInterfaceMethod(InterfaceMethod interfaceMethod)
  {
    this.interfaceMethod = interfaceMethod;
  }

  public String getRequest()
  {
    return request;
  }

  public void setRequest(String request)
  {
    this.request = request;
  }

  public String getResponse()
  {
    return response;
  }

  public void setResponse(String response)
  {
    this.response = response;
  }

  public MockSession getMockSession()
  {
    return mockSession;
  }

  public void setMockSession(MockSession mockSession)
  {
    this.mockSession = mockSession;
  }
}