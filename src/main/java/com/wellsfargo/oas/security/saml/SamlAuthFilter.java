package com.wellsfargo.oas.security.saml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class SamlAuthFilter extends AbstractAuthenticationProcessingFilter {

  public SamlAuthFilter(String defaultFilterProcessesUrl) {

    super(defaultFilterProcessesUrl);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    if ("authenticated".equals(request.getParameter("saml"))) {
      return new UsernamePasswordAuthenticationToken(request, null, null);
    }
    throw new AuthenticationCredentialsNotFoundException("asas");
  }
}