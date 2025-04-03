package de.joergdev.mosy.backend.api.intern.request.mockservices;

import java.util.ArrayList;
import java.util.Collection;
import jakarta.ws.rs.core.HttpHeaders;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.shared.Utils;

public class CaptureCommonRequest
{
  private String servicePathInterface;
  private String servicePathMethod;
  private HttpMethod httpMethod;
  private HttpHeaders httpHeaders;
  private String content;
  private boolean routeOnly = false;
  private String routeAddition;
  private String absolutePath;
  private final Collection<UrlArgument> urlArguments = new ArrayList<UrlArgument>();

  private String mockProfileNameCached = "______UNSET_______";

  private Integer recordSessionIdCached = -1;

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getMockProfileName()
  {
    if ("______UNSET_______".equals(mockProfileNameCached))
    {
      mockProfileNameCached = httpHeaders == null
          ? null
          : Utils.getFirstElementOfCollection(
              httpHeaders.getRequestHeader(APIConstants.HTTP_HEADER_MOCK_PROFILE_NAME));
    }

    return mockProfileNameCached;
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

  public HttpMethod getHttpMethod()
  {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod)
  {
    this.httpMethod = httpMethod;
  }

  public Collection<UrlArgument> getUrlArguments()
  {
    return urlArguments;
  }
}