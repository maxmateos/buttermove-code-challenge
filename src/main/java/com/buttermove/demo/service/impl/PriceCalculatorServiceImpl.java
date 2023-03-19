package com.buttermove.demo.service.impl;

import com.buttermove.demo.algorithms.PriceCalculationAlgorithm;
import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.service.PriceCalculatorService;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PriceCalculatorServiceImpl implements PriceCalculatorService {

  private final StatesConfigurationProperties statesConfigurationProperties;
  private final Map<String, PriceCalculationAlgorithm> algorithmsByName;

  public PriceCalculatorServiceImpl(
      StatesConfigurationProperties statesConfigurationProperties,
      Set<PriceCalculationAlgorithm> calculationAlgorithms) {
    this.statesConfigurationProperties = statesConfigurationProperties;

    algorithmsByName =
        calculationAlgorithms.stream()
            .collect(Collectors.toMap(PriceCalculationAlgorithm::getName, Function.identity()));
  }

  @Override
  public Mono<Float> calculatePrice(
      String stateCode, EstimationModes estimationMode, float kmDistance, float baseRate) {

    // Mono in SpringWebFlux is an async/non-blocking wrapper, usually service layer will call to db
    // or other services. This allows for lazy evaluation, non-blocking requests, and overall
    // asynchronousness.
    return Mono.just(statesConfigurationProperties.getState(stateCode))
        .flatMap(
            state -> {
              final float commissionRate =
                  (EstimationModes.PREMIUM.equals(estimationMode))
                      ? state.getPremiumRate()
                      : state.getStandardRate();
              return algorithmsByName
                  .get(state.getCalculationAlgorithm())
                  .calculatePrice(estimationMode, commissionRate, kmDistance, baseRate);
            });
  }
}
