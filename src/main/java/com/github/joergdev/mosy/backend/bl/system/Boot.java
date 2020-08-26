package com.github.joergdev.mosy.backend.bl.system;

import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.model.InterfaceType;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.bl.globalconfig.Save;
import com.github.joergdev.mosy.backend.persistence.Constraint;
import com.github.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceTypeDao;
import com.github.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import com.github.joergdev.mosy.backend.persistence.dao.MockSessionDao;

public class Boot extends AbstractBL<Void, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    alterConstraintsIfNecessary();

    createGlobalConfigIfNotExisting();
    createInterfaceTypesIfNotExisting();

    getDao(GlobalConfigDAO.class).setValuesOnStartup();
    getDao(InterfaceDao.class).setValuesOnStartup();
    getDao(InterfaceMethodDAO.class).setValuesOnStartup();
    getDao(MockDataDAO.class).setValuesOnStartup();

    getDao(MockSessionDao.class).clearAll();
  }

  /**
   * JPA doesnt generate "ON DELETE CASCADE" so we do this here (once)
   */
  private void alterConstraintsIfNecessary()
  {
    GlobalConfigDAO dao = getDao(GlobalConfigDAO.class);

    alterConstraintIfNecessary(dao, "MOCK_DATA", "MOCK_SESSION_ID");

    alterConstraintIfNecessary(dao, "INTERFACE_METHOD", "INTERFACE_ID");
    alterConstraintIfNecessary(dao, "RECORD_CONFIG", "INTERFACE_ID");

    alterConstraintIfNecessary(dao, "MOCK_DATA", "INTERFACE_METHOD_ID");
    alterConstraintIfNecessary(dao, "RECORD", "INTERFACE_METHOD_ID");
    alterConstraintIfNecessary(dao, "RECORD_CONFIG", "INTERFACE_METHOD_ID");
  }

  private void alterConstraintIfNecessary(GlobalConfigDAO dao, String tbl, String col)
  {
    Constraint constraint = dao.findConstraint(tbl, col);

    if (!constraint.isDeleteCascade())
    {
      constraint.addDeleteCascade();

      dao.alterConstraint(constraint);
    }
  }

  private void createInterfaceTypesIfNotExisting()
  {
    if (getDao(InterfaceTypeDao.class).getCount() == 0)
    {
      for (InterfaceType apiInterfaceType : InterfaceType.values())
      {
        com.github.joergdev.mosy.backend.persistence.model.InterfaceType dbInterfaceType = new com.github.joergdev.mosy.backend.persistence.model.InterfaceType();
        dbInterfaceType.setInterfaceTypeId(apiInterfaceType.id);
        dbInterfaceType.setName(apiInterfaceType.name());

        entityMgr.persist(dbInterfaceType);
      }

      entityMgr.flush();
    }
  }

  private void createGlobalConfigIfNotExisting()
  {
    if (getDao(GlobalConfigDAO.class).get() == null)
    {
      BaseData apiBaseData = new BaseData();
      apiBaseData.setTtlMockSession(86400);

      invokeSubBL(new Save(), apiBaseData, new EmptyResponse());
    }
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}