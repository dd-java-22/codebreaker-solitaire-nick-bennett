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
 * Implements a JavaFX-based user interface for the Codebreaker Solitaire client.
 */
module edu.cnm.deepdive.codebreaker.javafx {

  requires javafx.controls;
  requires javafx.fxml;
  requires edu.cnm.deepdive.codebreaker.api;
  requires edu.cnm.deepdive.codebreaker.client;

  exports edu.cnm.deepdive.codebreaker.client to javafx.graphics;

  opens edu.cnm.deepdive.codebreaker.client to javafx.fxml;
  opens edu.cnm.deepdive.codebreaker.client.controller to javafx.fxml;
  opens edu.cnm.deepdive.codebreaker.client.adapter to javafx.fxml;

}