package com.wellsfargo.oas.security;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Configuration
  @Order(1)
  public static class AccessPhraseWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.authorizeRequests().antMatchers("/", "/home").permitAll().anyRequest()
          .authenticated().and().formLogin().loginPage("/login").permitAll().and()
          .addFilterAt(authFilter(), UsernamePasswordAuthenticationFilter.class).logout()
          .permitAll();
    }

    @Bean
    UsernamePasswordAuthenticationFilter authFilter() {

      UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();

      usernamePasswordAuthenticationFilter.setAuthenticationDetailsSource((
          HttpServletRequest req) -> new OasUserDetails(req.getParameter("fn"), req
          .getParameter("ssn"), req.getParameter("ap"), req.getParameter("dob")));

      usernamePasswordAuthenticationFilter.setAuthenticationManager(new ProviderManager(
          Arrays.asList(new AccessPhraseAuthProvider())));

      return usernamePasswordAuthenticationFilter;
    }
  }
}
