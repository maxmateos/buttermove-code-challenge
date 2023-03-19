package com.buttermove.demo.configuration;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.buttermove.demo.handler.ConfigsHandler;
import com.buttermove.demo.handler.PriceCalculatorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EndpointConfiguration {

  private static final String BASE_PATH_V1 = "/v1/";
  private static final String PRICE_CALCULATOR_RESOURCE_PATH =
      String.format(
          "price-calculator/states/{%s}/estimation-modes/{%s}/calculate",
          STATE_PATH_PARAM_KEY, ESTIMATION_MODE_PATH_PARAM_KEY);

  private static final String STATES_CONFIG_RESOURCE_PATH = "configs/states";
  private static final String STATE_BY_CODE_CONFIG_RESOURCE_PATH =
      String.format(STATES_CONFIG_RESOURCE_PATH + "/{%s}", STATE_PATH_PARAM_KEY);

  private static final String ALGORITHMS_CONFIG_RESOURCE_PATH = "configs/algorithms";
  private static final String ALGORITHM_BY_NAME_CONFIG_RESOURCE_PATH =
      String.format(ALGORITHMS_CONFIG_RESOURCE_PATH + "/{%s}", ALGORITHM_PATH_PARAM_KEY);

  @Bean
  public RouterFunction<ServerResponse> routes(
      final PriceCalculatorHandler priceCalculatorHandler, final ConfigsHandler configsHandler) {

    return nest(
        path(BASE_PATH_V1),
        route(
                GET(PRICE_CALCULATOR_RESOURCE_PATH).and(accept(MediaType.APPLICATION_JSON)),
                priceCalculatorHandler::getPriceForRequest)
            .andRoute(
                GET(STATES_CONFIG_RESOURCE_PATH).and(accept(MediaType.APPLICATION_JSON)),
                configsHandler::getStatesConfigs)
            .andRoute(
                GET(STATE_BY_CODE_CONFIG_RESOURCE_PATH).and(accept(MediaType.APPLICATION_JSON)),
                configsHandler::getStateByCodeConfigs)
            .andRoute(
                GET(ALGORITHMS_CONFIG_RESOURCE_PATH).and(accept(MediaType.APPLICATION_JSON)),
                configsHandler::getAlgorithmsConfigs)
            .andRoute(
                GET(ALGORITHM_BY_NAME_CONFIG_RESOURCE_PATH).and(accept(MediaType.APPLICATION_JSON)),
                configsHandler::getAlgorithmByNameConfigs));
  }
}
