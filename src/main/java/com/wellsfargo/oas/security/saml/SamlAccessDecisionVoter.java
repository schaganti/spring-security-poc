package com.wellsfargo.oas.security.saml;

import java.util.Collection;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class SamlAccessDecisionVoter implements AccessDecisionVoter<Object> {

  @Override
  public boolean supports(ConfigAttribute attribute) {

    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {

    return true;
  }

  @Override
  public int vote(Authentication authentication, Object object,
      Collection<ConfigAttribute> attributes) {

    FilterInvocation filterInvocation = (FilterInvocation) object;
    HttpServletRequest request = filterInvocation.getHttpRequest();

    if (request.getCookies() != null) {
      boolean cookieExists = Stream.of(request.getCookies()).anyMatch(c -> {
        return c.getName().equals("kcookie");
      });
      if (cookieExists) {
        return AccessDecisionVoter.ACCESS_ABSTAIN;
      }
    }
    return AccessDecisionVoter.ACCESS_ABSTAIN;

  }
}
