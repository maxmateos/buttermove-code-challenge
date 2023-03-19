package com.buttermove.demo.validator;

import static com.buttermove.demo.constants.PriceCalculatorConstants.ALGORITHM_PATH_PARAM_KEY;

import com.buttermove.demo.configuration.properties.PriceAlgorithmConfigurationProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
@AllArgsConstructor
public class AlgorithmByNameRequestValidator implements Validator {

  private PriceAlgorithmConfigurationProperties algorithmConfigurationProperties;

  @Override
  public boolean supports(Class<?> clazz) {
    return ServerRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {

    final ServerRequest request = (ServerRequest) target;

    final String algorithmNameParam = request.pathVariables().get(ALGORITHM_PATH_PARAM_KEY);
    if (algorithmNameParam == null
        || algorithmConfigurationProperties.getAlgorithmConfigs(algorithmNameParam) == null) {
      errors.reject(
          String.format(
              "'%s' path param is required and must exist in %s",
              ALGORITHM_PATH_PARAM_KEY, algorithmConfigurationProperties.getValidAlgorithmNames()));
    }
  }
}
