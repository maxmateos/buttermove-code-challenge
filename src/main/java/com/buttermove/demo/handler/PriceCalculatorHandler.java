package com.buttermove.demo.handler;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;
import static com.buttermove.demo.converter.PriceCalculatorConverter.PRICE_TO_PRICE_CALCULATION_RESPONSE;

import com.buttermove.demo.enumeration.EstimationModes;
import com.buttermove.demo.service.PriceCalculatorService;
import com.buttermove.demo.validator.PriceCalculatorRequestValidator;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class PriceCalculatorHandler extends BaseHandler {

  private final PriceCalculatorService priceCalculatorService;
  private final PriceCalculatorRequestValidator priceCalculatorRequestValidator;

  public Mono<ServerResponse> getPriceForRequest(final ServerRequest serverRequest) {
    logIncomingRequest(serverRequest);

    return Mono.just(serverRequest)
        .doOnNext(r -> validate(r, priceCalculatorRequestValidator))
        .flatMap(
            r -> {
              final Map<String, String> pathParams = r.pathVariables();
              final String stateCode = pathParams.get(STATE_PATH_PARAM_KEY);
              final EstimationModes estimationMode =
                  EstimationModes.byId(pathParams.get(ESTIMATION_MODE_PATH_PARAM_KEY));

              final MultiValueMap<String, String> queryParams = r.queryParams();
              final float kmDistance =
                  Float.parseFloat(queryParams.getFirst(KM_DISTANCE_PARAM_KEY));
              final float baseRate = Float.parseFloat(queryParams.getFirst(BASE_RATE_PARAM_KEY));
              return priceCalculatorService.calculatePrice(
                  stateCode, estimationMode, kmDistance, baseRate);
            })
        .map(PRICE_TO_PRICE_CALCULATION_RESPONSE::convert)
        .flatMap(calculatorResponse -> ServerResponse.ok().bodyValue(calculatorResponse));
  }
}
