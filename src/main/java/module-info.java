module edu.cnm.deepdive.codebreaker {

  requires kotlin.stdlib;
  requires okhttp3;
  requires okhttp3.logging;
  requires retrofit2;
  requires retrofit2.converter.gson;
  requires com.google.gson;
  requires javafx.controls;
  requires javafx.fxml;
  requires jakarta.annotation;
  requires jakarta.validation;
  requires annotations;

  exports edu.cnm.deepdive.codebreaker to javafx.graphics;

  opens edu.cnm.deepdive.codebreaker to javafx.fxml;
  opens edu.cnm.deepdive.codebreaker.controller to javafx.fxml;

  opens edu.cnm.deepdive.codebreaker.model to com.google.gson, jakarta.validation, jakarta.annotation;
  opens edu.cnm.deepdive.codebreaker.service to retrofit2;

}