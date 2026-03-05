/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.codebreaker.client.service;

/**
 * Exception thrown when an attempt is made to submit a guess for a game that has already been
 * solved. This is an extension of {@link IllegalStateException} that corresponds to a 409 Conflict
 * response from the Codebreaker web service.
 */
public class GameSolvedException extends IllegalStateException {

  /**
   * Initializes this instance with no detail message.
   */
  GameSolvedException() {
  }

  /**
   * Initializes this instance with the specified detail message.
   *
   * @param s The detail message.
   */
  GameSolvedException(String s) {
    super(s);
  }

  /**
   * Initializes this instance with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  GameSolvedException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Initializes this instance with the specified cause.
   *
   * @param cause The cause of the exception.
   */
  GameSolvedException(Throwable cause) {
    super(cause);
  }

}
