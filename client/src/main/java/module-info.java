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

/**
 * Provides a client implementation for interacting with the Codebreaker Solitaire service.
 */
module edu.cnm.deepdive.codebreaker.client {

  requires okhttp3;
  requires okhttp3.logging;
  requires retrofit2;
  requires retrofit2.converter.gson;
  requires com.google.gson;
  requires edu.cnm.deepdive.codebreaker.api;

  exports edu.cnm.deepdive.codebreaker.client.service;

}