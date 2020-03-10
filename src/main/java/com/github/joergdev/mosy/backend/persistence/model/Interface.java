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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "INTERFACE")
public class Interface
{
  public static final int LENGTH_NAME = 200;
  public static final int LENGTH_SERVICE_PATH = 200;
  public static final int LENGTH_ROUTING_URL = 500;

  @Column(name = "INTERFACE_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer interfaceId;

  @Column(name = "NAME", length = LENGTH_NAME, unique = true, nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_TYPE_ID", nullable = false)
  private InterfaceType type;

  @Column(name = "SERVICE_PATH", length = LENGTH_SERVICE_PATH)
  private String servicePath;

  @Column(name = "MOCK_DISABLED_ON_STARTUP", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean mockDisabledOnStartup;

  @Column(name = "MOCK_DISABLED", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean mockDisabled;

  @Column(name = "ROUTING_URL", length = LENGTH_ROUTING_URL)
  private String routingUrl;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, nullable = false, columnDefinition = "INT(1)")
  private Boolean routingOnNoMockData;

  @OneToMany(mappedBy = "mockInterface", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<InterfaceMethod> methods;

  @OneToOne(mappedBy = "mockInterface", cascade = CascadeType.REMOVE)
  private RecordConfig recordConfig;

  public Integer getInterfaceId()
  {
    return interfaceId;
  }

  public void setInterfaceId(Integer interfaceId)
  {
    this.interfaceId = interfaceId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public InterfaceType getType()
  {
    return type;
  }

  public void setType(InterfaceType type)
  {
    this.type = type;
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

  public String getRoutingUrl()
  {
    return routingUrl;
  }

  public void setRoutingUrl(String routingUrl)
  {
    this.routingUrl = routingUrl;
  }

  public Boolean getRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(Boolean routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  public List<InterfaceMethod> getMethods()
  {
    return methods;
  }

  public void setMethods(List<InterfaceMethod> methods)
  {
    this.methods = methods;
  }

  public RecordConfig getRecordConfig()
  {
    return recordConfig;
  }

  public void setRecordConfig(RecordConfig recordConfig)
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
}