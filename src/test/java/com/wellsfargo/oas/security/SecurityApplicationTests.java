package com.wellsfargo.oas.security;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityApplicationTests {

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
  public void authProviderIsCalled() throws Exception {

    try {

      when(mockAccessPhraseAuthProvider.supports(any())).thenReturn(true);
      when(mockAccessPhraseAuthProvider.authenticate(any())).thenReturn(
          new UsernamePasswordAuthenticationToken("fn", "asas", null));

      mvc.perform(
          post("/login").param("fn", "fn").param("ssn", "ssn").param("ap", "ap")
              .param("dob", "dob")).andExpect(authenticated());

      verify(mockAccessPhraseAuthProvider, times(1)).authenticate(any());
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void pageShouldBeProtectedAndRedirectedToLoginPage() throws UnsupportedEncodingException, Exception {

    mvc.perform(get("/hello")).andExpect(redirectedUrl("/login"));

  }

  @Test
  public void pageShouldBeProtectedAndResponseShouldBeJson()
      throws UnsupportedEncodingException, Exception {

    mvc.perform(get("/hello").header("content-type", "application/json")).andExpect(
        content().string(containsString("This is working fine")));

  }
}
