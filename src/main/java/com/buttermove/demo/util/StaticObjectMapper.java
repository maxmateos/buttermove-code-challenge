package com.buttermove.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;

public class StaticObjectMapper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String PARSE_ERROR_MSG_PREFIX = "Couldn't parse json as ";

  static {
    OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  public static <T> T readValue(final String json, final Class<T> as) {
    try {
      return OBJECT_MAPPER.readValue(json, as);
    } catch (IOException e) {
      throw new IllegalStateException(PARSE_ERROR_MSG_PREFIX + as.getName() + ":\n" + json, e);
    }
  }

  public static String writeValueAsString(final Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsString(value);
    } catch (final JsonProcessingException e) {
      throw new IllegalStateException(PARSE_ERROR_MSG_PREFIX + value + " as json", e);
    }
  }

  public static JsonNode valueToTree(Object value) {
    return OBJECT_MAPPER.valueToTree(value);
  }
}
