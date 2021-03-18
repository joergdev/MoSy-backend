package de.joergdev.mosy.backend.api.intern.response.mockservices;

import de.joergdev.mosy.api.response.AbstractResponse;

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