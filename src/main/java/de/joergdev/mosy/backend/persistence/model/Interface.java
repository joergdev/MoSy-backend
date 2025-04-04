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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "INTERFACE")
public class Interface implements TenantScoped
{
  public static final int LENGTH_NAME = 200;
  public static final int LENGTH_SERVICE_PATH = 200;
  public static final int LENGTH_ROUTING_URL = 500;

  @Column(name = "INTERFACE_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer interfaceId;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

  @Column(name = "NAME", length = LENGTH_NAME, nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_TYPE_ID", nullable = false)
  private InterfaceType type;

  @Column(name = "SERVICE_PATH", length = LENGTH_SERVICE_PATH)
  private String servicePath;

  @Column(name = "MOCK_ACTIVE_ON_STARTUP", length = 1, columnDefinition = "INTEGER")
  private Boolean mockActiveOnStartup;

  @Column(name = "MOCK_ACTIVE", length = 1, columnDefinition = "INTEGER")
  private Boolean mockActive;

  @Column(name = "ROUTING_URL", length = LENGTH_ROUTING_URL)
  private String routingUrl;

  @Column(name = "ROUTING_ON_NO_MOCKDATA", length = 1, columnDefinition = "INTEGER")
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
