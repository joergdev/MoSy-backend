package de.joergdev.mosy.backend.bl.tenant;

import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.api.request.tenant.SaveRequest;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.tenant.SaveResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.system.Boot;
import de.joergdev.mosy.backend.persistence.dao.TenantDao;
import de.joergdev.mosy.backend.security.TokenManagerService;
import de.joergdev.mosy.shared.Utils;

public class Save extends AbstractBL<SaveRequest, SaveResponse>
{
  private Tenant apiTenant;
  private boolean creation;

  private de.joergdev.mosy.backend.persistence.model.Tenant dbTenant;

  @Override
  protected void beforeExecute()
  {
    if (request.getTenant() != null && request.getTenant().getTenantId() == null)
    {
      checkToken = false;
    }
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    apiTenant = request.getTenant();
    leaveOn(apiTenant == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("tenant"));

    leaveOn(Utils.isEmpty(apiTenant.getName()), ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("tenant name"));

    leaveOn(apiTenant.getTenantId() == null && request.getSecretHash() == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("tenant secret"));

    leaveOn(request.getSecretHash() != null && request.getSecretHash() == 0, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("tenant secret"));
  }

  @Override
  protected void execute()
  {
    loadOrCreateDbTenant();

    // check name unique
    checkUniqueData();

    // transfer values
    dbTenant.setName(apiTenant.getName());

    if (request.getSecretHash() != null)
    {
      dbTenant.setSecretHash(request.getSecretHash());
    }

    // save
    entityMgr.persist(dbTenant);
    entityMgr.flush();

    apiTenant.setTenantId(dbTenant.getTenantId());

    // If tenant is new, call boot (create GlobalConfig entry etc.)
    bootOnCreation();
  }

  private void bootOnCreation()
  {
    if (creation)
    {
      // get and set token, otherwise Boot has no tenant context.
      setToken(TokenManagerService.createToken(dbTenant.getSecretHash(), dbTenant.getTenantId(), dbTenant.getSecretHash(), null));

      invokeSubBL(new Boot(), null, new EmptyResponse());
    }
  }

  private void checkUniqueData()
  {
    // check unique name
    leaveOn(getDao(TenantDao.class).existsByName(apiTenant.getName(), apiTenant.getTenantId()),
        ResponseCode.DATA_ALREADY_EXISTS.withAddtitionalInfo("tenant with name: " + apiTenant.getName()));
  }

  private void loadOrCreateDbTenant()
  {
    if (apiTenant.getTenantId() != null)
    {
      dbTenant = findDbEntity(de.joergdev.mosy.backend.persistence.model.Tenant.class, apiTenant.getTenantId(), "tenant with id " + apiTenant.getTenantId());
    }
    else
    {
      dbTenant = new de.joergdev.mosy.backend.persistence.model.Tenant();

      creation = true;
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setTenant(apiTenant);
  }
}
