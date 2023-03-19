package com.buttermove.demo.configuration;

import com.buttermove.demo.exception.WebApiException;
import com.buttermove.demo.util.StaticObjectMapper;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

  public GlobalExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties webProperties,
      ApplicationContext applicationContext) {
    super(errorAttributes, webProperties.getResources(), applicationContext);
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::errorResponseHandler);
  }

  private Mono<ServerResponse> errorResponseHandler(ServerRequest request) {

    final Throwable throwable = getError(request);
    logRequestError(request, throwable);

    if (throwable instanceof WebApiException) {
      return ((WebApiException) throwable).toServerResponse();
    }

    if (throwable instanceof ServerWebInputException) {
      final ServerWebInputException swie = (ServerWebInputException) throwable;
      final Throwable rootCause = swie.getRootCause();
      if (rootCause instanceof WebApiException) {
        return ((WebApiException) rootCause).toServerResponse();
      }
    }

    final Map<String, Object> errorAttributes =
        getErrorAttributes(request, ErrorAttributeOptions.defaults());

    final int status =
        Optional.ofNullable(errorAttributes.get("status"))
            .map(Integer.class::cast)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());

    log.error("unexpected error:", throwable);

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorAttributes));
  }

  private void logRequestError(ServerRequest request, Throwable throwable) {

    log.error(
        "Error processing request '{} {}, headers {} queryParams [{}]' resulted in error '[{}]'",
        request.method(),
        request.path(),
        request.headers(),
        StaticObjectMapper.writeValueAsString(request.queryParams()),
        throwable.getMessage());
  }
}
