package de.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DB_CONFIG")
public class DbConfig
{
  @Column(name = "CREATED", updatable = false)
  @Id
  private LocalDateTime created;

  @Column(name = "SCHEMA_VERSION", length = 10)
  private String schemaVersion;

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public String getSchemaVersion()
  {
    return schemaVersion;
  }

  public void setSchemaVersion(String schemaVersion)
  {
    this.schemaVersion = schemaVersion;
  }
}
