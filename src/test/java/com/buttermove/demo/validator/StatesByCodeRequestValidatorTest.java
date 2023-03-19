package com.buttermove.demo.validator;

import static com.buttermove.demo.TestUtils.buildMockStatesConfiguration;
import static com.buttermove.demo.constants.PriceCalculatorConstants.STATE_PATH_PARAM_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;

class StatesByCodeRequestValidatorTest {
  private StatesByCodeRequestValidator subject;

  private StatesConfigurationProperties statesConfigurationProperties;

  @BeforeEach
  void setup() {
    statesConfigurationProperties = buildMockStatesConfiguration();
    subject = new StatesByCodeRequestValidator(statesConfigurationProperties);
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

    final ServerRequest request =
        buildMockRequest(
            new HttpHeaders(), Map.of(STATE_PATH_PARAM_KEY, "CA"), new LinkedMultiValueMap<>());
    final Errors errors = mock(Errors.class);
    subject.validate(request, errors);
    verifyNoInteractions(errors);
  }

  @Test
  void validate_whenInvalidParameters_shouldInteractWithErrorsObject() {

    final Errors errors = mock(Errors.class);

    subject.validate(MockServerRequest.builder().build(), errors);
    verify(errors).reject(any());
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
