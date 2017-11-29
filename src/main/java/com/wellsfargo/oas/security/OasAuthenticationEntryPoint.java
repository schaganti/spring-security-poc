package com.wellsfargo.oas.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OasAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    if ("application/json".equals(request.getHeader("content-type"))) {

      response.getWriter().write(
          new ObjectMapper().writeValueAsString(new ErrorInfo("403", "1000",
              "Unauthorized access", null)));
    }
    else {
      response.sendRedirect("/login");
    }

  }

}
