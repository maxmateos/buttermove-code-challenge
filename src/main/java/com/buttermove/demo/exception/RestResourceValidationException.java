package com.buttermove.demo.exception;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class RestResourceValidationException extends WebApiException {
  private final Errors validationErrors;

  public RestResourceValidationException(Errors errors, String message) {
    super(HttpStatus.NOT_FOUND, message);
    this.validationErrors = errors;
  }

  @Override
  public Mono<ServerResponse> toServerResponse() {
    return ServerResponse.status(getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of(RESPONSE_KEY_ERR_CODE, buildErrorCode(validationErrors)));
  }

  public Errors getValidationErrors() {
    return validationErrors;
  }

  private static Set<String> buildErrorCode(Errors errors) {

    return errors.getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getCode)
        .collect(Collectors.toSet());
  }
}
