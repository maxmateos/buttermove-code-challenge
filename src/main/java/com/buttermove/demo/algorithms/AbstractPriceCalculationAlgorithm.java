package com.buttermove.demo.algorithms;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.exception.InvalidAlgorithmConfigException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractPriceCalculationAlgorithm implements PriceCalculationAlgorithm {

  protected final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
      algorithmConfigurations;

  public AbstractPriceCalculationAlgorithm(
      PriceAlgorithmConfigurationProperties algorithmConfigurations) {
    this.algorithmConfigurations = algorithmConfigurations.getAlgorithmConfigs(getName());

    validateRequiredConfigValue(
        "premium-discount-configs.max-distance",
        getAlgorithmConfigs().getPremiumAlgorithmConfigs().getMaxDistance());
    validateRequiredConfigValue(
        "premium-discount-configs.over-bounds-discount",
        getAlgorithmConfigs().getPremiumAlgorithmConfigs().getOverBoundsDiscount());
  }

  @Override
  public PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
      getAlgorithmConfigs() {
    return algorithmConfigurations;
  }

  @Override
  public Mono<Float> calculatePrice(
      EstimationModes estimationMode, float commissionRate, float kmDistance, float baseRate) {

    final Mono<Float> price;
    if (EstimationModes.PREMIUM.equals(estimationMode)) {
      price =
          calculatePremiumPriceWithoutDiscounts(commissionRate, kmDistance, baseRate)
              .map(
                  subTotalWithoutDiscounts ->
                      applyPremiumDiscounts(
                          subTotalWithoutDiscounts, commissionRate, kmDistance, baseRate));
    } else {
      price =
          calculateStandardPriceWithoutDiscounts(commissionRate, kmDistance, baseRate)
              .map(
                  stdSubtotal ->
                      applyStdDiscounts(stdSubtotal, commissionRate, kmDistance, baseRate));
    }

    return price.doOnNext(
        total ->
            log.debug(
                "Calculated {} Price for ${}, with '{}' algorithm.",
                estimationMode.getKey(),
                total,
                getName()));
  }

  @Override
  public Mono<Float> calculateStandardPriceWithoutDiscounts(
      float commissionRate, float kmDistance, float baseRate) {

    // Weird to add commission rate % to rate, but followed requirements after confirming this was
    // the expected behavior.
    // Real use cases tend to calculate commission by doing baseRate * commission.
    return Mono.just(baseRate + (commissionRate * 100));
  }

  @Override
  public Mono<Float> calculatePremiumPriceWithoutDiscounts(
      float commissionRate, float kmDistance, float baseRate) {
    return calculateStandardPriceWithoutDiscounts(commissionRate, kmDistance, baseRate);
  }

  @Override
  public float applyPremiumDiscounts(
      float subTotal, float commissionRate, float kmDistance, float baseRate) {
    final Float maxDistance = getAlgorithmConfigs().getPremiumAlgorithmConfigs().getMaxDistance();
    if (kmDistance > maxDistance) {
      final float discount =
          subTotal * getAlgorithmConfigs().getPremiumAlgorithmConfigs().getOverBoundsDiscount();
      log.debug(
          "Applying premium discount of $'{}' with '{}' algorithm. Distance: {}",
          discount,
          getName(),
          kmDistance);
      return subTotal - discount;
    }

    return subTotal;
  }

  protected void validateRequiredConfigValue(String configName, Object configValue) {
    if (configValue == null) {
      throw new InvalidAlgorithmConfigException(
          String.format(
              "Invalid algorithm configurations for '%s' algorithm. Field '%s' is required and cannot be null",
              getName(), configName));
    }
  }
}
