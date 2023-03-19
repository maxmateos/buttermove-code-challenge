package com.buttermove.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityWebFilterChain filterChain(final ServerHttpSecurity http) throws Exception {
    return http.csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers("/v1/**")
        .authenticated()
        .anyExchange()
        .permitAll()
        .and()
        .httpBasic()
        .and()
        .formLogin()
        .disable()
        .logout()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(
            (swe, e) ->
                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
        .accessDeniedHandler(
            (swe, e) ->
                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
        .and()
        .build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService(
      @Value("${security.username}") String username,
      @Value("${security.password}") String password) {
    return new MapReactiveUserDetailsService(
        User.builder().username(username).password("{noop}" + password).roles("USER").build());
  }
}
