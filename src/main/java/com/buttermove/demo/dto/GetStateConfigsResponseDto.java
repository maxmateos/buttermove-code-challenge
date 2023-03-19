package com.buttermove.demo.dto;

import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
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
public class GetStateConfigsResponseDto {

  private Set<StatesConfigurationProperties.State> stateConfigs;
}
