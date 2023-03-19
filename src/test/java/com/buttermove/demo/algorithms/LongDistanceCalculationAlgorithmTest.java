package com.buttermove.demo.algorithms;

import static com.buttermove.demo.TestUtils.*;
import static com.buttermove.demo.TestUtils.SUBTOTAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.exception.InvalidAlgorithmConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class LongDistanceCalculationAlgorithmTest {

  private LongDistanceCalculationAlgorithm subject;

  private static final float STANDARD_PRICE_WITHOUT_DISCOUNTS = BASE_RATE + (COMMISSION_RATE * 100);

  @BeforeEach
  void setUp() {

    subject = new LongDistanceCalculationAlgorithm(buildMockAlgorithmConfigurations());
  }

  @Test
  void constructor_whenEmptyRequiredConfigs_shouldThrowInvalidAlgorithmConfigException() {
    final PriceAlgorithmConfigurationProperties algorithmConfigurationProperties =
        buildMockAlgorithmConfigurations();
    algorithmConfigurationProperties
        .getAlgorithmConfigs(subject.getName())
        .setOverBoundsDiscount(null);
    assertThrows(
        InvalidAlgorithmConfigException.class,
        () -> new LongDistanceCalculationAlgorithm(algorithmConfigurationProperties));
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
        () -> new LongDistanceCalculationAlgorithm(algorithmConfigurationProperties));
  }

  @Test
  void getName_shouldReturnAlgorithmName() {
    assertThat(subject.getName()).isEqualTo("long-distance-discount");
  }

  @Test
  void getAlgorithmConfigs_shouldReturnLongDistanceDiscountConfigs() {
    assertThat(subject.getAlgorithmConfigs())
        .usingRecursiveComparison()
        .isEqualTo(buildMockLongDistanceAlgorithmConfigs());
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
                * subject.getAlgorithmConfigs().getOverBoundsDiscount());

    StepVerifier.create(
            subject.calculatePrice(
                EstimationModes.NORMAL, COMMISSION_RATE, HIGHEST_KM_DISTANCE, BASE_RATE))
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
  void applyStdDiscounts_whenUnderMaxDistance_shouldNotApplyDiscount() {

    assertThat(subject.applyStdDiscounts(SUBTOTAL, COMMISSION_RATE, LOWEST_KM_DISTANCE, BASE_RATE))
        .isEqualTo(SUBTOTAL);
  }

  @Test
  void applyStdDiscounts_whenOverMaxDistance_shouldApplyOverBoundsDiscount() {

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
