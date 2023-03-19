package com.buttermove.demo.handler;

import com.buttermove.demo.exception.ApiRequestValidationException;
import com.buttermove.demo.exception.RestResourceValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class BaseHandler {

  void validate(Object objToValidate, Validator validator) {
    final Errors errors =
        new BeanPropertyBindingResult(objToValidate, objToValidate.getClass().getSimpleName());
    validator.validate(objToValidate, errors);
    if (errors.hasErrors()) {
      throw new ApiRequestValidationException(errors, "validation error");
    }
  }

  void validateRestPath(Object objToValidate, Validator validator) {
    final Errors errors =
        new BeanPropertyBindingResult(objToValidate, objToValidate.getClass().getSimpleName());
    validator.validate(objToValidate, errors);
    if (errors.hasErrors()) {
      throw new RestResourceValidationException(errors, "invalid REST resource error");
    }
  }

  void logIncomingRequest(ServerRequest serverRequest) {
    log.debug("REQUEST: {}", serverRequest);
  }

  <T> Mono<T> extractRequestBody(
      ServerRequest serverRequest, Class<T> bodyClass, T defaultValueIfEmpty) {
    return serverRequest.bodyToMono(bodyClass).defaultIfEmpty(bodyClass.cast(defaultValueIfEmpty));
  }
}
