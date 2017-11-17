package com.wellsfargo.oas.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AccessPhraseAuthProvider implements AuthenticationProvider {

  /**
   * @param webSecurityConfig
   */
  public AccessPhraseAuthProvider() {

  }

  @Override
  public boolean supports(Class<?> authentication) {

    return true;
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {

    OasUserDetails userDetails = (OasUserDetails) authentication.getDetails();
    if (userDetails.getFn().equalsIgnoreCase("fn")) {
      return new UsernamePasswordAuthenticationToken("fn", authentication.getDetails(),
          null);
    }
    throw new AuthenticationCredentialsNotFoundException("asas");

  }
}