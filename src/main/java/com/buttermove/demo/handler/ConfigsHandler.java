package com.buttermove.demo.handler;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.dto.GetAlgorithmConfigsResponseDto;
import com.buttermove.demo.dto.GetStateConfigsResponseDto;
import com.buttermove.demo.validator.AlgorithmByNameRequestValidator;
import com.buttermove.demo.validator.StatesByCodeRequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class ConfigsHandler extends BaseHandler {

  private final StatesConfigurationProperties statesConfigurationProperties;
  private final PriceAlgorithmConfigurationProperties algorithmConfigurationProperties;

  private final StatesByCodeRequestValidator statesByCodeRequestValidator;
  private final AlgorithmByNameRequestValidator algorithmByNameRequestValidator;

  public Mono<ServerResponse> getStatesConfigs(final ServerRequest serverRequest) {
    logIncomingRequest(serverRequest);

    return Mono.just(new GetStateConfigsResponseDto(statesConfigurationProperties.getStates()))
        .flatMap(response -> ServerResponse.ok().bodyValue(response));
  }

  public Mono<ServerResponse> getStateByCodeConfigs(final ServerRequest serverRequest) {
    logIncomingRequest(serverRequest);

    return Mono.just(serverRequest)
        .doOnNext(r -> validateRestPath(r, statesByCodeRequestValidator))
        .flatMap(
            r -> {
              final String stateCode = serverRequest.pathVariable(STATE_PATH_PARAM_KEY);
              return ServerResponse.ok()
                  .bodyValue(statesConfigurationProperties.getState(stateCode));
            });
  }

  public Mono<ServerResponse> getAlgorithmsConfigs(final ServerRequest serverRequest) {
    logIncomingRequest(serverRequest);

    return Mono.just(
            new GetAlgorithmConfigsResponseDto(algorithmConfigurationProperties.getAlgorithms()))
        .flatMap(response -> ServerResponse.ok().bodyValue(response));
  }

  public Mono<ServerResponse> getAlgorithmByNameConfigs(final ServerRequest serverRequest) {
    logIncomingRequest(serverRequest);

    return Mono.just(serverRequest)
        .doOnNext(r -> validateRestPath(r, algorithmByNameRequestValidator))
        .flatMap(
            r -> {
              final String algorithmName = serverRequest.pathVariable(ALGORITHM_PATH_PARAM_KEY);
              return ServerResponse.ok()
                  .bodyValue(algorithmConfigurationProperties.getAlgorithmConfigs(algorithmName));
            });
  }
}
