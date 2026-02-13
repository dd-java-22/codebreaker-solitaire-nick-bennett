package edu.cnm.deepdive.codebreaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

  static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    stage.setTitle("Codebreaker");
    FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("layouts/main.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setScene(scene);
    stage.show();
  }

}
