package com.buttermove.demo.handler;

import static com.buttermove.demo.TestUtils.buildMockAlgorithmConfigurations;
import static com.buttermove.demo.TestUtils.buildMockStatesConfiguration;
import static com.buttermove.demo.constants.PriceCalculatorConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.dto.GetAlgorithmConfigsResponseDto;
import com.buttermove.demo.dto.GetStateConfigsResponseDto;
import com.buttermove.demo.exception.RestResourceValidationException;
import com.buttermove.demo.validator.AlgorithmByNameRequestValidator;
import com.buttermove.demo.validator.StatesByCodeRequestValidator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.validation.Errors;
import reactor.test.StepVerifier;

class ConfigsHandlerTest extends BaseHandlerTest {

  private ConfigsHandler subject;

  private StatesConfigurationProperties statesConfigurationProperties;
  private PriceAlgorithmConfigurationProperties algorithmConfigurationProperties;

  private StatesByCodeRequestValidator statesByCodeRequestValidator;
  private AlgorithmByNameRequestValidator algorithmByNameRequestValidator;

  @BeforeEach
  void setup() {

    statesConfigurationProperties = buildMockStatesConfiguration();
    algorithmConfigurationProperties = buildMockAlgorithmConfigurations();

    statesByCodeRequestValidator = mock(StatesByCodeRequestValidator.class);
    algorithmByNameRequestValidator = mock(AlgorithmByNameRequestValidator.class);

    subject =
        new ConfigsHandler(
            statesConfigurationProperties,
            algorithmConfigurationProperties,
            statesByCodeRequestValidator,
            algorithmByNameRequestValidator);
  }

  @Test
  void getStatesConfig_shouldReturnPriceCalculationResponseDto() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    StepVerifier.create(subject.getStatesConfigs(mockServerRequest))
        .assertNext(
            serverResponse -> {
              assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
              final GetStateConfigsResponseDto response =
                  extractEntity(serverResponse, GetStateConfigsResponseDto.class);
              assertThat(response.getStateConfigs())
                  .usingRecursiveComparison()
                  .isEqualTo(buildMockStatesConfiguration().getStates());
            })
        .verifyComplete();

    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  @Test
  void getAlgorithmConfig_shouldReturnPriceCalculationResponseDto() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    StepVerifier.create(subject.getAlgorithmsConfigs(mockServerRequest))
        .assertNext(
            serverResponse -> {
              assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
              final GetAlgorithmConfigsResponseDto response =
                  extractEntity(serverResponse, GetAlgorithmConfigsResponseDto.class);
              assertThat(response.getAlgorithmConfigs())
                  .usingRecursiveComparison()
                  .isEqualTo(buildMockAlgorithmConfigurations().getAlgorithms());
            })
        .verifyComplete();

    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  @Test
  void getStateByCode_whenValidRequest_shouldReturnPriceCalculationResponseDto() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    StepVerifier.create(subject.getStateByCodeConfigs(mockServerRequest))
        .assertNext(
            serverResponse -> {
              assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
              final StatesConfigurationProperties.State response =
                  extractEntity(serverResponse, StatesConfigurationProperties.State.class);
              assertThat(response).isEqualTo(buildMockStatesConfiguration().getState("CA"));
            })
        .verifyComplete();

    verify(statesByCodeRequestValidator).validate(any(), any());
    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  @Test
  void getStateByCode_whenValidatorError_shouldThrowApiRequestValidationException() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    doAnswer(
            i -> {
              i.getArgument(1, Errors.class).reject("error");
              return null;
            })
        .when(statesByCodeRequestValidator)
        .validate(any(), any());

    StepVerifier.create(subject.getStateByCodeConfigs(mockServerRequest))
        .expectError(RestResourceValidationException.class)
        .verify();

    verify(statesByCodeRequestValidator).validate(any(), any());
    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  @Test
  void getAlgorithmByName_whenValidRequest_shouldReturnPriceCalculationResponseDto() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    StepVerifier.create(subject.getAlgorithmByNameConfigs(mockServerRequest))
        .assertNext(
            serverResponse -> {
              assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
              final PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
                  response =
                      extractEntity(
                          serverResponse,
                          PriceAlgorithmConfigurationProperties.PriceCalculationAlgorithmConfigs
                              .class);
              assertThat(response)
                  .isEqualTo(
                      buildMockAlgorithmConfigurations().getAlgorithmConfigs("standard-tax"));
            })
        .verifyComplete();

    verify(algorithmByNameRequestValidator).validate(any(), any());
    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  @Test
  void getAlgorithmByName_whenValidatorError_shouldThrowApiRequestValidationException() {

    final MockServerRequest mockServerRequest = buildMockRequest();

    doAnswer(
            i -> {
              i.getArgument(1, Errors.class).reject("error");
              return null;
            })
        .when(algorithmByNameRequestValidator)
        .validate(any(), any());

    StepVerifier.create(subject.getAlgorithmByNameConfigs(mockServerRequest))
        .expectError(RestResourceValidationException.class)
        .verify();

    verify(algorithmByNameRequestValidator).validate(any(), any());
    verifyNoMoreInteractions(statesByCodeRequestValidator, algorithmByNameRequestValidator);
  }

  private MockServerRequest buildMockRequest() {
    final Map<String, String> pathVariables =
        Map.of(STATE_PATH_PARAM_KEY, "CA", ALGORITHM_PATH_PARAM_KEY, "standard-tax");

    return MockServerRequest.builder().pathVariables(pathVariables).build();
  }
}
