package com.buttermove.demo.algorithms;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class StandardTaxCalculationAlgorithm extends AbstractPriceCalculationAlgorithm {

  public StandardTaxCalculationAlgorithm(
      PriceAlgorithmConfigurationProperties algorithmConfigurations) {
    super(algorithmConfigurations);

    validateRequiredConfigValue("tax-rate", getAlgorithmConfigs().getTaxRate());
  }

  @Override
  public String getName() {
    return "standard-tax";
  }

  @Override
  public Mono<Float> calculateStandardPriceWithoutDiscounts(
      float commissionRate, float kmDistance, float baseRate) {

    final float subTotal = baseRate + (commissionRate * 100);
    final float tax = subTotal * getAlgorithmConfigs().getTaxRate();
    return Mono.just(subTotal + tax);
  }

  @Override
  public float applyStdDiscounts(
      float subTotal, float commissionRate, float kmDistance, float baseRate) {
    return subTotal;
  }
}
