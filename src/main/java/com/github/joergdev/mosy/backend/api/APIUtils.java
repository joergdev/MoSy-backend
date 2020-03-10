package com.github.joergdev.mosy.backend.api;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;
import com.github.joergdev.mosy.api.response.AbstractResponse;
import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.api.response.ResponseMessage;
import com.github.joergdev.mosy.api.response.ResponseMessageLevel;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.bl.core.BLException;
import com.github.joergdev.mosy.backend.persistence.EntityManagerProviderService;

public class APIUtils
{
  private static final Logger LOG = Logger.getLogger(APIUtils.class);

  public static <T extends AbstractResponse, R> Response executeBL(R request, T response, AbstractBL<R, T> bl)
  {
    return executeBL(request, response, bl, null, Status.OK);
  }

  public static <T extends AbstractResponse, R> Response executeBL(R request, T response, AbstractBL<R, T> bl,
                                                                   Status statusOnSuccess)
  {
    return executeBL(request, response, bl, null, statusOnSuccess);
  }

  public static <T extends AbstractResponse, R> Response executeBL(R request, T response, AbstractBL<R, T> bl,
                                                                   String token)
  {
    return executeBL(request, response, bl, token, Status.OK);
  }

  public static <T extends AbstractResponse, R> Response executeBL(R blRequest, T blResponse,
                                                                   AbstractBL<R, T> bl, String token,
                                                                   Status statusOnSuccess)
  {
    // jaxrs-response
    ResponseBuilder responseBui = null;

    EntityManager em = null;
    EntityTransaction tx = null;

    try
    {
      // handle EntityManager
      em = EntityManagerProviderService.getInstance().getEntityManager();

      tx = em.getTransaction();

      if (!EntityManagerProviderService.getInstance().isContainerManaged())
      {
        tx.begin();
      }

      // configure BusinessLogic and  execute
      bl.setToken(token);
      bl.setRequest(blRequest);
      bl.setResponse(blResponse);
      bl.setEntityMgr(em);

      bl.executeCore(true);

      // commit
      if (!EntityManagerProviderService.getInstance().isContainerManaged())
      {
        tx.commit();
      }

      // Response => OK
      responseBui = Response.status(statusOnSuccess);
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);

      if (bl.getResponse() == null || !bl.getResponse().getMessages().stream()
          .anyMatch(msg -> ResponseMessageLevel.ERROR.equals(msg.getResponseCode().level)))
      {
        bl.addResponseMessage(ResponseCode.UNEXPECTED_ERROR.withAddtitionalInfo(ex.getMessage()));
      }

      if (tx != null)
      {
        tx.rollback();
      }

      blResponse.setStateOK(false);

      responseBui = createResponseBuilderOnError(ex);
    }
    finally
    {
      if (em != null)
      {
        EntityManagerProviderService.getInstance().releaseEntityManager(em);
      }
    }

    return responseBui.entity(blResponse).build();
  }

  private static ResponseBuilder createResponseBuilderOnError(Exception ex)
  {
    // default
    Status status = Status.INTERNAL_SERVER_ERROR;

    if (ex instanceof BLException)
    {
      ResponseMessage rspm = ((BLException) ex).getResponseMessage();
      if (rspm != null && rspm.getResponseCode() != null)
      {
        ResponseCode rsCode = rspm.getResponseCode();

        if (ResponseCode.ACCESS_DENIED.equals(rsCode))
        {
          status = Status.UNAUTHORIZED;
        }
        else if (ResponseCode.INVALID_INPUT_PARAMS.equals(rsCode))
        {
          status = Status.BAD_REQUEST;
        }
        else if (ResponseCode.DATA_DOESNT_EXIST.equals(rsCode))
        {
          status = Status.NOT_FOUND;
        }
      }
    }

    return Response.status(status);
  }
}