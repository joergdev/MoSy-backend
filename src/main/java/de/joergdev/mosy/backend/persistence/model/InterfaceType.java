package de.joergdev.mosy.backend.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "INTERFACE_TYPE")
public class InterfaceType
{
  @Column(name = "INTERFACE_TYPE_ID", length = 2)
  @Id
  private Integer interfaceTypeId;

  @Column(name = "NAME", length = 50, nullable = false, unique = true)
  private String name;

  public Integer getInterfaceTypeId()
  {
    return interfaceTypeId;
  }

  public void setInterfaceTypeId(Integer interfaceTypeId)
  {
    this.interfaceTypeId = interfaceTypeId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}