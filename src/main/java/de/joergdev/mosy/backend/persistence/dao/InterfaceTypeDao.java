package de.joergdev.mosy.backend.persistence.dao;

import jakarta.persistence.Query;
import de.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import de.joergdev.mosy.shared.Utils;

public class InterfaceTypeDao extends AbstractDAO
{
  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(INTERFACE_TYPE_ID) from INTERFACE_TYPE ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    return Utils.numberToInteger(getSingleResult(q));
  }
}