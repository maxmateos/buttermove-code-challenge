package com.buttermove.demo.exception;

public class InvalidAlgorithmConfigException extends RuntimeException {

  public InvalidAlgorithmConfigException() {}

  public InvalidAlgorithmConfigException(String message) {
    super(message);
  }

  public InvalidAlgorithmConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidAlgorithmConfigException(Throwable cause) {
    super(cause);
  }

  public InvalidAlgorithmConfigException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
