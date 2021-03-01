package de.joergdev.mosy.backend.api.intern.request.record;

import de.joergdev.mosy.api.model.InterfaceMethod;

public class LoadAllRequest
{
  private InterfaceMethod interfaceMethod;
  private Integer loadCount;
  private Integer lastLoadedId;
  private Integer recordSessionID;

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

  public InterfaceMethod getInterfaceMethod()
  {
    return interfaceMethod;
  }

  public void setInterfaceMethod(InterfaceMethod interfaceMethod)
  {
    this.interfaceMethod = interfaceMethod;
  }

  public Integer getRecordSessionID()
  {
    return recordSessionID;
  }

  public void setRecordSessionID(Integer recordSessionID)
  {
    this.recordSessionID = recordSessionID;
  }
}