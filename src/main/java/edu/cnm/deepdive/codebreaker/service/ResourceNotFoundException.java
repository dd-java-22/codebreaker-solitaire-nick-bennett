package edu.cnm.deepdive.codebreaker.service;

import java.util.NoSuchElementException;

public class ResourceNotFoundException extends NoSuchElementException {

  ResourceNotFoundException() {
  }

  ResourceNotFoundException(String s) {
    super(s);
  }

  ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  ResourceNotFoundException(Throwable cause) {
    super(cause);
  }

}
