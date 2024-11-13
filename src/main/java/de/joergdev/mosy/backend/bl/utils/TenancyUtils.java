package de.joergdev.mosy.backend.bl.utils;

import javax.ws.rs.core.MultivaluedMap;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.response.AbstractResponse;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.Config;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.TenantDao;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.backend.persistence.model.TenantScoped;
import de.joergdev.mosy.backend.security.TokenManagerService;
import de.joergdev.mosy.shared.Utils;

public class TenancyUtils
{
  public static Integer getDefaultTenantIdForNonMultiTanency(AbstractBL<?, ? extends AbstractResponse> bl)
  {
    return bl.getDao(TenantDao.class).getByName(Config.DUMMY_TENANT_NAME_NON_MULTI_TENANCY, null).getTenantId();
  }

  public static void setInternTokenForTenancy(AbstractBL<?, ? extends AbstractResponse> bl, MultivaluedMap<String, String> requestHeader)
  {
    Integer tenantId = null;
    if (Config.isMultiTenancyEnabled())
    {
      String tenantIdHeader = requestHeader.getFirst(APIConstants.HTTP_HEADER_TENANT_ID);

      bl.leaveOn(!Utils.isNumeric(tenantIdHeader), ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request header - tenantId"));

      tenantId = Integer.valueOf(tenantIdHeader);
    }

    setInternTokenForTenancy(bl, tenantId);
  }

  public static void setInternTokenForTenancy(AbstractBL<?, ? extends AbstractResponse> bl, Integer tenantId)
  {
    if (bl.getToken() != null)
    {
      return;
    }

    String token = TokenManagerService.createTokenWithoutSecretCheck(tenantId, () -> TenancyUtils.getDefaultTenantIdForNonMultiTanency(bl));

    bl.setToken(token);
  }

  public static <L> L checkTenantAccessForDbEntity(L entity, Integer tenantId)
  {
    if (tenantId != null)
    {
      Tenant entityTenant = null;
      boolean checkTenant = false;

      if (entity instanceof TenantScoped)
      {
        entityTenant = ((TenantScoped) entity).getTenant();

        checkTenant = entityTenant != null;
      }
      else if (entity instanceof Tenant)
      {
        entityTenant = (Tenant) entity;

        // if tenantId is null no check possible (tenant creation)
        checkTenant = entityTenant.getTenantId() != null;
      }

      if (checkTenant && !tenantId.equals(entityTenant.getTenantId()))
      {
        throw new IllegalStateException("Access denied - tenantId: " + tenantId + " - entityTenandId: " + entityTenant.getTenantId());
      }
    }

    return entity;
  }
}
