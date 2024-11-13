package de.joergdev.mosy.backend.security;

import java.util.Objects;

public class Token
{
  private String tokenId;
  private Integer tenantId;

  public static Token of(String tokenId)
  {
    return of(tokenId, null);
  }

  public static Token of(String tokenId, Integer tenantId)
  {
    Token token = new Token();
    token.setTokenId(tokenId);
    token.setTenantId(tenantId);

    return token;
  }

  public String getTokenId()
  {
    return tokenId;
  }

  public void setTokenId(String tokenId)
  {
    this.tokenId = tokenId;
  }

  public Integer getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(Integer tenantId)
  {
    this.tenantId = tenantId;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(tokenId);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj instanceof Token == false)
    {
      return false;
    }

    Token other = (Token) obj;

    return Objects.equals(tokenId, other.tokenId) && (tenantId == null || other.tenantId == null || Objects.equals(tenantId, other.tenantId));
  }
}
