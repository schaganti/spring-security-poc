package com.wellsfargo.oas.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests().antMatchers("/", "/home").permitAll().anyRequest()
        .authenticated().and().formLogin().loginPage("/login").permitAll().and().logout()
        .permitAll().and().addFilterBefore(authFilter(), CustomAuthFilter.class);
  }

  @Bean
  UsernamePasswordAuthenticationFilter authFilter() {

    CustomAuthFilter customAuthFilter = new CustomAuthFilter();

    List<AuthenticationProvider> authenticationProviderList = new ArrayList<AuthenticationProvider>();
    authenticationProviderList.add(new CustomAuthProvider());

    AuthenticationManager authenticationManager = new ProviderManager(
        authenticationProviderList);

    customAuthFilter.setAuthenticationManager(authenticationManager);

    return customAuthFilter;
  }

}