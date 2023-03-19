package com.buttermove.demo.handler;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.buttermove.demo.dto.PriceCalculationResponseDto;
import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.exception.ApiRequestValidationException;
import com.buttermove.demo.service.PriceCalculatorService;
import com.buttermove.demo.validator.PriceCalculatorRequestValidator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PriceCalculatorHandlerTest extends BaseHandlerTest {

  private PriceCalculatorHandler subject;

  private PriceCalculatorService priceCalculatorService;
  private PriceCalculatorRequestValidator priceCalculatorRequestValidator;

  @BeforeEach
  void setup() {

    priceCalculatorService = mock(PriceCalculatorService.class);
    priceCalculatorRequestValidator = mock(PriceCalculatorRequestValidator.class);

    subject = new PriceCalculatorHandler(priceCalculatorService, priceCalculatorRequestValidator);
  }

  @Test
  void calculatePrice_whenValidRequest_shouldReturnPriceCalculationResponseDto() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    final Float price = 333.0f;
    when(priceCalculatorService.calculatePrice(any(), any(), anyFloat(), anyFloat()))
        .thenReturn(Mono.just(price));

    StepVerifier.create(subject.getPriceForRequest(mockServerRequest))
        .assertNext(
            serverResponse -> {
              assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
              final PriceCalculationResponseDto response =
                  extractEntity(serverResponse, PriceCalculationResponseDto.class);
              assertThat(response.getCalculatedPrice()).isEqualTo(price);
              assertThat(response.getDate()).isNotNull();
            })
        .verifyComplete();

    verify(priceCalculatorService).calculatePrice(any(), any(), anyFloat(), anyFloat());
    verifyNoMoreInteractions(priceCalculatorService);
  }

  @Test
  void calculatePrice_whenValidatorError_shouldThrowApiRequestValidationException() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    doAnswer(
            i -> {
              i.getArgument(1, Errors.class).reject("error");
              return null;
            })
        .when(priceCalculatorRequestValidator)
        .validate(any(), any());

    StepVerifier.create(subject.getPriceForRequest(mockServerRequest))
        .expectError(ApiRequestValidationException.class)
        .verify();

    verify(priceCalculatorRequestValidator).validate(any(), any());
    verifyNoMoreInteractions(priceCalculatorService);
  }

  private MockServerRequest buildMockRequest() {
    final Map<String, String> pathVariables =
        Map.of(
            STATE_PATH_PARAM_KEY,
            "CA",
            ESTIMATION_MODE_PATH_PARAM_KEY,
            EstimationModes.NORMAL.getKey());
    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.put(KM_DISTANCE_PARAM_KEY, List.of("20.0"));
    queryParams.put(BASE_RATE_PARAM_KEY, List.of("10.0"));

    return MockServerRequest.builder()
        .pathVariables(pathVariables)
        .queryParams(queryParams)
        .build();
  }
}
