package com.wellsfargo.oas.security.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.wellsfargo.oas.security.OasAuthenticationEntryPoint;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthenticationDetailsSource;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthenticationSuccessAndFailureHandler;
import com.wellsfargo.oas.security.saml.SamlAccessDecisionVoter;
import com.wellsfargo.oas.security.saml.SamlAuthFilter;
import com.wellsfargo.oas.security.saml.SamlAuthenticationProvider;
import com.wellsfargo.oas.security.saml.SamlAuthenticationSuccessAndFailureHandler;
import com.wellsfargo.oas.security.saml.SamlObjectPostProcessor;

@Configuration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/", "/home", "/error")
        .permitAll()
        .anyRequest()
        .authenticated()
        .withObjectPostProcessor(samlObjectPostProcessor())
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(new OasAuthenticationEntryPoint())
        .and()
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
        .addFilterBefore(accessPhraseAuthFilter(),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(samlAuthFilter(), UsernamePasswordAuthenticationFilter.class)
        .logout().addLogoutHandler(logOutHandler()).permitAll();
  }

  private LogoutHandler logOutHandler() {

    return new CompositeLogoutHandler(Arrays.asList(new SecurityContextLogoutHandler(),
        new CookieClearingLogoutHandler("kcookie")));

  }

  @Bean
  public ObjectPostProcessor<AffirmativeBased> samlObjectPostProcessor() {

    return new SamlObjectPostProcessor(samlAccessDecisionVoter());
  }

  @Bean
  public AccessDecisionVoter<Object> samlAccessDecisionVoter() {

    return new SamlAccessDecisionVoter();
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
        .setAuthenticationSuccessHandler(accessPhraseAuthenticationSuccessHandler());

    usernamePasswordAuthenticationFilter
        .setAuthenticationFailureHandler(accessPhraseAuthenticationFailureHandler());

    usernamePasswordAuthenticationFilter.setAuthenticationManager(new ProviderManager(
        Arrays.asList(accessPhraseAuthProvider())));

    return usernamePasswordAuthenticationFilter;
  }

  @Bean
  AuthenticationSuccessHandler accessPhraseAuthenticationSuccessHandler() {

    return new AccessPhraseAuthenticationSuccessAndFailureHandler();
  }

  @Bean
  AuthenticationFailureHandler accessPhraseAuthenticationFailureHandler() {

    return new AccessPhraseAuthenticationSuccessAndFailureHandler();
  }

  @Bean
  AccessPhraseAuthProvider accessPhraseAuthProvider() {

    return new AccessPhraseAuthProvider();
  }
}
