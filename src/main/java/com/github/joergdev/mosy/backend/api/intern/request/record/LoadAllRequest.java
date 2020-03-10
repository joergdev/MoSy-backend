package com.github.joergdev.mosy.backend.api.intern.request.record;

import com.github.joergdev.mosy.api.model.InterfaceMethod;

public class LoadAllRequest
{
  private InterfaceMethod interfaceMethod;
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

  public InterfaceMethod getInterfaceMethod()
  {
    return interfaceMethod;
  }

  public void setInterfaceMethod(InterfaceMethod interfaceMethod)
  {
    this.interfaceMethod = interfaceMethod;
  }
}