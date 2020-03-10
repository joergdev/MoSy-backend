package com.github.joergdev.mosy.backend.persistence.model;

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
@Table(name = "INTERFACE_METHOD")
public class InterfaceMethod
{
  @Column(name = "INTERFACE_METHOD_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer interfaceMethodId;

  @Column(name = "NAME", length = Interface.LENGTH_NAME, nullable = false)
  private String name;

  @Column(name = "SERVICE_PATH", length = Interface.LENGTH_SERVICE_PATH)
  private String servicePath;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean routingOnNoMockData;

  @Column(name = "MOCK_DISABLED_ON_STARTUP", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean mockDisabledOnStartup;

  @Column(name = "MOCK_DISABLED", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean mockDisabled;

  @Column(name = "COUNT_CALLS", nullable = false)
  private Integer countCalls;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_ID")
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

  public Boolean getMockDisabledOnStartup()
  {
    return mockDisabledOnStartup;
  }

  public void setMockDisabledOnStartup(Boolean mockDisabledOnStartup)
  {
    this.mockDisabledOnStartup = mockDisabledOnStartup;
  }

  public Boolean getMockDisabled()
  {
    return mockDisabled;
  }

  public void setMockDisabled(Boolean mockDisabled)
  {
    this.mockDisabled = mockDisabled;
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
}