package com.github.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @since 2.0
 */
@Entity
@Table(name = "RECORD_SESSION")
public class RecordSession
{
  @Column(name = "RECORD_SESSION_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordSessionID;

  @Column(name = "CREATED")
  private LocalDateTime created;

  @OneToMany(mappedBy = "recordSession", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<Record> records;

  public Integer getRecordSessionID()
  {
    return recordSessionID;
  }

  public void setRecordSessionID(Integer recordSessionID)
  {
    this.recordSessionID = recordSessionID;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  /**
   * @return the records
   */
  public List<Record> getRecords()
  {
    return records;
  }

  /**
   * @param records the records to set
   */
  public void setRecords(List<Record> records)
  {
    this.records = records;
  }
}