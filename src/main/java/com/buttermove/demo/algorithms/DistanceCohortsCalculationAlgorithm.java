package com.buttermove.demo.algorithms;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DistanceCohortsCalculationAlgorithm extends AbstractPriceCalculationAlgorithm {

  @Autowired
  public DistanceCohortsCalculationAlgorithm(
      PriceAlgorithmConfigurationProperties algorithmConfigurations) {
    super(algorithmConfigurations);

    validateRequiredConfigValue("min-distance", getAlgorithmConfigs().getMinDistance());
    validateRequiredConfigValue("max-distance", getAlgorithmConfigs().getMaxDistance());
    validateRequiredConfigValue(
        "over-bounds-discount", getAlgorithmConfigs().getOverBoundsDiscount());
    validateRequiredConfigValue(
        "within-bounds-discount", getAlgorithmConfigs().getWithinBoundsDiscount());
  }

  @Override
  public String getName() {
    return "distance-cohorts-discount";
  }

  @Override
  public float applyStdDiscounts(
      float subTotal, float commissionRate, float kmDistance, float baseRate) {

    final Float minDistance = getAlgorithmConfigs().getMinDistance();
    final Float maxDistance = getAlgorithmConfigs().getMaxDistance();

    if (kmDistance < minDistance) {
      final float discount = subTotal * getAlgorithmConfigs().getUnderBoundsDiscount();
      log.debug(
          "Applying under bounds discount of $'{}' with '{}' algorithm. Distance: {}",
          discount,
          getName(),
          kmDistance);
      return subTotal - discount;
    } else if (kmDistance > maxDistance) {
      final float discount = subTotal * getAlgorithmConfigs().getOverBoundsDiscount();
      log.debug(
          "Applying over bounds discount of $'{}' with '{}' algorithm. Distance: {}",
          discount,
          getName(),
          kmDistance);
      return subTotal - discount;
    } else {
      final float discount = subTotal * getAlgorithmConfigs().getWithinBoundsDiscount();
      log.debug(
          "Applying within bounds discount of $'{}' with '{}' algorithm. Distance: {}",
          discount,
          getName(),
          kmDistance);
      return subTotal - discount;
    }
  }
}
