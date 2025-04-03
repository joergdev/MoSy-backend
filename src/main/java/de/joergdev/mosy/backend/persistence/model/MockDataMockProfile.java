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
 * @since 2.0
 */
@Entity
@Table(name = "MOCK_DATA_MOCK_PROFILE")
public class MockDataMockProfile
{
  @Column(name = "MOCK_DATA_MOCK_PROFILE_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockDataMockProfileId;

  @ManyToOne
  @JoinColumn(name = "MOCK_DATA_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MockData mockData;

  @ManyToOne
  @JoinColumn(name = "MOCK_PROFILE_ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MockProfile mockProfile;

  /**
   * @return the mockDataMockProfileId
   */
  public Integer getMockDataMockProfileId()
  {
    return mockDataMockProfileId;
  }

  /**
   * @param mockDataMockProfileId the mockDataMockProfileId to set
   */
  public void setMockDataMockProfileId(Integer mockDataMockProfileId)
  {
    this.mockDataMockProfileId = mockDataMockProfileId;
  }

  /**
   * @return the mockData
   */
  public MockData getMockData()
  {
    return mockData;
  }

  /**
   * @param mockData the mockData to set
   */
  public void setMockData(MockData mockData)
  {
    this.mockData = mockData;
  }

  /**
   * @return the mockProfile
   */
  public MockProfile getMockProfile()
  {
    return mockProfile;
  }

  /**
   * @param mockProfile the mockProfile to set
   */
  public void setMockProfile(MockProfile mockProfile)
  {
    this.mockProfile = mockProfile;
  }
}
