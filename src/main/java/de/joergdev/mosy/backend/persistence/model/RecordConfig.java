package de.joergdev.mosy.backend.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "RECORD_CONFIG")
public class RecordConfig
{
  public static final int LENGTH_TITLE = 200;

  @Column(name = "RECORD_CONFIG_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordConfigId;

  @Column(name = "TITLE", length = LENGTH_TITLE)
  private String title;

  @OneToOne
  @JoinColumn(name = "INTERFACE_ID")
  private Interface mockInterface;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  private InterfaceMethod interfaceMethod;

  @Column(name = "REQUEST_DATA", length = MockData.LENGTH_REQUEST)
  private String requestData;

  @Column(name = "ENABLED", length = 1, nullable = false, columnDefinition = "INT(1)")
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
}