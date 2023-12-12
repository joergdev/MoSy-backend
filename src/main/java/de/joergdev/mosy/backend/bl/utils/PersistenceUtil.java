package de.joergdev.mosy.backend.bl.utils;

import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.shared.Utils;

public class PersistenceUtil
{
  public static InterfaceMethod getDbInterfaceMethodByNames(AbstractBL<?, ?> bl, String nameInterface,
                                                            String nameMethod,
                                                            boolean nonStrictSearchMethodServicePath,
                                                            HttpMethod httpMethod)
  {
    Interface dbInterface = getDbInterfaceByName(bl, nameInterface);
    if (dbInterface != null)
    {
      return getDbInterfaceMethodByName(bl, nameMethod, dbInterface);
    }

    return null;
  }

  public static Interface getDbInterfaceByName(AbstractBL<?, ?> bl, String name)
  {
    Interface dbInterface = bl.getDao(InterfaceDao.class).getByName(name);

    bl.leaveOn(dbInterface == null,
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("interface for name: " + name));

    return dbInterface;
  }

  public static Interface getDbInterfaceByServicePath(AbstractBL<?, ?> bl, String servicePath,
                                                      boolean servicePathStartsWith)
  {
    Interface dbInterface = bl.getDao(InterfaceDao.class).getByServicePath(servicePath,
        servicePathStartsWith);

    bl.leaveOn(dbInterface == null,
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("interface for servicepath: " + servicePath));

    return dbInterface;
  }

  public static InterfaceMethod getDbInterfaceMethodByName(AbstractBL<?, ?> bl, String nameMethod,
                                                           Interface dbInterface)
  {
    if (Utils.isEmpty(nameMethod))
    {
      return null;
    }

    InterfaceMethod dbMethod = bl.getDao(InterfaceMethodDAO.class)
        .getBySearchParams(dbInterface.getInterfaceId(), nameMethod, null, false, null, null);

    bl.leaveOn(dbMethod == null,
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("interface method for name: " + nameMethod));

    return dbMethod;
  }

  public static InterfaceMethod getDbInterfaceMethodByServicePath(AbstractBL<?, ?> bl,
                                                                  String servicePathMethod,
                                                                  boolean nonStrictSearchServicePath,
                                                                  HttpMethod httpMethod,
                                                                  Interface dbInterface,
                                                                  boolean leaveOnNotFound)
  {
    if (Utils.isEmpty(servicePathMethod))
    {
      return null;
    }

    InterfaceMethod dbMethod = bl.getDao(InterfaceMethodDAO.class).getByServicePath(
        dbInterface.getInterfaceId(), servicePathMethod, nonStrictSearchServicePath, httpMethod);

    bl.leaveOn(leaveOnNotFound && dbMethod == null, ResponseCode.DATA_DOESNT_EXIST
        .withAddtitionalInfo("interface method for servicepath: " + servicePathMethod));

    return dbMethod;
  }
}