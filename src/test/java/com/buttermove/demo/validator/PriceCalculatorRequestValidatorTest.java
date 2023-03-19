package com.buttermove.demo.validator;

import static com.buttermove.demo.TestUtils.*;
import static com.buttermove.demo.constants.PriceCalculatorConstants.*;
import static com.buttermove.demo.constants.PriceCalculatorConstants.BASE_RATE_PARAM_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;

class PriceCalculatorRequestValidatorTest {

  private PriceCalculatorRequestValidator subject;

  private StatesConfigurationProperties statesConfigurationProperties;

  @BeforeEach
  void setup() {
    statesConfigurationProperties = buildMockStatesConfiguration();
    subject = new PriceCalculatorRequestValidator(statesConfigurationProperties);
  }

  @Test
  void supports_whenServerRequest_shouldReturnTrue() {
    assertThat(subject.supports(ServerRequest.class)).isTrue();
  }

  @Test
  void supports_whenNotServerRequest_shouldReturnFalse() {
    assertThat(subject.supports(Object.class)).isFalse();
  }

  @Test
  void validate_whenNoErrors_shouldNotInteractWithErrorsObject() {

    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(IP_CLIENT_HEADER_KEY, "33.34.35.36");

    final Map<String, String> pathParams =
        Map.of(
            STATE_PATH_PARAM_KEY,
            "CA",
            ESTIMATION_MODE_PATH_PARAM_KEY,
            EstimationModes.NORMAL.getKey());
    final MultiValueMap<String, String> queryParams =
        new LinkedMultiValueMap<>(
            Map.of(KM_DISTANCE_PARAM_KEY, List.of("100.0"), BASE_RATE_PARAM_KEY, List.of("10.0")));

    final ServerRequest request = buildMockRequest(httpHeaders, pathParams, queryParams);
    final Errors errors = mock(Errors.class);
    subject.validate(request, errors);
    verifyNoInteractions(errors);
  }

  @Test
  void validate_whenInvalidParameters_shouldInteractWithErrorsObject() {

    final Errors errors = mock(Errors.class);

    subject.validate(MockServerRequest.builder().build(), errors);
    verify(errors, times(5)).reject(any());
  }

  private static ServerRequest buildMockRequest(
      HttpHeaders headers,
      Map<String, String> pathParams,
      MultiValueMap<String, String> queryParams) {

    return MockServerRequest.builder()
        .headers(headers)
        .pathVariables(pathParams)
        .queryParams(queryParams)
        .build();
  }
}
