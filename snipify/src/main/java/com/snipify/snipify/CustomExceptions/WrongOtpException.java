package com.snipify.snipify.CustomExceptions;

public class WrongOtpException extends RuntimeException {
  public WrongOtpException(String message) {
    super(message);
  }
}
