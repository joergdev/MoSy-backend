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
import jakarta.persistence.Table;

/**
 * @since 3.0
 */
@Entity
@Table(name = "RECORD_URL_ARGUMENT")
public class RecordUrlArgument
{
  @Column(name = "RECORD_URL_ARGUMENT_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordUrlArgumentId;

  @ManyToOne
  @JoinColumn(name = "RECORD_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Record record;

  @Column(name = "KEY_NAME", nullable = false, length = 100)
  private String key;

  @Column(name = "VALUE_TEXT", nullable = false, length = 100)
  private String value;

  public Integer getRecordUrlArgumentId()
  {
    return recordUrlArgumentId;
  }

  public void setRecordUrlArgumentId(Integer recordUrlArgumentId)
  {
    this.recordUrlArgumentId = recordUrlArgumentId;
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
