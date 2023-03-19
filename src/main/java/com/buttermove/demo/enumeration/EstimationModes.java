package com.buttermove.demo.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum EstimationModes {
  NORMAL("normal"),
  PREMIUM("premium");

  private final String key;
  private static final Map<String, EstimationModes> BY_KEY = new HashMap<>();

  static {
    for (final EstimationModes estimationModes : values()) {
      BY_KEY.put(estimationModes.key.toLowerCase(), estimationModes);
    }
  }

  EstimationModes(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public static EstimationModes byId(final String key) {
    return BY_KEY.get(key.toLowerCase());
  }
}
