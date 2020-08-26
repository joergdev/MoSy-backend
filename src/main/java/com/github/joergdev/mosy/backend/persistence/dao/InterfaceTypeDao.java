package com.github.joergdev.mosy.backend.persistence.dao;

import javax.persistence.Query;
import com.github.joergdev.mosy.backend.persistence.dao.core.AbstractDAO;
import com.github.joergdev.mosy.shared.Utils;

public class InterfaceTypeDao extends AbstractDAO
{
  public int getCount()
  {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(INTERFACE_TYPE_ID) from INTERFACE_TYPE ");

    Query q = entityMgr.createNativeQuery(sql.toString());

    return Utils.bigInteger2Integer(getSingleResult(q));
  }
}