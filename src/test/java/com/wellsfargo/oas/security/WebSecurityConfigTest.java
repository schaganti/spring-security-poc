package com.wellsfargo.oas.security;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebSecurityConfigTest {

  @Autowired
  private WebApplicationContext context;

  MockMvc mvc;

  @MockBean
  AccessPhraseAuthProvider mockAccessPhraseAuthProvider;

  @Before
  public void before() {

    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  public void authenticationShouldBeSuccessfulWhenAuthProviderAuthenticatesTheUser()
      throws Exception {

    when(mockAccessPhraseAuthProvider.supports(any())).thenReturn(true);
    when(mockAccessPhraseAuthProvider.authenticate(any())).thenReturn(
        new UsernamePasswordAuthenticationToken("fn", "details", null));

    mvc.perform(
        post("/login").param("fn", "fn").param("ssn", "ssn").param("ap", "ap")
            .param("dob", "dob")).andExpect(authenticated())
        .andExpect(cookie().exists("kcookie"));

    verify(mockAccessPhraseAuthProvider, times(1)).authenticate(any());
  }

  @Test
  public void authenticationShouldFailWhenAuthProviderFailesToAuthenticateTheUser()
      throws Exception {

    when(mockAccessPhraseAuthProvider.supports(any())).thenReturn(true);
    when(mockAccessPhraseAuthProvider.authenticate(any())).thenThrow(
        new BadCredentialsException("Invalid accessphrase credentials"));

    mvc.perform(
        post("/login").param("fn", "fn").param("ssn", "ssn").param("ap", "ap")
            .param("dob", "dob")).andExpect(unauthenticated());

    verify(mockAccessPhraseAuthProvider, times(1)).authenticate(any());
  }

  @Test
  public void requestShouldBeRedirectedToLoginPageWhenProtectedResourceIsAccessedWithoutAuthentication()
      throws UnsupportedEncodingException, Exception {

    mvc.perform(get("/hello")).andExpect(redirectedUrl("/accessDenied"));

  }

  @Test
  public void errorResponseShouldBeReturnedWhenProtectedResourceIsAccessedThroughAPICall()
      throws UnsupportedEncodingException, Exception {

    mvc.perform(get("/hello").header("content-type", "application/json"))
        .andExpect(jsonPath("$.statusCode", is("403")))
        .andExpect(jsonPath("$.messageCode", is("1000")))
        .andExpect(jsonPath("$.message", is("Unauthorized access")));
  }

  @Test
  public void protectedResourceShouldBeAccessableToAuthenticatedUser() throws Exception {

    mvc.perform(
        get("/hello").cookie(new Cookie("kcookie", "authenticated"))
            .with(
                authentication(new UsernamePasswordAuthenticationToken("fn", "details",
                    null)))).andExpect(status().isOk())
        .andExpect(content().string(containsString("Protected Page")));

  }
  
  @Test
  public void protectedResourceAccessedAfterAuthenticationWithoutKCookieShouldRenderAccessDeniedPage() throws Exception {

    mvc.perform(get("/hello")
            .with(
                authentication(new UsernamePasswordAuthenticationToken("fn", "details",
                    null)))).andExpect(redirectedUrl("/accessDenied"));
  }


  @Test
  public void samlEntryShouldBeAuthenticatedSuccessfullyAndCreateKCookie()
      throws Exception {

    mvc.perform(get("/samlEntry").param("saml", "authenticated"))
        .andExpect(authenticated()).andExpect(cookie().exists("kcookie"));

  }

  @Test
  public void samlEntryShouldFailAuthentication() throws Exception {

    mvc.perform(get("/samlEntry").param("saml", "wrongtoken"))
        .andExpect(unauthenticated()).andExpect(cookie().doesNotExist("kcookie"));
  }

  @Test
  public void logOutShouldInvalidateAuthenticationAndCallLogOutSuccessHandler()
      throws Exception {

    mvc.perform(logout("/logOut")).andExpect(unauthenticated());
  }

}
