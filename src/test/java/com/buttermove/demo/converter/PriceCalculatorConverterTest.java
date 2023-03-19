package com.buttermove.demo.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.buttermove.demo.dto.PriceCalculationResponseDto;
import org.junit.jupiter.api.Test;

class PriceCalculatorConverterTest {
  @Test
  void transform_priceCalculatorResponse() {

    final Float price = 3.333f;
    final PriceCalculationResponseDto response =
        PriceCalculatorConverter.PRICE_TO_PRICE_CALCULATION_RESPONSE.convert(price);
    assertThat(response.getCalculatedPrice()).isEqualTo(price);
    assertThat(response.getDate()).isNotNull();
  }
}
