package com.buttermove.demo;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import java.util.Set;

public class TestUtils {

  public static final float SUBTOTAL = 100.0f;
  public static final float COMMISSION_RATE = 50.0f;
  public static final float KM_DISTANCE = 25.0f;
  public static final float BASE_RATE = 10.0f;
  public static final float TAX_RATE = 0.21f;

  public static final float LOWEST_KM_DISTANCE = -100f;
  public static final float HIGHEST_KM_DISTANCE = 100000f;

  public static PriceAlgorithmConfigurationProperties buildMockAlgorithmConfigurations() {

    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
        distanceCohortsAlgorithmConfigs = buildMockDistanceCohortsAlgorithmConfigs();
    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
        standardTaxAlgorithmConfigs = buildMockStandardTaxAlgorithmConfigs();
    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
        longDistanceAlgorithmConfigs = buildMockLongDistanceAlgorithmConfigs();

    final PriceAlgorithmConfigurationProperties algorithmConfigurationProperties =
        new PriceAlgorithmConfigurationProperties();
    algorithmConfigurationProperties.setAlgorithms(
        Set.of(
            distanceCohortsAlgorithmConfigs,
            standardTaxAlgorithmConfigs,
            longDistanceAlgorithmConfigs));
    algorithmConfigurationProperties.populateAlgorithmsByName();

    return algorithmConfigurationProperties;
  }

  public static PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs
      buildMockPremiumAlgorithmConfigurations() {

    final PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs algorithmConfigs =
        new PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs();
    algorithmConfigs.setMaxDistance(25f);
    algorithmConfigs.setOverBoundsDiscount(0.05f);
    return algorithmConfigs;
  }

  public static PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
      buildMockDistanceCohortsAlgorithmConfigs() {

    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs algorithmConfigs =
        new PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs();
    algorithmConfigs.setName("distance-cohorts-discount");
    algorithmConfigs.setMinDistance(20.0f);
    algorithmConfigs.setMaxDistance(30.0f);
    algorithmConfigs.setUnderBoundsDiscount(0.0f);
    algorithmConfigs.setWithinBoundsDiscount(0.03f);
    algorithmConfigs.setOverBoundsDiscount(0.05f);
    algorithmConfigs.setPremiumAlgorithmConfigs(buildMockPremiumAlgorithmConfigurations());

    return algorithmConfigs;
  }

  public static PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
      buildMockStandardTaxAlgorithmConfigs() {

    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs algorithmConfigs =
        new PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs();
    algorithmConfigs.setName("standard-tax");
    algorithmConfigs.setTaxRate(TAX_RATE);
    algorithmConfigs.setPremiumAlgorithmConfigs(buildMockPremiumAlgorithmConfigurations());

    return algorithmConfigs;
  }

  public static PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
      buildMockLongDistanceAlgorithmConfigs() {

    final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs algorithmConfigs =
        new PriceAlgorithmConfigurationProperties.PremiumAlgorithmConfigs();
    algorithmConfigs.setName("long-distance-discount");
    algorithmConfigs.setMaxDistance(26f);
    algorithmConfigs.setOverBoundsDiscount(0.05f);
    algorithmConfigs.setPremiumAlgorithmConfigs(buildMockPremiumAlgorithmConfigurations());

    return algorithmConfigs;
  }

  public static StatesConfigurationProperties buildMockStatesConfiguration() {

    final StatesConfigurationProperties.State distanceCohortsAlgorithmConfigs =
        buildMockStateConfiguration();

    final StatesConfigurationProperties algorithmConfigurationProperties =
        new StatesConfigurationProperties();
    algorithmConfigurationProperties.setStates(Set.of(distanceCohortsAlgorithmConfigs));
    algorithmConfigurationProperties.populateStatesByCode();

    return algorithmConfigurationProperties;
  }

  public static StatesConfigurationProperties.State buildMockStateConfiguration() {

    final StatesConfigurationProperties.State stateConfigs =
        new StatesConfigurationProperties.State();
    stateConfigs.setCode("CA");
    stateConfigs.setName("California");
    stateConfigs.setStandardRate(0.23f);
    stateConfigs.setPremiumRate(0.33f);
    stateConfigs.setCalculationAlgorithm("long-distance-discount");

    return stateConfigs;
  }
}
