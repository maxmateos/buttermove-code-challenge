package com.buttermove.demo.dto;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAlgorithmConfigsResponseDto {

  private Set<PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs>
      algorithmConfigs;
}
