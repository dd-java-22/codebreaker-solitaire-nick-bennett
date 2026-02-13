package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainController {

  private static final String PROPERTIES_FILE = "game.properties";
  private static final String POOL_KEY = "pool";
  private static final String LENGTH_KEY = "length";

  @FXML
  private ScrollPane scrollPane;
  @FXML
  private TextFlow textFlow;
  @FXML
  private Text gameState;
  @FXML
  private TextField guessInput;
  @FXML
  private Button send;

  private GameViewModel viewModel;
  private Game game;

  @FXML
  private void initialize() throws IOException {
    textFlow
        .heightProperty()
        .addListener((_, _, _) -> scrollPane.setVvalue(1.0));
    viewModel = connectToViewModel();
    startGame();
  }

  @FXML
  private void submitGuess() {
    String guessText = guessInput.getText().strip();
    if (guessText.length() == game.getLength()) {
      viewModel.submitGuess(guessText);
    }
  }

  private GameViewModel connectToViewModel() {
    GameViewModel viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver((game) -> {
      this.game = game;
      gameState.setText(game.toString());
    });
    viewModel.registerErrorObserver((throwable) -> { /* TODO Display or log this throwable. */ });
    return viewModel;
  }

  private void startGame() throws IOException {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      Properties properties = new Properties();
      properties.load(input);
      String pool = properties.getProperty(POOL_KEY);
      int length = Integer.parseInt(properties.getProperty(LENGTH_KEY));
      viewModel.startGame(pool, length);
    }
  }

}
