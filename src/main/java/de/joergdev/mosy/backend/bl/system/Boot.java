package de.joergdev.mosy.backend.bl.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.globalconfig.Save;
import de.joergdev.mosy.backend.persistence.Constraint;
import de.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import de.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.dao.InterfaceTypeDao;
import de.joergdev.mosy.backend.persistence.dao.MigrationDao;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.dao.RecordSessionDao;
import de.joergdev.mosy.backend.persistence.model.GlobalConfig;
import de.joergdev.mosy.shared.Utils;

public class Boot extends AbstractBL<Void, EmptyResponse>
{
  private static final Integer[] ACTUAL_VERSION = new Integer[] {3, 0, 0};

  private GlobalConfig dbGlobalConfig;

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

    getDao(RecordSessionDao.class).clearAll();
    getDao(MockProfileDao.class).clearAllNonPersistent();

    doDbUpdate();
  }

  private void doDbUpdate()
  {
    Integer[] schemaVersionDb = getSchemaVersionDb();

    // Execute schema updates for higher versions then actual set
    for (Entry<Integer[], Runnable> schemaUpdate : getDbUpdateImplementations().entrySet())
    {
      if (isUpdateVersionHigherThenActualVersion(schemaVersionDb, schemaUpdate.getKey()))
      {
        schemaUpdate.getValue().run();
      }
    }

    dbGlobalConfig = getDao(GlobalConfigDAO.class).get();

    // set actual version as schema version
    dbGlobalConfig.setSchemaVersion(
        Arrays.asList(ACTUAL_VERSION).stream().map(i -> String.valueOf(i)).collect(Collectors.joining(".")));

    entityMgr.persist(dbGlobalConfig);
    entityMgr.flush();
  }

  private Map<Integer[], Runnable> getDbUpdateImplementations()
  {
    Map<Integer[], Runnable> mapSchemaUpdates = new HashMap<>();

    // 3.0.0
    mapSchemaUpdates.put(new Integer[] {3, 0, 0}, new Runnable()
    {
      @Override
      public void run()
      {
        MigrationDao migDao = getDao(MigrationDao.class);

        migDao.migrateServicePathIntern();
        migDao.setMockDataResponseNullable();
        migDao.setRecordRequestNullable();
        migDao.setRecordResponseNullable();
      }
    });

    return mapSchemaUpdates;
  }

  private Integer[] getSchemaVersionDb()
  {
    String schemaVersion = dbGlobalConfig.getSchemaVersion();
    if (Utils.isEmpty(schemaVersion))
    {
      schemaVersion = "0.0.0";
    }

    Integer[] schemaVersionDb = new Integer[3];
    int pos = 0;
    for (String v : schemaVersion.split(Pattern.quote(".")))
    {
      schemaVersionDb[pos] = Integer.valueOf(v);
      pos++;
    }

    return schemaVersionDb;
  }

  private boolean isUpdateVersionHigherThenActualVersion(Integer[] schemaVersionDb, Integer[] versionUpdate)
  {
    if (schemaVersionDb[0] < versionUpdate[0])
    {
      return true;
    }
    else if (schemaVersionDb[0] == versionUpdate[0])
    {
      if (schemaVersionDb[1] < versionUpdate[1])
      {
        return true;
      }
      else if (schemaVersionDb[1] == versionUpdate[1])
      {
        if (schemaVersionDb[2] < versionUpdate[2])
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * JPA doesnt generate "ON DELETE CASCADE" so we do this here (once)
   */
  private void alterConstraintsIfNecessary()
  {
    GlobalConfigDAO dao = getDao(GlobalConfigDAO.class);

    alterConstraintIfNecessary(dao, "MOCK_DATA_MOCK_PROFILE", "MOCK_DATA_ID");
    alterConstraintIfNecessary(dao, "MOCK_DATA_MOCK_PROFILE", "MOCK_PROFILE_ID");

    alterConstraintIfNecessary(dao, "MOCK_DATA_PATH_PARAM", "MOCK_DATA_ID");

    alterConstraintIfNecessary(dao, "RECORD_PATH_PARAM", "RECORD_ID");

    alterConstraintIfNecessary(dao, "INTERFACE_METHOD", "INTERFACE_ID");
    alterConstraintIfNecessary(dao, "RECORD_CONFIG", "INTERFACE_ID");

    alterConstraintIfNecessary(dao, "MOCK_DATA", "INTERFACE_METHOD_ID");
    alterConstraintIfNecessary(dao, "RECORD", "INTERFACE_METHOD_ID");
    alterConstraintIfNecessary(dao, "RECORD_CONFIG", "INTERFACE_METHOD_ID");

    alterConstraintIfNecessary(dao, "RECORD", "RECORD_SESSION_ID");
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
        de.joergdev.mosy.backend.persistence.model.InterfaceType dbInterfaceType = new de.joergdev.mosy.backend.persistence.model.InterfaceType();
        dbInterfaceType.setInterfaceTypeId(apiInterfaceType.id);
        dbInterfaceType.setName(apiInterfaceType.name());

        entityMgr.persist(dbInterfaceType);
      }

      entityMgr.flush();
    }
  }

  private void createGlobalConfigIfNotExisting()
  {
    dbGlobalConfig = getDao(GlobalConfigDAO.class).get();

    if (dbGlobalConfig == null)
    {
      BaseData apiBaseData = new BaseData();
      apiBaseData.setTtlMockProfile(86400);
      apiBaseData.setTtlRecordSession(86400);

      invokeSubBL(new Save(), apiBaseData, new EmptyResponse());

      dbGlobalConfig = getDao(GlobalConfigDAO.class).get();
    }
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}