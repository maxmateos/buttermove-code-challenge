package com.buttermove.demo.algorithms;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LongDistanceCalculationAlgorithm extends AbstractPriceCalculationAlgorithm {

  public LongDistanceCalculationAlgorithm(
      PriceAlgorithmConfigurationProperties algorithmConfigurations) {
    super(algorithmConfigurations);

    validateRequiredConfigValue("max-distance", getAlgorithmConfigs().getMaxDistance());
    validateRequiredConfigValue(
        "over-bounds-discount", getAlgorithmConfigs().getOverBoundsDiscount());
  }

  @Override
  public String getName() {
    return "long-distance-discount";
  }

  @Override
  public float applyStdDiscounts(
      float subTotal, float commissionRate, float kmDistance, float baseRate) {

    final Float maxDistance = getAlgorithmConfigs().getMaxDistance();
    if (kmDistance > maxDistance) {
      final float discount = subTotal * getAlgorithmConfigs().getOverBoundsDiscount();
      log.debug(
          "Applying over bounds discount of $'{}' with '{}' algorithm. Distance: {}",
          discount,
          getName(),
          kmDistance);
      return subTotal - discount;
    }

    return subTotal;
  }
}
