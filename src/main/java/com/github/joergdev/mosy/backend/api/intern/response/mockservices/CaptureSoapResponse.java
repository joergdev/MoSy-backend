package com.github.joergdev.mosy.backend.api.intern.response.mockservices;

import com.github.joergdev.mosy.api.response.AbstractResponse;

public class CaptureSoapResponse extends AbstractResponse
{
  private String response;

  public String getResponse()
  {
    return response;
  }

  public void setResponse(String response)
  {
    this.response = response;
  }
}