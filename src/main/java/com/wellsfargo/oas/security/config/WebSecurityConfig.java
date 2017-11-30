package com.wellsfargo.oas.security.config;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.wellsfargo.oas.security.OasAuthenticationAndAuthorizationErrorHandler;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthenticationDetailsSource;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthenticationSuccessAndFailureHandler;
import com.wellsfargo.oas.security.saml.SamlAuthFilter;
import com.wellsfargo.oas.security.saml.SamlAuthenticationProvider;
import com.wellsfargo.oas.security.saml.SamlAuthenticationSuccessAndFailureHandler;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
      .csrf()
        .disable()
      .authorizeRequests()
        .antMatchers("/", "/home", "/error", "/accessDenied", "/login")
          .permitAll()
        .anyRequest()
          .access("isFullyAuthenticated() and @kCookieCheck.test(request)")
      .and()
        .exceptionHandling()
          .authenticationEntryPoint(new OasAuthenticationAndAuthorizationErrorHandler())
          .accessDeniedHandler(new OasAuthenticationAndAuthorizationErrorHandler())
      .and()
        .addFilterBefore(accessPhraseAuthFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(samlAuthFilter(), UsernamePasswordAuthenticationFilter.class)
      .logout()
        .addLogoutHandler(logOutHandler());
  }

  private LogoutHandler logOutHandler() {

    return new CompositeLogoutHandler(Arrays.asList(new SecurityContextLogoutHandler(),
        new CookieClearingLogoutHandler("kcookie")));

  }

  @Bean
  SamlAuthFilter samlAuthFilter() {

    SamlAuthFilter samlAuthFilter = new SamlAuthFilter("/samlEntry");
    samlAuthFilter.setAuthenticationFailureHandler(samlAuthenticationFailureHandler());
    samlAuthFilter.setAuthenticationManager(new ProviderManager(Arrays
        .asList(samlAuthProvider())));
    return samlAuthFilter;
  }

  @Bean
  SamlAuthenticationProvider samlAuthProvider() {

    return new SamlAuthenticationProvider();
  }

  @Bean
  AuthenticationFailureHandler samlAuthenticationFailureHandler() {

    return new SamlAuthenticationSuccessAndFailureHandler();
  }

  @Bean
  UsernamePasswordAuthenticationFilter accessPhraseAuthFilter() {

    UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();

    usernamePasswordAuthenticationFilter
        .setAuthenticationDetailsSource(new AccessPhraseAuthenticationDetailsSource());

    usernamePasswordAuthenticationFilter
        .setAuthenticationSuccessHandler(accessPhraseAuthenticationHandler());

    usernamePasswordAuthenticationFilter
        .setAuthenticationFailureHandler(accessPhraseAuthenticationHandler());

    usernamePasswordAuthenticationFilter.setAuthenticationManager(new ProviderManager(
        Arrays.asList(accessPhraseAuthProvider())));

    return usernamePasswordAuthenticationFilter;
  }

  @Bean
  AccessPhraseAuthenticationSuccessAndFailureHandler accessPhraseAuthenticationHandler() {

    return new AccessPhraseAuthenticationSuccessAndFailureHandler();
  }

  @Bean
  AccessPhraseAuthProvider accessPhraseAuthProvider() {

    return new AccessPhraseAuthProvider();

  }

  @Bean
  Predicate<HttpServletRequest> kCookieCheck() {

    return request -> {
      if (request.getCookies() != null) {
        return Stream.of(request.getCookies()).anyMatch(c -> {
          return c.getName().equals("kcookie");
        });
      }
      return false;
    };
  }
}
