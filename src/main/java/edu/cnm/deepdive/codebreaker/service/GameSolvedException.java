package edu.cnm.deepdive.codebreaker.service;

public class GameSolvedException extends IllegalStateException {

  GameSolvedException() {
  }

  GameSolvedException(String s) {
    super(s);
  }

  GameSolvedException(String message, Throwable cause) {
    super(message, cause);
  }

  GameSolvedException(Throwable cause) {
    super(cause);
  }

}
