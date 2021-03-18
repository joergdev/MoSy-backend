package de.joergdev.mosy.backend.bl.utils;

import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.backend.persistence.model.InterfaceType;
import de.joergdev.mosy.shared.Utils;

public class BlUtils
{
  public static Integer getInterfaceTypeId(de.joergdev.mosy.api.model.InterfaceMethod blMethod,
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

  private static Integer getInterfaceTypeId(de.joergdev.mosy.api.model.InterfaceMethod blMethod)
  {
    if (blMethod != null)
    {
      de.joergdev.mosy.api.model.Interface blInterface = blMethod.getMockInterface();
      if (blInterface != null)
      {
        de.joergdev.mosy.api.model.InterfaceType blIfcType = blInterface.getType();
        if (blIfcType != null)
        {
          return blIfcType.id;
        }
      }
    }

    return null;
  }
}