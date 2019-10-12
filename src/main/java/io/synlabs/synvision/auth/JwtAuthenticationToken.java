package io.synlabs.synvision.auth;

import io.synlabs.synvision.entity.CurrentUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken
{

  //user object
  private final Object principal;

  //jwt token
  private final Object credentials;

  private boolean stale;

  public JwtAuthenticationToken(Object principal, Object credentials)
  {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(true);
  }

  public JwtAuthenticationToken(Object principal, Object credentials,
                                Collection<? extends GrantedAuthority> authorities)
  {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); // must use super, as we override
  }

  @Override
  public Object getCredentials()
  {
    return credentials;
  }

  @Override
  public Object getPrincipal()
  {
    return principal;
  }

  public void markStale() {
    this.stale = true;
  }

  public boolean isStale()
  {
    return stale;
  }

  @Override
  public String toString()
  {
    return "JwtAuthenticationToken{" +
        "principal=" + ((CurrentUser)principal ).getUsername()+
        '}';
  }
}
