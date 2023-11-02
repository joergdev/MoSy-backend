package de.joergdev.mosy.backend.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @since 3.0
 */
@Entity
@Table(name = "RECORD_PATH_PARAM")
public class RecordPathParam
{
  @Column(name = "RECORD_PATH_PARAM_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordPathParamId;

  @ManyToOne
  @JoinColumn(name = "RECORD_ID")
  private Record record;

  @Column(name = "KEY", nullable = false, length = 100)
  private String key;

  @Column(name = "VALUE", nullable = false, length = 100)
  private String value;

  public Integer getRecordPathParamId()
  {
    return recordPathParamId;
  }

  public void setRecordPathParamId(Integer recordPathParamId)
  {
    this.recordPathParamId = recordPathParamId;
  }

  public Record getRecord()
  {
    return record;
  }

  public void setRecord(Record record)
  {
    this.record = record;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}