package com.github.joergdev.mosy.backend.persistence;

public class Constraint
{
  private String table;
  private String name;
  private String sql;

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getSql()
  {
    return sql;
  }

  public void setSql(String sql)
  {
    this.sql = sql;
  }

  public String getTable()
  {
    return table;
  }

  public void setTable(String table)
  {
    this.table = table;
  }

  public boolean isDeleteCascade()
  {
    return sql.toUpperCase().contains("ON DELETE CASCADE");
  }

  public void addDeleteCascade()
  {
    // REFERENCES PUBLIC.INTERFACE(INTERFACE_ID)

    int idxRef = sql.indexOf("REFERENCES");
    if (idxRef > 0)
    {
      int idxEndRef = sql.indexOf(")", idxRef);
      if (idxEndRef > 0)
      {
        StringBuilder bui = new StringBuilder(sql);
        bui.insert(idxEndRef + 1, " ON DELETE CASCADE");

        sql = bui.toString();
      }
    }
  }
}