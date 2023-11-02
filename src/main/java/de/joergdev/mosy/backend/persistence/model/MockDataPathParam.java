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
@Table(name = "MOCK_DATA_PATH_PARAM")
public class MockDataPathParam
{
  public static final int LENGTH_KEY = 100;
  public static final int LENGTH_VALUE = 100;
  
  @Column(name = "MOCK_DATA_PATH_PARAM_ID")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer mockDataPathParamId;

  @ManyToOne
  @JoinColumn(name = "MOCK_DATA_ID")
  private MockData mockData;

  @Column(name = "KEY", nullable = false, length = LENGTH_KEY)
  private String key;

  @Column(name = "VALUE", nullable = false, length = LENGTH_VALUE)
  private String value;

  public Integer getMockDataPathParamId()
  {
    return mockDataPathParamId;
  }

  public void setMockDataPathParamId(Integer mockDataPathParamId)
  {
    this.mockDataPathParamId = mockDataPathParamId;
  }

  public MockData getMockData()
  {
    return mockData;
  }

  public void setMockData(MockData mockData)
  {
    this.mockData = mockData;
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