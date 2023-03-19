package com.buttermove.demo.configuration.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "price-algorithm-configs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceAlgorithmConfigurationProperties {

  private Set<PriceCalculationAlgorithmConfigs> algorithms;
  private Map<String, PriceCalculationAlgorithmConfigs> algorithmsByName;

  @PostConstruct
  public void populateAlgorithmsByName() {
    this.algorithmsByName =
        algorithms.stream()
            .collect(
                Collectors.toMap(PriceCalculationAlgorithmConfigs::getName, Function.identity()));
  }

  public PriceCalculationAlgorithmConfigs getAlgorithmConfigs(String name) {
    return algorithmsByName.get(name);
  }

  public Set<String> getValidAlgorithmNames() {
    return algorithms.stream()
        .map(PriceCalculationAlgorithmConfigs::getName)
        .collect(Collectors.toSet());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class PriceCalculationAlgorithmConfigs {
    private String name;
    private Float taxRate;
    private Float minDistance;
    private Float maxDistance;
    private Float underBoundsDiscount;
    private Float withinBoundsDiscount;
    private Float overBoundsDiscount;
    private PremiumAlgorithmConfigs premiumAlgorithmConfigs;
  }

  @EqualsAndHashCode(callSuper = true)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class PremiumAlgorithmConfigs extends PriceCalculationAlgorithmConfigs {}
}
