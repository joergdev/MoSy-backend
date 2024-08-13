package de.joergdev.mosy.backend.bl.tenant;

import java.util.ArrayList;
import java.util.List;
import de.joergdev.mosy.api.model.Tenant;
import de.joergdev.mosy.api.response.tenant.LoadAllResponse;
import de.joergdev.mosy.backend.Config;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.TenantDao;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

public class LoadAll extends AbstractBL<Void, LoadAllResponse>
{
  private final List<Tenant> apiTenants = new ArrayList<>();

  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    if (!Config.isMultiTenancyEnabled())
    {
      return;
    }

    List<de.joergdev.mosy.backend.persistence.model.Tenant> dbTenants = getDao(TenantDao.class).getAll();

    for (de.joergdev.mosy.backend.persistence.model.Tenant dbTenant : Utils.nvlCollection(dbTenants))
    {
      Tenant apiTenant = new Tenant();

      ObjectUtils.copyValues(dbTenant, apiTenant);

      apiTenants.add(apiTenant);
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setMultiTanencyEnabled(Config.isMultiTenancyEnabled());
    response.getTenants().addAll(apiTenants);
  }
}
