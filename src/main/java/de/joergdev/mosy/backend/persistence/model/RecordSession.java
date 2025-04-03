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

/**
 * @since 2.0
 */
@Entity
@Table(name = "RECORD_SESSION")
public class RecordSession implements TenantScoped
{
  @Column(name = "RECORD_SESSION_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer recordSessionID;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

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
