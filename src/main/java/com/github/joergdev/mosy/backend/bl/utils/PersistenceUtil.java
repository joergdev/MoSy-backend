package com.github.joergdev.mosy.backend.bl.utils;

import com.github.joergdev.mosy.api.response.ResponseCode;
import com.github.joergdev.mosy.backend.bl.core.AbstractBL;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceDao;
import com.github.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import com.github.joergdev.mosy.backend.persistence.model.Interface;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.shared.Utils;

public class PersistenceUtil
{
  public static InterfaceMethod getDbInterfaceMethodByServicePaths(AbstractBL<?, ?> bl,
                                                                   String servicePathInterface,
                                                                   String servicePathMethod)
  {
    Interface dbInterface = getDbInterfaceByServicePath(bl, servicePathInterface);
    if (dbInterface != null)
    {
      return getDbInterfaceMethodByServicePath(bl, servicePathMethod, dbInterface);
    }

    return null;
  }

  public static Interface getDbInterfaceByServicePath(AbstractBL<?, ?> bl, String servicePath)
  {
    Interface dbInterface = bl.getDao(InterfaceDao.class).getByServicePath(servicePath);

    bl.leaveOn(dbInterface == null,
        ResponseCode.DATA_DOESNT_EXIST.withAddtitionalInfo("interface for servicepath: " + servicePath));

    return dbInterface;
  }

  public static InterfaceMethod getDbInterfaceMethodByServicePath(AbstractBL<?, ?> bl,
                                                                  String servicePathMethod,
                                                                  Interface dbInterface)
  {
    if (Utils.isEmpty(servicePathMethod))
    {
      return null;
    }

    InterfaceMethod dbMethod = bl.getDao(InterfaceMethodDAO.class)
        .getByServicePath(dbInterface.getInterfaceId(), servicePathMethod);

    bl.leaveOn(dbMethod == null, ResponseCode.DATA_DOESNT_EXIST
        .withAddtitionalInfo("interface method for servicepath: " + servicePathMethod));

    return dbMethod;
  }
}