package com.wellsfargo.oas.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AbstractAuthenticationToken;

class AccessPhraseAuthenticationToken extends AbstractAuthenticationToken {

  String userName;

  String password;

  String ap;

  public AccessPhraseAuthenticationToken(HttpServletRequest request) {

    super(null);
    userName = request.getParameter("username");
    password = request.getParameter("password");
    ap = request.getParameter("ap");
  }

  public String getUserName() {

    return userName;
  }

  public String getPassword() {

    return password;
  }

  public String getAp() {

    return ap;
  }

  @Override
  public Object getCredentials() {

    // TODO Auto-generated method stub
    return password + "-" + ap;
  }

  @Override
  public boolean isAuthenticated() {

    return super.isAuthenticated();
  }

  @Override
  public Object getPrincipal() {

    return userName;
  }

}