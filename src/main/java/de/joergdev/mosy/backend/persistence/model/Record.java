package de.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
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
@Table(name = "RECORD")
public class Record implements TenantScoped
{
  @Column(name = "RECORD_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordId;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private InterfaceMethod interfaceMethod;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

  @Column(name = "REQUEST_DATA", length = MockData.LENGTH_REQUEST)
  private String requestData;

  /** REST */
  @Column(name = "HTTP_RETURN_CODE", nullable = true, length = 4)
  private Integer httpReturnCode;

  @Column(name = "RESPONSE", length = MockData.LENGTH_RESPONSE)
  private String response;

  @Column(name = "CREATED", updatable = false)
  private LocalDateTime created;

  @ManyToOne
  @JoinColumn(name = "RECORD_SESSION_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private RecordSession recordSession;

  @OneToMany(mappedBy = "record", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<RecordPathParam> pathParams;

  @OneToMany(mappedBy = "record", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<RecordUrlArgument> urlArguments;

  public Integer getRecordId()
  {
    return recordId;
  }

  public void setRecordId(Integer recordId)
  {
    this.recordId = recordId;
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

  public String getResponse()
  {
    return response;
  }

  public void setResponse(String response)
  {
    this.response = response;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public RecordSession getRecordSession()
  {
    return recordSession;
  }

  public void setRecordSession(RecordSession recordSession)
  {
    this.recordSession = recordSession;
  }

  public Integer getHttpReturnCode()
  {
    return httpReturnCode;
  }

  public void setHttpReturnCode(Integer httpReturnCode)
  {
    this.httpReturnCode = httpReturnCode;
  }

  public List<RecordPathParam> getPathParams()
  {
    return pathParams;
  }

  public void setPathParams(List<RecordPathParam> pathParams)
  {
    this.pathParams = pathParams;
  }

  public List<RecordUrlArgument> getUrlArguments()
  {
    return urlArguments;
  }

  public void setUrlArguments(List<RecordUrlArgument> urlArguments)
  {
    this.urlArguments = urlArguments;
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
