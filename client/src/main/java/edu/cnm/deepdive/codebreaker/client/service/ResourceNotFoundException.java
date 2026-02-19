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

import java.util.NoSuchElementException;

/**
 * Exception thrown when a requested resource (e.g., a game or guess) cannot be found on the
 * Codebreaker service. This corresponds to a 404 Not Found response from the Codebreaker web
 * service.
 */
public class ResourceNotFoundException extends NoSuchElementException {

  /**
   * Initializes this instance with no detail message.
   */
  ResourceNotFoundException() {
  }

  /**
   * Initializes this instance with the specified detail message.
   *
   * @param s The detail message.
   */
  ResourceNotFoundException(String s) {
    super(s);
  }

  /**
   * Initializes this instance with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Initializes this instance with the specified cause.
   *
   * @param cause The cause of the exception.
   */
  ResourceNotFoundException(Throwable cause) {
    super(cause);
  }

}
