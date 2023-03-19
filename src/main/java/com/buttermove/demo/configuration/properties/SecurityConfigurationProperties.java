package com.buttermove.demo.configuration.properties;

import java.util.Set;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security-config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityConfigurationProperties {

  private Set<User> users;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class User {
    private String username;
    private String password;
    private String role;
  }
}
