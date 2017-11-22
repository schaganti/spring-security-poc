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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wellsfargo.oas.security.ap.AccessPhraseAuthProvider;
import com.wellsfargo.oas.security.ap.AccessPhraseAuthenticationDetailsSource;
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

      http.csrf()
          .disable()
          .authorizeRequests()
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

    /*
     * private AccessDecisionManager accessDecisionManager() {
     * 
     * return new UnanimousBased(Arrays.asList(getCustomVoter()));
     * 
     * }
     * 
     * private AccessDecisionVoter<? extends Object> getCustomVoter() {
     * 
     * return new AccessDecisionVoter<Object>() {
     * 
     * @Override public boolean supports(ConfigAttribute attribute) {
     * 
     * // TODO Auto-generated method stub return true; }
     * 
     * @Override public boolean supports(Class<?> clazz) {
     * 
     * // TODO Auto-generated method stub return true; }
     * 
     * @Override public int vote(Authentication authentication, Object object,
     * Collection<ConfigAttribute> attributes) {
     * 
     * return AccessDecisionVoter.ACCESS_GRANTED; } }; }
     */
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

      usernamePasswordAuthenticationFilter
          .setAuthenticationDetailsSource(new AccessPhraseAuthenticationDetailsSource());

      usernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler((
          HttpServletRequest request, HttpServletResponse response,
          Authentication authentication) -> {

        if ("application/json".equals(request.getHeader("content-type"))) {

          response.getWriter().write("This is working fine");
        }
        else {
          response.sendRedirect("/hello");
        }

      });

      usernamePasswordAuthenticationFilter.setAuthenticationManager(new ProviderManager(
          Arrays.asList(new AccessPhraseAuthProvider())));

      return usernamePasswordAuthenticationFilter;
    }
  }
}
