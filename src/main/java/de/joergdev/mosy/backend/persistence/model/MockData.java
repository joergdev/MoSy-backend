package de.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "MOCK_DATA")
public class MockData implements TenantScoped
{
  public static final int LENGTH_TITLE = 200;
  public static final int LENGTH_REQUEST = 500000;
  public static final int LENGTH_RESPONSE = 500000;

  @Column(name = "MOCK_DATA_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockDataId;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  private Tenant tenant;

  @Column(name = "TITLE", length = LENGTH_TITLE)
  private String title;

  @Column(name = "CREATED", nullable = false, updatable = false)
  private LocalDateTime created;

  @Column(name = "ACTIVE", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean active;

  @Column(name = "COMMON", length = 1, nullable = false, columnDefinition = "INT(1) default 0")
  private Boolean common;

  @Column(name = "COUNT_CALLS", nullable = false, updatable = false)
  private Integer countCalls;

  @Column(name = "REQUEST", length = LENGTH_REQUEST, nullable = true)
  private String request;

  @Column(name = "RESPONSE", length = LENGTH_RESPONSE, nullable = true)
  private String response;

  @Column(name = "HTTP_RETURN_CODE", nullable = true, columnDefinition = "INT(4) default null")
  private Integer httpReturnCode;

  @Column(name = "REQUEST_HASH", nullable = false, columnDefinition = "INT(11) default 0")
  private Integer requestHash;

  @Column(name = "RESPONSE_HASH", nullable = false, columnDefinition = "INT(11) default 0")
  private Integer responseHash;

  @Column(name = "DELAY", nullable = true)
  private Long delay;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  private InterfaceMethod interfaceMethod;

  @OneToMany(mappedBy = "mockData", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockDataMockProfile> mockProfiles;

  @OneToMany(mappedBy = "mockData", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockDataPathParam> pathParams;

  @OneToMany(mappedBy = "mockData", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockDataUrlArgument> urlArguments;

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

  /**
   * @return the mockProfiles
   */
  public List<MockDataMockProfile> getMockProfiles()
  {
    return mockProfiles;
  }

  /**
   * @param mockProfiles the mockProfiles to set
   */
  public void setMockProfiles(List<MockDataMockProfile> mockProfiles)
  {
    this.mockProfiles = mockProfiles;
  }

  /**
   * @return the common
   */
  public Boolean getCommon()
  {
    return common;
  }

  /**
   * @param common the common to set
   */
  public void setCommon(Boolean common)
  {
    this.common = common;
  }

  /**
   * @return the requestHash
   */
  public Integer getRequestHash()
  {
    return requestHash;
  }

  /**
   * @param requestHash the requestHash to set
   */
  public void setRequestHash(Integer requestHash)
  {
    this.requestHash = requestHash;
  }

  /**
   * @return the responseHash
   */
  public Integer getResponseHash()
  {
    return responseHash;
  }

  /**
   * @param responseHash the responseHash to set
   */
  public void setResponseHash(Integer responseHash)
  {
    this.responseHash = responseHash;
  }

  public Integer getHttpReturnCode()
  {
    return httpReturnCode;
  }

  public void setHttpReturnCode(Integer httpReturnCode)
  {
    this.httpReturnCode = httpReturnCode;
  }

  public List<MockDataPathParam> getPathParams()
  {
    return pathParams;
  }

  public void setPathParams(List<MockDataPathParam> pathParams)
  {
    this.pathParams = pathParams;
  }

  public List<MockDataUrlArgument> getUrlArguments()
  {
    return urlArguments;
  }

  public void setUrlArguments(List<MockDataUrlArgument> urlArguments)
  {
    this.urlArguments = urlArguments;
  }

  public Long getDelay()
  {
    return delay;
  }

  public void setDelay(Long delay)
  {
    this.delay = delay;
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
