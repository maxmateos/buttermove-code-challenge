package com.buttermove.demo.configuration.properties;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "states-config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatesConfigurationProperties {

  private Set<State> states;
  private Map<String, State> statesByCode;

  @PostConstruct
  public void populateStatesByCode() {
    this.statesByCode =
        states.stream().collect(Collectors.toMap(State::getCode, Function.identity()));
  }

  public State getState(String code) {
    return statesByCode.get(code);
  }

  public Set<String> getValidStateCodes() {
    return states.stream().map(State::getCode).collect(Collectors.toSet());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class State {
    private String name;
    private String code;
    private Float standardRate;
    private Float premiumRate;
    private String calculationAlgorithm;
  }
}
