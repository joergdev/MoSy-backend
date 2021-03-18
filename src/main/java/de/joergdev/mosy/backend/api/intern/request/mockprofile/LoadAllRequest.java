package de.joergdev.mosy.backend.api.intern.request.mockprofile;

public class LoadAllRequest
{
  private Integer loadCount;
  private Integer lastLoadedId;

  public Integer getLoadCount()
  {
    return loadCount;
  }

  public void setLoadCount(Integer loadCount)
  {
    this.loadCount = loadCount;
  }

  public Integer getLastLoadedId()
  {
    return lastLoadedId;
  }

  public void setLastLoadedId(Integer lastLoadedId)
  {
    this.lastLoadedId = lastLoadedId;
  }
}