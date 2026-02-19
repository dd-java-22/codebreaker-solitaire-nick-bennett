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
package edu.cnm.deepdive.codebreaker.client;

import edu.cnm.deepdive.codebreaker.client.controller.MainController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Entry point for the Codebreaker JavaFX application. This class extends the {@link Application}
 * class and is responsible for initializing and displaying the primary stage of the application.
 */
public class Main extends Application {

  private static final String BUNDLE_BASE_NAME = "bundles/game";
  private static final String WINDOW_TITLE_KEY = "window_title";
  private static final String MAIN_LAYOUT_KEY = "main_layout";
  private MainController controller;

  static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME);
    stage.setTitle(bundle.getString(WINDOW_TITLE_KEY));
    URL location = getClass().getResource(bundle.getString(MAIN_LAYOUT_KEY));
    FXMLLoader fxmlLoader = new FXMLLoader(location, bundle);
    Scene scene = new Scene(fxmlLoader.load());
    controller = fxmlLoader.getController();
    stage.setScene(scene);
    stage.show();
  }

  // TODO: 2026-02-19 Override stop() and invoke shutdown.

}
