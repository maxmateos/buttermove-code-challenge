package com.buttermove.demo.algorithms;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import reactor.core.publisher.Mono;

public interface PriceCalculationAlgorithm {

  String getName();

  PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs getAlgorithmConfigs();

  Mono<Float> calculatePrice(
      EstimationModes estimationMode, float commissionRate, float kmDistance, float baseRate);

  Mono<Float> calculateStandardPriceWithoutDiscounts(
      float commissionRate, float kmDistance, float baseRate);

  Mono<Float> calculatePremiumPriceWithoutDiscounts(
      float commissionRate, float kmDistance, float baseRate);

  float applyStdDiscounts(float subTotal, float commissionRate, float kmDistance, float baseRate);

  float applyPremiumDiscounts(
      float subTotal, float commissionRate, float kmDistance, float baseRate);
}
