package com.wellsfargo.oas.security.ap;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessPhraseAuthenticationDetailsSource implements
    AuthenticationDetailsSource<HttpServletRequest, AccessPhraseUserDetails> {

  @Override
  public AccessPhraseUserDetails buildDetails(HttpServletRequest req) {

    if ("application/json".equals(req.getHeader("content-type"))) {

      return unmarshallJson(req);
    }
    else {

      return new AccessPhraseUserDetails(req.getParameter("fn"), req.getParameter("ssn"),
          req.getParameter("ap"), req.getParameter("dob"), req.getRequestURI());
    }
  }

  AccessPhraseUserDetails unmarshallJson(HttpServletRequest request) {

    try {
      StringBuffer sb = new StringBuffer();
      String line = null;

      BufferedReader reader = request.getReader();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      // json transformation
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(sb.toString(), AccessPhraseUserDetails.class);
    }
    catch (Exception e) {
      throw new RuntimeException("Exception occured while unmarshalling", e);
    }
  }

}
