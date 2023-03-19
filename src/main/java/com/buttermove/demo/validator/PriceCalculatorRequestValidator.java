package com.buttermove.demo.validator;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;

import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import com.buttermove.demo.enumeration.EstimationModes;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
@AllArgsConstructor
public class PriceCalculatorRequestValidator implements Validator {

  private static final InetAddressValidator ipValidator = InetAddressValidator.getInstance();

  private StatesConfigurationProperties statesConfigurationProperties;

  @Override
  public boolean supports(Class<?> clazz) {
    return ServerRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {

    final ServerRequest request = (ServerRequest) target;

    final String ipHeader = request.headers().firstHeader(IP_CLIENT_HEADER_KEY);
    if (ipHeader == null || !ipValidator.isValid(ipHeader)) {
      errors.reject(
          String.format(
              "'%s' header is required and must be a valid IPv4 or IPv6", IP_CLIENT_HEADER_KEY));
    }

    final Map<String, String> pathParams = request.pathVariables();
    final String stateParam = pathParams.get(STATE_PATH_PARAM_KEY);
    if (stateParam == null || statesConfigurationProperties.getState(stateParam) == null) {
      errors.reject(
          String.format(
              "'%s' path param is required and must exist in %s",
              STATE_PATH_PARAM_KEY, statesConfigurationProperties.getValidStateCodes()));
    }

    final String estimationModeParam = pathParams.get(ESTIMATION_MODE_PATH_PARAM_KEY);
    if (estimationModeParam == null || EstimationModes.byId(estimationModeParam) == null) {
      errors.reject(
          String.format(
              "'%s' path param is required and must exist in %s",
              ESTIMATION_MODE_PATH_PARAM_KEY, List.of(EstimationModes.values())));
    }

    final MultiValueMap<String, String> queryParams = request.queryParams();
    final String distanceKms = queryParams.getFirst(KM_DISTANCE_PARAM_KEY);
    if (distanceKms == null || !isValidFloat(distanceKms)) {
      errors.reject(
          String.format(
              "'%s' param is required and must be a valid decimal number", KM_DISTANCE_PARAM_KEY));
    }

    final String baseRate = queryParams.getFirst(BASE_RATE_PARAM_KEY);
    if (baseRate == null || !isValidFloat(baseRate)) {
      errors.reject(
          String.format(
              "'%s' param is required and must be a valid decimal number", BASE_RATE_PARAM_KEY));
    }
  }

  private boolean isValidFloat(String stringValue) {
    try {
      Float.parseFloat(stringValue);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }
}
