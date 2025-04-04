package de.joergdev.mosy.backend.api.intern.response.mockservices;

import jakarta.ws.rs.core.MultivaluedMap;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.response.AbstractResponse;

public class CaptureCommonResponse extends AbstractResponse
{
  private String response;
  private Integer responseHttpCode;
  private MultivaluedMap<String, Object> responseHeaders;
  private Boolean record;
  private Boolean route;
  private InterfaceMethod interfaceMethod;

  public String getResponse()
  {
    return response;
  }

  public void setResponse(String response)
  {
    this.response = response;
  }

  public Boolean getRecord()
  {
    return record;
  }

  public void setRecord(Boolean record)
  {
    this.record = record;
  }

  public Boolean getRoute()
  {
    return route;
  }

  public void setRoute(Boolean route)
  {
    this.route = route;
  }

  public InterfaceMethod getInterfaceMethod()
  {
    return interfaceMethod;
  }

  public void setInterfaceMethod(InterfaceMethod interfaceMethod)
  {
    this.interfaceMethod = interfaceMethod;
  }

  public Integer getResponseHttpCode()
  {
    return responseHttpCode;
  }

  public void setResponseHttpCode(Integer responseHttpCode)
  {
    this.responseHttpCode = responseHttpCode;
  }

  public MultivaluedMap<String, Object> getResponseHeaders()
  {
    return responseHeaders;
  }

  public void setResponseHeaders(MultivaluedMap<String, Object> responseHeaders)
  {
    this.responseHeaders = responseHeaders;
  }
}