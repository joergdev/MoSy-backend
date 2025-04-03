package de.joergdev.mosy.backend.persistence.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECORD_CONFIG")
public class RecordConfig implements TenantScoped
{
  public static final int LENGTH_TITLE = 200;

  @Column(name = "RECORD_CONFIG_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordConfigId;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

  @Column(name = "TITLE", length = LENGTH_TITLE)
  private String title;

  @OneToOne
  @JoinColumn(name = "INTERFACE_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Interface mockInterface;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private InterfaceMethod interfaceMethod;

  @Column(name = "REQUEST_DATA", length = MockData.LENGTH_REQUEST)
  private String requestData;

  @Column(name = "ENABLED", length = 1, nullable = false, columnDefinition = "INTEGER")
  private Boolean enabled;

  public Integer getRecordConfigId()
  {
    return recordConfigId;
  }

  public void setRecordConfigId(Integer recordConfigId)
  {
    this.recordConfigId = recordConfigId;
  }

  public Interface getMockInterface()
  {
    return mockInterface;
  }

  public void setMockInterface(Interface mockInterface)
  {
    this.mockInterface = mockInterface;
  }

  public InterfaceMethod getInterfaceMethod()
  {
    return interfaceMethod;
  }

  public void setInterfaceMethod(InterfaceMethod interfaceMethod)
  {
    this.interfaceMethod = interfaceMethod;
  }

  public String getRequestData()
  {
    return requestData;
  }

  public void setRequestData(String requestData)
  {
    this.requestData = requestData;
  }

  public Boolean getEnabled()
  {
    return enabled;
  }

  public void setEnabled(Boolean enabled)
  {
    this.enabled = enabled;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
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
