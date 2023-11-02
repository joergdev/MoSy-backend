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
@Table(name = "RECORD")
public class Record
{
  @Column(name = "RECORD_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordId;

  @ManyToOne
  @JoinColumn(name = "INTERFACE_METHOD_ID")
  private InterfaceMethod interfaceMethod;

  @Column(name = "REQUEST_DATA", length = MockData.LENGTH_REQUEST)
  private String requestData;

  /** REST */
  @Column(name = "HTTP_RETURN_CODE", nullable = true, columnDefinition = "INT(4) default null")
  private Integer httpReturnCode;

  @Column(name = "RESPONSE", length = MockData.LENGTH_RESPONSE)
  private String response;

  @Column(name = "CREATED", updatable = false)
  private LocalDateTime created;

  @ManyToOne
  @JoinColumn(name = "RECORD_SESSION_ID")
  private RecordSession recordSession;

  @OneToMany(mappedBy = "record", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<RecordPathParam> pathParams;

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
}