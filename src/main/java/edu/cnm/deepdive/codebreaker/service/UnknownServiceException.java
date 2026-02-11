package edu.cnm.deepdive.codebreaker.service;

public class UnknownServiceException extends IllegalStateException {

  UnknownServiceException() {
  }

  UnknownServiceException(String s) {
    super(s);
  }

  UnknownServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  UnknownServiceException(Throwable cause) {
    super(cause);
  }

}
