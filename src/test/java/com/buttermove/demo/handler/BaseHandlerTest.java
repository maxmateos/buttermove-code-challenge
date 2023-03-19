package com.buttermove.demo.handler;

import java.util.Collections;
import java.util.List;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

public abstract class BaseHandlerTest {

  protected <T> T extractEntity(final ServerResponse response, final Class<T> type) {

    final EntityResponse<T> entityResponse = (EntityResponse<T>) response;

    return type.cast(entityResponse.entity());
  }

  public static String serverResponseAsString(ServerResponse serverResponse) {
    MockServerWebExchange exchange =
        MockServerWebExchange.from(MockServerHttpRequest.get("/foo/foo"));

    DebugServerContext debugServerContext = new DebugServerContext();
    serverResponse.writeTo(exchange, debugServerContext).block();

    MockServerHttpResponse response = exchange.getResponse();
    return response.getBodyAsString().block().replace("data:", "");
  }

  private static class DebugServerContext implements ServerResponse.Context {

    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
      return HandlerStrategies.withDefaults().messageWriters();
    }

    @Override
    public List<ViewResolver> viewResolvers() {
      return Collections.emptyList();
    }
  }
}
