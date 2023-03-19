package com.buttermove.demo.algorithms;

import static com.buttermove.demo.TestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.exception.InvalidAlgorithmConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class DistanceCohortsCalculationAlgorithmTest {

  private DistanceCohortsCalculationAlgorithm subject;

  private static final float STANDARD_PRICE_WITHOUT_DISCOUNTS = BASE_RATE + (COMMISSION_RATE * 100);

  @BeforeEach
  void setUp() {

    subject = new DistanceCohortsCalculationAlgorithm(buildMockAlgorithmConfigurations());
  }

  @Test
  void constructor_whenEmptyRequiredConfigs_shouldThrowInvalidAlgorithmConfigException() {
    final PriceAlgorithmConfigurationProperties algorithmConfigurationProperties =
        buildMockAlgorithmConfigurations();
    algorithmConfigurationProperties
        .getAlgorithmConfigs(subject.getName())
        .setWithinBoundsDiscount(null);
    assertThrows(
        InvalidAlgorithmConfigException.class,
        () -> new DistanceCohortsCalculationAlgorithm(algorithmConfigurationProperties));
  }

  @Test
  void constructor_whenEmptyPremiumRequiredConfigs_shouldThrowInvalidAlgorithmConfigException() {
    final PriceAlgorithmConfigurationProperties algorithmConfigurationProperties =
        buildMockAlgorithmConfigurations();
    algorithmConfigurationProperties
        .getAlgorithmConfigs(subject.getName())
        .getPremiumAlgorithmConfigs()
        .setOverBoundsDiscount(null);
    assertThrows(
        InvalidAlgorithmConfigException.class,
        () -> new DistanceCohortsCalculationAlgorithm(algorithmConfigurationProperties));
  }

  @Test
  void getName_shouldReturnAlgorithmName() {
    assertThat(subject.getName()).isEqualTo("distance-cohorts-discount");
  }

  @Test
  void getAlgorithmConfigs_shouldReturnDistanceCohortsConfigs() {
    assertThat(subject.getAlgorithmConfigs())
        .usingRecursiveComparison()
        .isEqualTo(buildMockDistanceCohortsAlgorithmConfigs());
  }

  @Test
  void calculateStandardPriceWithoutDiscounts_shouldReturnBaseRatePlusCommission() {

    StepVerifier.create(
            subject.calculateStandardPriceWithoutDiscounts(COMMISSION_RATE, KM_DISTANCE, BASE_RATE))
        .expectNext(STANDARD_PRICE_WITHOUT_DISCOUNTS)
        .verifyComplete();
  }

  @Test
  void calculatePrice_whenNormalEstimation_shouldApplyNormalDiscounts() {
    final float expectedValue =
        STANDARD_PRICE_WITHOUT_DISCOUNTS
            - (STANDARD_PRICE_WITHOUT_DISCOUNTS
                * subject.getAlgorithmConfigs().getWithinBoundsDiscount());

    StepVerifier.create(
            subject.calculatePrice(EstimationModes.NORMAL, COMMISSION_RATE, KM_DISTANCE, BASE_RATE))
        .assertNext(response -> assertThat(response).isEqualTo(expectedValue))
        .verifyComplete();
  }

  @Test
  void calculatePrice_whenPremiumEstimation_shouldApplyPremiumDiscounts() {
    final float expectedValue =
        STANDARD_PRICE_WITHOUT_DISCOUNTS
            - (STANDARD_PRICE_WITHOUT_DISCOUNTS
                * subject
                    .getAlgorithmConfigs()
                    .getPremiumAlgorithmConfigs()
                    .getOverBoundsDiscount());

    StepVerifier.create(
            subject.calculatePrice(
                EstimationModes.PREMIUM, COMMISSION_RATE, HIGHEST_KM_DISTANCE, BASE_RATE))
        .assertNext(response -> assertThat(response).isEqualTo(expectedValue))
        .verifyComplete();
  }

  @Test
  void applyStdDiscounts_whenUnderAllCohorts_shouldApplyUnderBoundsDiscount() {

    final float expectedValue =
        SUBTOTAL - (SUBTOTAL * subject.getAlgorithmConfigs().getUnderBoundsDiscount());
    assertThat(subject.applyStdDiscounts(SUBTOTAL, COMMISSION_RATE, LOWEST_KM_DISTANCE, BASE_RATE))
        .isEqualTo(expectedValue);
  }

  @Test
  void applyStdDiscounts_whenWithinCohorts_shouldApplyWithinBoundsDiscount() {

    final float expectedValue =
        SUBTOTAL - (SUBTOTAL * subject.getAlgorithmConfigs().getWithinBoundsDiscount());
    assertThat(subject.applyStdDiscounts(SUBTOTAL, COMMISSION_RATE, KM_DISTANCE, BASE_RATE))
        .isEqualTo(expectedValue);
  }

  @Test
  void applyStdDiscounts_whenOverCohorts_shouldApplyOverBoundsDiscount() {

    final float expectedValue =
        SUBTOTAL - (SUBTOTAL * subject.getAlgorithmConfigs().getOverBoundsDiscount());
    assertThat(subject.applyStdDiscounts(SUBTOTAL, COMMISSION_RATE, HIGHEST_KM_DISTANCE, BASE_RATE))
        .isEqualTo(expectedValue);
  }

  @Test
  void applyPremiumDiscounts_whenOverMaxDistance_shouldApplyOverBoundsDiscount() {

    final float expectedValue =
        SUBTOTAL
            - (SUBTOTAL
                * subject
                    .getAlgorithmConfigs()
                    .getPremiumAlgorithmConfigs()
                    .getOverBoundsDiscount());
    assertThat(
            subject.applyPremiumDiscounts(
                SUBTOTAL, COMMISSION_RATE, HIGHEST_KM_DISTANCE, BASE_RATE))
        .isEqualTo(expectedValue);
  }

  @Test
  void applyPremiumDiscounts_whenNotOverMaxDistance_shouldNotApplyDiscount() {

    assertThat(subject.applyPremiumDiscounts(SUBTOTAL, COMMISSION_RATE, KM_DISTANCE, BASE_RATE))
        .isEqualTo(SUBTOTAL);
  }
}
