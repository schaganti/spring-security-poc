package com.wellsfargo.oas.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

final class CustomAuthProvider implements AuthenticationProvider {

  /**
   * @param webSecurityConfig
   */
  CustomAuthProvider() {

  }

  @Override
  public boolean supports(Class<?> authentication) {

    return authentication.isAssignableFrom(AccessPhraseAuthenticationToken.class);
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {

    AccessPhraseAuthenticationToken accessPhraseAuthenticationToken = (AccessPhraseAuthenticationToken) authentication;

    if ("username".equalsIgnoreCase(accessPhraseAuthenticationToken.getUserName())
        && "password".equalsIgnoreCase(accessPhraseAuthenticationToken.getPassword())
        && "ap".equalsIgnoreCase(accessPhraseAuthenticationToken.getAp())) {

      accessPhraseAuthenticationToken.setAuthenticated(true);
    }
    return accessPhraseAuthenticationToken;
  }
}