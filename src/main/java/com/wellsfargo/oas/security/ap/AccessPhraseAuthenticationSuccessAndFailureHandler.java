package com.wellsfargo.oas.security.ap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class AccessPhraseAuthenticationSuccessAndFailureHandler implements
    AuthenticationSuccessHandler, AuthenticationFailureHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) throws IOException,
      ServletException {

    if ("application/json".equals(request.getHeader("content-type"))) {

      response.getWriter().write("This is working fine");
    }
    else {
      response.sendRedirect("/hello");
    }

  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    if ("application/json".equals(request.getHeader("content-type"))) {

      response.getWriter().write("This is working fine");
    }
    else {
      response.sendRedirect("/login");
    }

  }

}
