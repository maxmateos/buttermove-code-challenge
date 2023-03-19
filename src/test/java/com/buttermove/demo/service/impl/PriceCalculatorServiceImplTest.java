package com.buttermove.demo.service.impl;

import static com.buttermove.demo.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

import com.buttermove.demo.algorithms.PriceCalculationAlgorithm;
import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PriceCalculatorServiceImplTest {

  private PriceCalculatorServiceImpl subject;

  private StatesConfigurationProperties statesConfigurationProperties;
  private Set<PriceCalculationAlgorithm> calculationAlgorithms;

  private static final Float PRICE = 123f;
  private PriceCalculationAlgorithm priceCalculationAlgorithmMock;

  @BeforeEach
  void setup() {

    statesConfigurationProperties = buildMockStatesConfiguration();

    priceCalculationAlgorithmMock = mock(PriceCalculationAlgorithm.class);
    when(priceCalculationAlgorithmMock.getName()).thenReturn("long-distance-discount");
    when(priceCalculationAlgorithmMock.calculatePrice(any(), anyFloat(), anyFloat(), anyFloat()))
        .thenReturn(Mono.just(PRICE));
    calculationAlgorithms = Set.of(priceCalculationAlgorithmMock);

    subject = new PriceCalculatorServiceImpl(statesConfigurationProperties, calculationAlgorithms);
  }

  @Test
  void calculatePrice_shouldInvokeAlgorithmServiceAssignedToState() {

    StepVerifier.create(
            subject.calculatePrice("CA", EstimationModes.NORMAL, KM_DISTANCE, BASE_RATE))
        .expectNext(PRICE)
        .verifyComplete();

    verify(priceCalculationAlgorithmMock).calculatePrice(any(), anyFloat(), anyFloat(), anyFloat());
    verify(priceCalculationAlgorithmMock).getName();
  }
}
