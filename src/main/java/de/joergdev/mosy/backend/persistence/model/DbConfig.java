package de.joergdev.mosy.backend.persistence.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
