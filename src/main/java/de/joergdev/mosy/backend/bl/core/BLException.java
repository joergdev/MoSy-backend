package de.joergdev.mosy.backend.bl.core;

import de.joergdev.mosy.api.response.ResponseMessage;

public class BLException extends RuntimeException
{
  private ResponseMessage responseMessage;

  public BLException(ResponseMessage msg)
  {
    super(msg.getFullMessage());

    this.responseMessage = msg;
  }

  public BLException(String error, Throwable cause)
  {
    super(error, cause);
  }

  public String getCauseMessage()
  {
    Throwable cause = this;
    while (cause.getCause() != null)
    {
      cause = cause.getCause();
    }

    return cause.getMessage();
  }

  public ResponseMessage getResponseMessage()
  {
    return responseMessage;
  }
}