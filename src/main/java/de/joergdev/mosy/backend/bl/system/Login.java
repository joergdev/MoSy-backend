package de.joergdev.mosy.backend.bl.system;

import de.joergdev.mosy.api.request.system.LoginRequest;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response.system.LoginResponse;
import de.joergdev.mosy.backend.Config;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
import de.joergdev.mosy.backend.persistence.dao.TenantDao;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.backend.security.TokenManagerService;

public class Login extends AbstractBL<LoginRequest, LoginResponse>
{
  private String token;

  private final TenantData tenantData = new TenantData();

  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(request.getSecretHash() == 0, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("secret"));

    leaveOn((Config.isMultiTenancyEnabled() && request.getTenantId() == null && request.getTenantName() == null)
            || (!Config.isMultiTenancyEnabled() && (request.getTenantId() != null || request.getTenantName() != null)),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("tenant"));
  }

  @Override
  protected void execute()
  {
    loadTenantDataIfIdOrNameSet();

    token = TokenManagerService.createToken(request.getSecretHash(), tenantData.tenantId, tenantData.tenantSecretHash,
        () -> TenancyUtils.getDefaultTenantIdForNonMultiTanency(this));

    leaveOn(token == null, ResponseCode.INVALID_CREDENTIALS);
  }

  private void loadTenantDataIfIdOrNameSet()
  {
    Integer tenantId = request.getTenantId();
    String tenantName = request.getTenantName();

    Tenant dbTenant = null;

    if (tenantId != null)
    {
      dbTenant = entityMgr.find(Tenant.class, tenantId);

      leaveOn(dbTenant == null, ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("tenant"));
    }
    else if (tenantName != null)
    {
      dbTenant = getDao(TenantDao.class).getByName(tenantName, null);

      leaveOn(dbTenant == null, ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("tenant"));
    }

    if (dbTenant != null)
    {
      tenantData.tenantId = dbTenant.getTenantId();
      tenantData.tenantSecretHash = dbTenant.getSecretHash();
    }
  }

  private class TenantData
  {
    Integer tenantId = null;
    Integer tenantSecretHash = null;
  }

  @Override
  protected void fillOutput()
  {
    response.setToken(token);
  }
}
