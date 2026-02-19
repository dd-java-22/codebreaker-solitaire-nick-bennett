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
 * Defines the service and data model interfaces for the Codebreaker Solitaire API.
 */
module edu.cnm.deepdive.codebreaker.api {

  requires kotlin.stdlib;

  requires retrofit2;
  requires com.google.gson;
  requires jakarta.annotation;
  requires jakarta.validation;

  exports edu.cnm.deepdive.codebreaker.api.model;
  exports edu.cnm.deepdive.codebreaker.api.service;

  opens edu.cnm.deepdive.codebreaker.api.model to com.google.gson, jakarta.validation, jakarta.annotation;
  opens edu.cnm.deepdive.codebreaker.api.service to retrofit2;

}