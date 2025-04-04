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
@Table(name = "MOCK_PROFILE")
public class MockProfile implements TenantScoped
{
  public static final int LENGTH_NAME = 200;
  public static final int LENGTH_DESCRIPTION = 2000;

  @Column(name = "MOCK_PROFILE_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockProfileID;

  @ManyToOne
  @JoinColumn(name = "TENANT_ID", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Tenant tenant;

  @Column(name = "CREATED")
  private LocalDateTime created;

  @Column(name = "NAME", length = LENGTH_NAME, nullable = true)
  private String name;

  @Column(name = "PERSISTENT", length = 1, nullable = false, columnDefinition = "INTEGER")
  private Boolean persistent;

  @Column(name = "USE_COMMON_MOCKS", length = 1, nullable = false, columnDefinition = "INTEGER")
  private Boolean useCommonMocks;

  @Column(name = "DESCRIPTION", length = LENGTH_DESCRIPTION)
  private String description;

  @OneToMany(mappedBy = "mockProfile", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<MockDataMockProfile> mockData;

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  /**
   * @return the mockProfileID
   */
  public Integer getMockProfileID()
  {
    return mockProfileID;
  }

  /**
   * @param mockProfileID the mockProfileID to set
   */
  public void setMockProfileID(Integer mockProfileID)
  {
    this.mockProfileID = mockProfileID;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * @return the persistent
   */
  public Boolean getPersistent()
  {
    return persistent;
  }

  /**
   * @param persistent the persistent to set
   */
  public void setPersistent(Boolean persistent)
  {
    this.persistent = persistent;
  }

  /**
   * @return the useCommonMocks
   */
  public Boolean getUseCommonMocks()
  {
    return useCommonMocks;
  }

  /**
   * @param useCommonMocks the useCommonMocks to set
   */
  public void setUseCommonMocks(Boolean useCommonMocks)
  {
    this.useCommonMocks = useCommonMocks;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return the mockData
   */
  public List<MockDataMockProfile> getMockData()
  {
    return mockData;
  }

  /**
   * @param mockData the mockData to set
   */
  public void setMockData(List<MockDataMockProfile> mockData)
  {
    this.mockData = mockData;
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
