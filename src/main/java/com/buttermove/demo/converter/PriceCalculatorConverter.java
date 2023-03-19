package com.buttermove.demo.converter;

import com.buttermove.demo.dto.PriceCalculationResponseDto;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceCalculatorConverter {

  private static final SimpleDateFormat RFC_RFC3339_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
  public static final DomainDtoConverter<Float, PriceCalculationResponseDto>
      PRICE_TO_PRICE_CALCULATION_RESPONSE =
          calculatedPrice ->
              PriceCalculationResponseDto.builder()
                  .calculatedPrice(calculatedPrice)
                  .date(RFC_RFC3339_DATE_FORMAT.format(new Date()))
                  .build();
}
