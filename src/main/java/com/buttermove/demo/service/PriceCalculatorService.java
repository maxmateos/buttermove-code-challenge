package com.buttermove.demo.service;

import com.buttermove.demo.enumeration.EstimationModes;
import reactor.core.publisher.Mono;

public interface PriceCalculatorService {
  public Mono<Float> calculatePrice(
      String stateCode, EstimationModes estimationMode, float kmDistance, float baseRate);
}
