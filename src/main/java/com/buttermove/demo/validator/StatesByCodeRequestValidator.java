package com.buttermove.demo.validator;

import static com.buttermove.demo.constants.PriceCalculatorConstants.*;

import com.buttermove.demo.configuration.properties.StatesConfigurationProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
@AllArgsConstructor
public class StatesByCodeRequestValidator implements Validator {

  private StatesConfigurationProperties statesConfigurationProperties;

  @Override
  public boolean supports(Class<?> clazz) {
    return ServerRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {

    final ServerRequest request = (ServerRequest) target;

    final String stateParam = request.pathVariables().get(STATE_PATH_PARAM_KEY);
    if (stateParam == null || statesConfigurationProperties.getState(stateParam) == null) {
      errors.reject(
          String.format(
              "'%s' path param is required and must exist in %s",
              STATE_PATH_PARAM_KEY, statesConfigurationProperties.getValidStateCodes()));
    }
  }
}
