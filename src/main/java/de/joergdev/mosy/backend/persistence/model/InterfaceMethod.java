package de.joergdev.mosy.backend.persistence.model;

import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "INTERFACE_METHOD")
public class InterfaceMethod implements TenantScoped
{
  @Column(name = "INTERFACE_METHOD_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer interfaceMethodId;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  private Tenant tenant;

  @Column(name = "NAME", length = Interface.LENGTH_NAME, nullable = false)
  private String name;

  @Column(name = "SERVICE_PATH", length = Interface.LENGTH_SERVICE_PATH)
  private String servicePath;

  @Column(name = "SERVICE_PATH_INTERN", length = Interface.LENGTH_SERVICE_PATH)
  private String servicePathIntern;

  @Column(name = "HTTP_METHOD", length = 6)
  private String httpMethod;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, columnDefinition = "INTEGER", nullable = false)
  private Boolean routingOnNoMockData;

  @Column(name = "MOCK_ACTIVE_ON_STARTUP", length = 1, columnDefinition = "INTEGER", nullable = false)
  private Boolean mockActiveOnStartup;

  @Column(name = "MOCK_ACTIVE", length = 1, columnDefinition = "INTEGER", nullable = false)
  private Boolean mockActive;

  @Column(name = "COUNT_CALLS", nullable = false)
  private Integer countCalls;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Interface mockInterface;

  @OneToMany(mappedBy = "interfaceMethod", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockData> mockData;

  @OneToMany(mappedBy = "interfaceMethod", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<RecordConfig> recordConfig;

  @OneToMany(mappedBy = "interfaceMethod", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Record> records;

  public Integer getInterfaceMethodId()
  {
    return interfaceMethodId;
  }

  public void setInterfaceMethodId(Integer interfaceMethodId)
  {
    this.interfaceMethodId = interfaceMethodId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Boolean getRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(Boolean routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  public Integer getCountCalls()
  {
    return countCalls;
  }

  public void setCountCalls(Integer countCalls)
  {
    this.countCalls = countCalls;
  }

  public Interface getMockInterface()
  {
    return mockInterface;
  }

  public void setMockInterface(Interface mockInterface)
  {
    this.mockInterface = mockInterface;
  }

  public List<MockData> getMockData()
  {
    return mockData;
  }

  public void setMockData(List<MockData> mockData)
  {
    this.mockData = mockData;
  }

  public List<RecordConfig> getRecordConfig()
  {
    return recordConfig;
  }

  public void setRecordConfig(List<RecordConfig> recordConfig)
  {
    this.recordConfig = recordConfig;
  }

  public String getServicePath()
  {
    return servicePath;
  }

  public void setServicePath(String servicePath)
  {
    this.servicePath = servicePath;
  }

  public List<Record> getRecords()
  {
    return records;
  }

  public void setRecords(List<Record> records)
  {
    this.records = records;
  }

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

  public String getHttpMethod()
  {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod)
  {
    this.httpMethod = httpMethod;
  }

  public String getServicePathIntern()
  {
    return servicePathIntern;
  }

  public void setServicePathIntern(String servicePathIntern)
  {
    this.servicePathIntern = servicePathIntern;
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
