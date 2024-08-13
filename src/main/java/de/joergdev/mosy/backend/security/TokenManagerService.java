package de.joergdev.mosy.backend.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;
import de.joergdev.mosy.backend.Config;

public class TokenManagerService
{
  private static final TokenHolder TOKEN_HOLDER = new TokenHolder();

  /**
   * Creates a new token.
   * 
   * @param hash
   * @param tenantID - may be null if multi-tanency is not enabled
   * @return null on invalid credentials else valid token
   */
  public static String createToken(int hash, Integer tenantId, Integer hashForTenant, IntSupplier defaultTenantIdForNonMultiTanencySupplier)
  {
    if (!checkSecret(hash, hashForTenant))
    {
      return null;
    }

    return createToken(tenantId, defaultTenantIdForNonMultiTanencySupplier);
  }

  /**
   * <pre>
   * Creates a new token WITHOUT check of secret key.
   * 
   * !!ATTENTION!!
   * This may only be used for internal use cases.
   * This token should never be delivered to a client!
   * </pre>
   * 
   * @param tenantId
   * @param defaultTenantIdForNonMultiTanencySupplier
   * @return null on invalid credentials else valid token
   */
  public static String createTokenWithoutSecretCheck(Integer tenantId, IntSupplier defaultTenantIdForNonMultiTanencySupplier)
  {
    return createToken(tenantId, defaultTenantIdForNonMultiTanencySupplier);
  }

  private static String createToken(Integer tenantId, IntSupplier defaultTenantIdForNonMultiTanencySupplier)
  {
    Token token = Token.of("@MOSY_" + System.currentTimeMillis() / Math.random(), getTenantIdForToken(tenantId, defaultTenantIdForNonMultiTanencySupplier));

    TOKEN_HOLDER.tokens.put(token, token);

    return token.getTokenId();
  }

  private static Integer getTenantIdForToken(Integer tenantId, IntSupplier defaultTenantIdForNonMultiTanencySupplier)
  {
    return Config.isMultiTenancyEnabled() ? tenantId : defaultTenantIdForNonMultiTanencySupplier.getAsInt();
  }

  private static boolean checkSecret(int hash, Integer hashForTenant)
  {
    if (Config.isMultiTenancyEnabled())
    {
      return hashForTenant != null && hash == hashForTenant;
    }
    else
    {
      return hash == Config.getLoginSecret();
    }
  }

  public static boolean validateToken(String token)
  {
    return TOKEN_HOLDER.tokens.keySet().contains(Token.of(token));
  }

  public static boolean invalidateToken(String token)
  {
    return TOKEN_HOLDER.tokens.remove(Token.of(token)) != null;
  }

  public static Integer getTenantId(String token)
  {
    Token tokenObj = TOKEN_HOLDER.tokens.get(Token.of(token));

    return tokenObj == null ? null : tokenObj.getTenantId();
  }

  private static class TokenHolder
  {
    private final Map<Token, Token> tokens = new HashMap<>();
  }
}
