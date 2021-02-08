package com.github.joergdev.mosy.backend.api.intern.request.mockservices;

import javax.ws.rs.core.HttpHeaders;
import com.github.joergdev.mosy.api.APIConstants;
import com.github.joergdev.mosy.shared.Utils;

public class CaptureCommonRequest
{
  private String servicePathInterface;
  private String servicePathMethod;
  private HttpHeaders httpHeaders;
  private String content;
  private boolean routeOnly = false;
  private String routeAddition;
  private String absolutePath;

  private Integer mockProfileIdCached = -1;

  private Integer recordSessionIdCached = -1;

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public Integer getMockProfileID()
  {
    if (mockProfileIdCached != null && mockProfileIdCached == -1)
    {
      mockProfileIdCached = httpHeaders == null
          ? null
          : Utils.asInteger(Utils.getFirstElementOfCollection(
              httpHeaders.getRequestHeader(APIConstants.HTTP_HEADER_MOCK_PROFILE_ID)));
    }

    return mockProfileIdCached;
  }

  public Integer getRecordSessionID()
  {
    if (recordSessionIdCached != null && recordSessionIdCached == -1)
    {
      recordSessionIdCached = httpHeaders == null
          ? null
          : Utils.asInteger(Utils.getFirstElementOfCollection(
              httpHeaders.getRequestHeader(APIConstants.HTTP_HEADER_RECORD_SESSION_ID)));
    }

    return recordSessionIdCached;
  }

  public HttpHeaders getHttpHeaders()
  {
    return httpHeaders;
  }

  public void setHttpHeaders(HttpHeaders httpHeaders)
  {
    this.httpHeaders = httpHeaders;
  }

  public String getServicePathMethod()
  {
    return servicePathMethod;
  }

  public void setServicePathMethod(String servicePathMethod)
  {
    this.servicePathMethod = servicePathMethod;
  }

  public String getServicePathInterface()
  {
    return servicePathInterface;
  }

  public void setServicePathInterface(String servicePathInterface)
  {
    this.servicePathInterface = servicePathInterface;
  }

  public boolean isRouteOnly()
  {
    return routeOnly;
  }

  public void setRouteOnly(boolean routeOnly)
  {
    this.routeOnly = routeOnly;
  }

  public String getRouteAddition()
  {
    return routeAddition;
  }

  public void setRouteAddition(String routeAddition)
  {
    this.routeAddition = routeAddition;
  }

  public String getAbsolutePath()
  {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath)
  {
    this.absolutePath = absolutePath;
  }
}