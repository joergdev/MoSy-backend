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

  @Column(name = "REQUEST_DATA", length = MockData.LENGTH_REQUEST, nullable = false)
  private String requestData;

  @Column(name = "RESPONSE", length = MockData.LENGTH_RESPONSE, nullable = false)
  private String response;

  @Column(name = "CREATED", updatable = false)
  private LocalDateTime created;

  @ManyToOne
  @JoinColumn(name = "RECORD_SESSION_ID")
  private RecordSession recordSession;

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
}