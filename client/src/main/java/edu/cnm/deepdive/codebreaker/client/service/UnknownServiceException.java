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
 * Exception thrown when the Codebreaker web service returns an error that is not specifically
 * handled by other exception classes. This corresponds to a 500 Internal Server Error response from
 * the web service.
 */
public class UnknownServiceException extends IllegalStateException {

  /**
   * Initializes this instance with no detail message.
   */
  UnknownServiceException() {
  }

  /**
   * Initializes this instance with the specified detail message.
   *
   * @param s The detail message.
   */
  UnknownServiceException(String s) {
    super(s);
  }

  /**
   * Initializes this instance with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  UnknownServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Initializes this instance with the specified cause.
   *
   * @param cause The cause of the exception.
   */
  UnknownServiceException(Throwable cause) {
    super(cause);
  }

}
