package com.wellsfargo.oas.security.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wellsfargo.oas.security.OasUserDetails;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;
import com.wellsfargo.oas.security.saml.SamlAuthFilter;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class })
public class WebSecurityConfig {

  @Configuration
  @Order(1)
  public static class AccessPhraseWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.authorizeRequests()
          .antMatchers("/", "/home", "/error")
          .permitAll()
          .anyRequest()
          .authenticated()
          .and()
          .formLogin()
          .loginPage("/login")
          .permitAll()
          .and()
          .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(getSamlAuthFilter(),
              UsernamePasswordAuthenticationFilter.class).logout().permitAll();
    }

    private SamlAuthFilter getSamlAuthFilter() {

      SamlAuthFilter samlAuthFilter = new SamlAuthFilter("/samlEntry");
      samlAuthFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

          response.sendRedirect("/error");
        }
      });
      return samlAuthFilter;
    }

    @Bean
    UsernamePasswordAuthenticationFilter authFilter() {

      UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();

      usernamePasswordAuthenticationFilter.setAuthenticationDetailsSource((
          HttpServletRequest req) -> new OasUserDetails(req.getParameter("fn"), req
          .getParameter("ssn"), req.getParameter("ap"), req.getParameter("dob"), req
          .getRequestURI()));

      usernamePasswordAuthenticationFilter.setAuthenticationManager(new ProviderManager(
          Arrays.asList(new AccessPhraseAuthProvider())));

      return usernamePasswordAuthenticationFilter;
    }
  }
}
