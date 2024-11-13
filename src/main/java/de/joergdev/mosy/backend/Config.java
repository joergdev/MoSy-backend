package de.joergdev.mosy.backend;

import java.util.ResourceBundle;
import de.joergdev.mosy.shared.Utils;

public class Config
{
  /**
   * <pre>
   * MOSY_LOGIN_SECRET
   * 
   * Only used if multi-tenancy is NOT enbled.
   * If system property is not set, login secret of mosy_backend.properties is used.
   * </pre>
   */
  public static final String SYSTEM_PROPERTY_LOGIN_SECRET = "MOSY_LOGIN_SECRET";

  /**
   * <pre>
   * MOSY_MULTI_TENANCY_ENABLED
   * 
   * If system property is not set, value of mosy_backend.properties is used.
   * 
   * The default is false (no multi tenancy per default).
   * </pre>
   */
  public static final String SYSTEM_PROPERTY_MULTI_TENANCY_ENABLED = "MOSY_MULTI_TENANCY_ENABLED";

  public static final String DUMMY_TENANT_NAME_NON_MULTI_TENANCY = "Default non-multi-tanency";

  private static final ResourceBundle RES_MOSY_BACKEND = ResourceBundle.getBundle("mosy_backend");
  private static final int RES_MOSY_BACKEND_SECRET_HASH = RES_MOSY_BACKEND.getString("login_secret").hashCode();
  private static final String RES_MOSY_BACKEND_MULTI_TENANCY_ENABLED = RES_MOSY_BACKEND.getString("multi_tenancy_enabled");

  /**
   * Get the login secret from system-property ({@link #SYSTEM_PROPERTY_LOGIN_SECRET} or from mosy_backend.properties.
   * 
   * If multi-tenancy is used the login secret is stored per Tenant!
   * 
   * @return int - secret hash
   */
  public static int getLoginSecret()
  {
    String sysProp = Utils.getSystemProperty(SYSTEM_PROPERTY_LOGIN_SECRET);

    if (!Utils.isEmpty(sysProp))
    {
      return sysProp.hashCode();
    }
    else
    {
      return RES_MOSY_BACKEND_SECRET_HASH;
    }
  }

  public static boolean isMultiTenancyEnabled()
  {
    String sysProp = Utils.getSystemProperty(SYSTEM_PROPERTY_MULTI_TENANCY_ENABLED);

    if (!Utils.isEmpty(sysProp))
    {
      return Boolean.valueOf(sysProp);
    }
    else
    {
      return !Utils.isEmpty(RES_MOSY_BACKEND_MULTI_TENANCY_ENABLED) && Boolean.valueOf(RES_MOSY_BACKEND_MULTI_TENANCY_ENABLED);
    }
  }
}
