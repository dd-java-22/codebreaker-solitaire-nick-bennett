package edu.cnm.deepdive.codebreaker.service;

public class InvalidPayloadException extends IllegalArgumentException {

  InvalidPayloadException() {
  }

  InvalidPayloadException(String s) {
    super(s);
  }

  InvalidPayloadException(String message, Throwable cause) {
    super(message, cause);
  }

  InvalidPayloadException(Throwable cause) {
    super(cause);
  }

}
