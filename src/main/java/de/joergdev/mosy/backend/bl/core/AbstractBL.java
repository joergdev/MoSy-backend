package de.joergdev.mosy.backend.bl.core;

import java.util.Objects;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import de.joergdev.mosy.api.response.AbstractResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.ResponseMessage;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.backend.security.TokenManagerService;

public abstract class AbstractBL<T, K extends AbstractResponse>
{
  protected final Logger log = Logger.getLogger(getClass());

  protected T request;

  protected K response;

  private String token;

  protected boolean checkToken = true;

  protected boolean isSubcall = false;

  protected BLEntityManager entityMgr;

  public void executeCore(boolean throwException)
  {
    try
    {
      beforeExecute();

      Objects.requireNonNull(response, "response");

      checkSecurity();

      validateInput();
      execute();

      fillOutput();

      response.setStateOK(true);
    }
    catch (BLException ble)
    {
      if (throwException)
      {
        throw ble;
      }
      // else -> nothing to do, event contains the error
    }
  }

  /**
   * Runs before anything else (check Response set, security, validate input, execution,...).
   * Can be overwritten for init purposes.
   */
  protected void beforeExecute()
  {

  }

  private void checkSecurity()
  {
    leaveOn(checkToken && !TokenManagerService.validateToken(token), ResponseCode.ACCESS_DENIED);
  }

  /**
   * Fuegt dem Event die uebergebene ErrorMessage als Error hinzu und
   * wirft eine Exception um den Worklfow zu beenden.
   * 
   * @param rspCode
   * @throws BLException
   */
  public void leave(ResponseCode rspCode)
  {
    leave(new ResponseMessage(rspCode));
  }

  public void leave(ResponseCode rspCode, Throwable t)
  {
    leave(rspCode, t);
  }

  public void leaveOn(boolean expression, ResponseCode rspCode)
  {
    leaveOn(expression, new ResponseMessage(rspCode));
  }

  /**
   * Fuegt dem Event die uebergebene ErrorMessage als Error hinzu und
   * wirft eine Exception um den Worklfow zu beenden.
   * 
   * @param respM
   * @throws BLException
   */
  public void leave(ResponseMessage respM)
    throws BLException
  {
    addResponseMessage(respM);

    throw new BLException(respM);
  }

  public void leave(ResponseMessage rspM, Throwable t)
  {
    log.error(rspM.getFullMessage(), t);

    leave(rspM);
  }

  public void leaveOn(boolean expression, Supplier<ResponseMessage> rspM)
  {
    if (expression)
    {
      leave(rspM.get());
    }
  }

  public void leaveOn(boolean expression, ResponseMessage rspM)
  {
    if (expression)
    {
      leave(rspM);
    }
  }

  /**
   * Adding an message to response.
   * 
   * @param rspCode
   */
  public void addResponseCode(ResponseCode rspCode)
  {
    addResponseMessage(new ResponseMessage(rspCode));
  }

  /**
   * Adding an message to response.
   * 
   * @param rspM
   */
  public void addResponseMessage(ResponseMessage rspM)
  {
    response.getMessages().add(Objects.requireNonNull(rspM, "rspM"));
  }

  protected abstract void validateInput();

  protected abstract void execute();

  protected abstract void fillOutput();

  public <L, M extends AbstractResponse> M invokeSubBL(AbstractBL<L, M> bl, L request, M response)
  {
    return invokeSubBL(bl, request, response, true);
  }

  public <L, M extends AbstractResponse> M invokeSubBL(AbstractBL<L, M> bl, L request, M response, boolean throwException)
  {
    bl.isSubcall = true;

    bl.setToken(token);
    bl.checkToken = false;

    bl.setRequest(request);
    bl.setResponse(response);
    bl.setEntityMgr(entityMgr);

    try
    {
      bl.executeCore(throwException);
    }
    finally
    {
      response.getMessages().addAll(bl.getResponse().getMessages());
    }

    return response;
  }

  public AbstractResponse getResponse()
  {
    return response;
  }

  public void setResponse(K response)
  {
    this.response = response;
  }

  public String getToken()
  {
    return token;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public T getRequest()
  {
    return request;
  }

  public void setRequest(T request)
  {
    this.request = request;
  }

  public EntityManager getEntityMgr()
  {
    return entityMgr;
  }

  public void setEntityMgr(EntityManager entityMgr)
  {
    if (entityMgr != null)
    {
      // overwrite EntityManger for check tenancy before delegate to origin EntityManger
      this.entityMgr = new BLEntityManager(entityMgr, getTenantId());
    }
    else
    {
      this.entityMgr = null;
    }
  }

  public <L extends AbstractDAO> L getDao(Class<L> daoClass)
  {
    try
    {
      L dao = daoClass.newInstance();

      dao.setEntityMgr(entityMgr);
      dao.setTenantId(getTenantId());

      return dao;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public <L> L findDbEntity(Class<L> entityClass, Object pk, String errorOnNull)
  {
    L entity = entityMgr.find(entityClass, pk);

    leaveOn(entity == null, ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo(errorOnNull));

    return entity;
  }

  protected Integer getTenantId()
  {
    return TokenManagerService.getTenantId(token);
  }
}
