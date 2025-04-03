package de.joergdev.mosy.backend.bl.system;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.backend.Config;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.bl.globalconfig.Save;
import de.joergdev.mosy.backend.bl.utils.TenancyUtils;
import de.joergdev.mosy.backend.persistence.dao.DbConfigDAO;
import de.joergdev.mosy.backend.persistence.dao.GlobalConfigDAO;
import de.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.dao.InterfaceTypeDao;
import de.joergdev.mosy.backend.persistence.dao.MigrationDao;
import de.joergdev.mosy.backend.persistence.dao.MockDataDAO;
import de.joergdev.mosy.backend.persistence.dao.MockProfileDao;
import de.joergdev.mosy.backend.persistence.dao.RecordSessionDao;
import de.joergdev.mosy.backend.persistence.dao.TenantDao;
import de.joergdev.mosy.backend.persistence.model.DbConfig;
import de.joergdev.mosy.backend.persistence.model.GlobalConfig;
import de.joergdev.mosy.backend.persistence.model.Tenant;
import de.joergdev.mosy.shared.Utils;

public class Boot extends AbstractBL<Void, EmptyResponse>
{
  private static final Integer[] ACTUAL_VERSION = new Integer[] {4, 0, 0};

  private DbConfig dbConfig;

  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    createDbConfigIfNotExisting();
    createDefaultTenantForNonMultiTanencyIfNotExisting();
    createInterfaceTypesIfNotExisting();

    createGlobalConfigIfNotExisting();

    getDao(GlobalConfigDAO.class).setValuesOnStartup();
    getDao(InterfaceDao.class).setValuesOnStartup();
    getDao(InterfaceMethodDAO.class).setValuesOnStartup();
    getDao(MockDataDAO.class).setValuesOnStartup();

    getDao(RecordSessionDao.class).clearAll();
    getDao(MockProfileDao.class).clearAllNonPersistent();

    doDbUpdate();
  }

  private void createDefaultTenantForNonMultiTanencyIfNotExisting()
  {
    if (!getDao(TenantDao.class).existsByName(Config.DUMMY_TENANT_NAME_NON_MULTI_TENANCY, null))
    {
      Tenant dbTenant = new Tenant();
      dbTenant.setName(Config.DUMMY_TENANT_NAME_NON_MULTI_TENANCY);
      dbTenant.setSecretHash(123);

      entityMgr.persist(dbTenant);
      entityMgr.flush();

      createGlobalConfigIfNotExisting(dbTenant.getTenantId(), true);
    }
  }

  private void doDbUpdate()
  {
    if (getTenantId() != null)
    {
      return;
    }

    Integer[] schemaVersionDb = getSchemaVersionDb();

    String actualDbSchemaVersion = getDbSchemaVersionAsString(ACTUAL_VERSION);

    // Schema is already up to date -> no update needed
    if (actualDbSchemaVersion.equals(getDbSchemaVersionAsString(schemaVersionDb)))
    {
      return;
    }

    // Execute schema updates for higher versions then actual set
    for (Entry<Integer[], Runnable> schemaUpdate : getDbUpdateImplementations().entrySet())
    {
      if (isUpdateVersionHigherThenActualVersion(schemaVersionDb, schemaUpdate.getKey()))
      {
        schemaUpdate.getValue().run();
      }
    }

    dbConfig.setSchemaVersion(actualDbSchemaVersion);

    entityMgr.persist(dbConfig);
    entityMgr.flush();
  }

  private String getDbSchemaVersionAsString(Integer[] versionArr)
  {
    return Arrays.asList(versionArr).stream().map(i -> String.valueOf(i)).collect(Collectors.joining("."));
  }

  private Map<Integer[], Runnable> getDbUpdateImplementations()
  {
    Map<Integer[], Runnable> mapSchemaUpdates = new HashMap<>();

    // 3.0.0
    mapSchemaUpdates.put(new Integer[] {3, 0, 0}, () -> {
      MigrationDao migDao = getDao(MigrationDao.class);

      migDao.migrateServicePathIntern();
      migDao.setMockDataResponseNullable();
      migDao.setRecordRequestNullable();
      migDao.setRecordResponseNullable();
    });

    return mapSchemaUpdates;
  }

  private Integer[] getSchemaVersionDb()
  {
    String schemaVersion = dbConfig.getSchemaVersion();
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
    else if (schemaVersionDb[0].equals(versionUpdate[0]))
    {
      if (schemaVersionDb[1] < versionUpdate[1])
      {
        return true;
      }
      else if (schemaVersionDb[1].equals(versionUpdate[1]))
      {
        if (schemaVersionDb[2] < versionUpdate[2])
        {
          return true;
        }
      }
    }

    return false;
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

  private void createDbConfigIfNotExisting()
  {
    dbConfig = getDao(DbConfigDAO.class).get();

    if (dbConfig == null)
    {
      dbConfig = new DbConfig();
      dbConfig.setCreated(LocalDateTime.now());
      dbConfig.setSchemaVersion(getDbSchemaVersionAsString(ACTUAL_VERSION));

      entityMgr.persist(dbConfig);
      entityMgr.flush();

      dbConfig = getDao(DbConfigDAO.class).get();
    }
  }

  private void createGlobalConfigIfNotExisting()
  {
    createGlobalConfigIfNotExisting(getTenantId(), false);
  }

  private void createGlobalConfigIfNotExisting(Integer tenantId, boolean ctxCreateDefaultTenantForNonMultiTanency)
  {
    if (tenantId == null)
    {
      return;
    }

    GlobalConfigDAO dao = getDao(GlobalConfigDAO.class);

    // We have to set the tenantId for the use-case creation for default tenant (non-multi-tanency).
    // In this case the tenantId is not set global.
    if (ctxCreateDefaultTenantForNonMultiTanency)
    {
      dao.setTenantId(tenantId);
    }

    GlobalConfig dbGlobalConfig = dao.get();

    if (dbGlobalConfig == null)
    {
      BaseData apiBaseData = new BaseData();
      apiBaseData.setTtlMockProfile(86400);
      apiBaseData.setTtlRecordSession(86400);

      // See above, here we have to set an token for the default tenant.
      // afterwards set the token back to null
      boolean resetToken = false;
      if (ctxCreateDefaultTenantForNonMultiTanency && getToken() == null)
      {
        TenancyUtils.setInternTokenForTenancy(this, tenantId);
        resetToken = true;
      }

      invokeSubBL(new Save(), apiBaseData, new EmptyResponse());

      if (resetToken)
      {
        setToken(null);
      }
    }
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}
