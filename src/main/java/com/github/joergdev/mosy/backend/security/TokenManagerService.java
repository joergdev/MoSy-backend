package com.github.joergdev.mosy.backend.security;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class TokenManagerService
{
  private static final ResourceBundle MOSY_BACKEND = ResourceBundle.getBundle("mosy_backend");

  private static final int SECRET_HASH = MOSY_BACKEND.getString("login_secret").hashCode();

  private static final TokenHolder TOKEN_HOLDER = new TokenHolder();

  /**
   * Creates a new token.
   * 
   * @param hash
   * @return null on invalid credentials else valid token
   */
  public static String createToken(int hash)
  {
    if (hash != SECRET_HASH)
    {
      return null;
    }

    String token = "@MOSY_" + System.currentTimeMillis() / Math.random();

    TOKEN_HOLDER.tokens.add(token);

    return token;
  }

  public static boolean validateToken(String token)
  {
    return TOKEN_HOLDER.tokens.contains(token);
  }

  public static boolean invalidateToken(String token)
  {
    return TOKEN_HOLDER.tokens.remove(token);
  }

  private static class TokenHolder
  {
    private final Set<String> tokens = new HashSet<>();
  }
}