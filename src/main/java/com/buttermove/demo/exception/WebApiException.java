package com.buttermove.demo.exception;

import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public abstract class WebApiException extends ResponseStatusException {

  public static final String RESPONSE_KEY_ERR_CODE = "errors";

  public WebApiException(HttpStatus httpStatus, String message) {
    super(httpStatus, message);
  }

  public Mono<ServerResponse> toServerResponse() {

    return ServerResponse.status(getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of(RESPONSE_KEY_ERR_CODE, Set.of(getReason())));
  }
}
