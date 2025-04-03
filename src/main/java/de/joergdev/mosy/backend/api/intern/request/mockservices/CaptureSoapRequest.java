package de.joergdev.mosy.backend.api.intern.request.mockservices;

import jakarta.ws.rs.core.HttpHeaders;

public class CaptureSoapRequest
{
  private String path;
  private String absolutePath;
  private String content;
  private HttpHeaders httpHeaders;
  private boolean wsdlRequest = false;

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public HttpHeaders getHttpHeaders()
  {
    return httpHeaders;
  }

  public void setHttpHeaders(HttpHeaders httpHeaders)
  {
    this.httpHeaders = httpHeaders;
  }

  public boolean isWsdlRequest()
  {
    return wsdlRequest;
  }

  public void setWsdlRequest(boolean wsdlRequest)
  {
    this.wsdlRequest = wsdlRequest;
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