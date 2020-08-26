package com.github.joergdev.mosy.backend.bl.utils;

import com.github.joergdev.mosy.backend.persistence.model.Interface;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import com.github.joergdev.mosy.backend.persistence.model.InterfaceType;
import com.github.joergdev.mosy.shared.Utils;

public class BlUtils
{
  public static Integer getInterfaceTypeId(com.github.joergdev.mosy.api.model.InterfaceMethod blMethod,
                                           InterfaceMethod dbMethod)
  {
    return Utils.nvl(getInterfaceTypeId(blMethod), () -> getInterfaceTypeId(dbMethod));
  }

  private static Integer getInterfaceTypeId(InterfaceMethod dbMethod)
  {
    if (dbMethod != null)
    {
      Interface dbInterface = dbMethod.getMockInterface();
      if (dbInterface != null)
      {
        InterfaceType dbIfcType = dbInterface.getType();
        if (dbIfcType != null)
        {
          return dbIfcType.getInterfaceTypeId();
        }
      }
    }

    return null;
  }

  private static Integer getInterfaceTypeId(com.github.joergdev.mosy.api.model.InterfaceMethod blMethod)
  {
    if (blMethod != null)
    {
      com.github.joergdev.mosy.api.model.Interface blInterface = blMethod.getMockInterface();
      if (blInterface != null)
      {
        com.github.joergdev.mosy.api.model.InterfaceType blIfcType = blInterface.getType();
        if (blIfcType != null)
        {
          return blIfcType.id;
        }
      }
    }

    return null;
  }
}