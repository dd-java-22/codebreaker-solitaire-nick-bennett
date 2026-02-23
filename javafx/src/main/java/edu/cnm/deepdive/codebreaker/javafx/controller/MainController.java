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
package edu.cnm.deepdive.codebreaker.javafx.controller;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.javafx.adapter.GuessAdapter;
import edu.cnm.deepdive.codebreaker.javafx.util.CodePointInfo;
import edu.cnm.deepdive.codebreaker.javafx.util.Constants;
import edu.cnm.deepdive.codebreaker.javafx.viewmodel.GameViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.TilePane;

/**
 * Handles UI interactions and updates for the Codebreaker game. This class is responsible for
 * managing the game's visual state, including the guess container, palette, and game state/guess
 * history. It coordinates with the {@link GameViewModel} to process user guesses and respond to
 * game state changes.
 */
public class MainController implements Stoppable {

  private static final String LENGTH_KEY = "length";
  private static final String GUESS_ITEM_LAYOUT_KEY = "guess_item_layout";
  private static final String PALETTE_ITEM_LAYOUT_KEY = "palette_item_layout";
  private static final char MNEMONIC_PREFIX = '_';

  @FXML
  private ResourceBundle resources;

  @FXML
  private Button newGame;

  @FXML
  private ListView<Guess> guessHistory;

  @FXML
  private TilePane guessContainer;

  @FXML
  private Button send;

  @FXML
  private TilePane guessPalette;

  private GameViewModel viewModel;
  private Game game;
  private String pool;
  private int length;
  private URL paletteItemUrl;
  private URL guessItemUrl;
  private ToggleGroup group;

  /**
   * Initializes the controller after autowiring (to the nodes instantiated from the FXML layout by
   * the {@link FXMLLoader}) is complete. This method is called automatically by the
   * {@link FXMLLoader}. It loads game properties, connects to the viewmodel, and starts a new
   * game.
   */
  @FXML
  protected void initialize() {
    loadGameProperties();
    connectToViewModel();
    startGame();
  }

  @FXML
  protected void startGame() {
    viewModel.startGame(pool, length);
  }

  /**
   * Processes the user's current guess and submits it to the {@link GameViewModel}. This method is
   * invoked when the user clicks the "send" button.
   */
  @FXML
  protected void submitGuess() {
    String guessText = group
        .getToggles()
        .stream()
        .map((toggle) -> (ToggleButton) toggle)
        .map((button) -> (Integer) button.getUserData())
        .reduce(new StringBuilder(), StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    viewModel.submitGuess(guessText);
  }

  @Override
  public void shutdown() {
    viewModel.shutdown();
  }

  private void loadGameProperties() {
    pool = resources.getString(Constants.POOL_KEY);
    length = Integer.parseInt(resources.getString(LENGTH_KEY));
    paletteItemUrl = getClass().getResource(resources.getString(PALETTE_ITEM_LAYOUT_KEY));
    guessItemUrl = getClass().getResource(resources.getString(GUESS_ITEM_LAYOUT_KEY));
  }

  private void connectToViewModel() {
    viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver(this::handleGame);
    viewModel.registerErrorObserver((throwable) -> { /* TODO Display or log this throwable. */ });
  }

  private void handleGame(Game game) {
    // TODO: Add logic to handle null game reference (e.g., after deleting current game).
    this.game = game;
    updateGuessHistory();
    buildPalette();
    buildGuess();
    updateSend();
  }

  private void updateGuessHistory() {
    guessHistory.setCellFactory(new GuessAdapter(resources));
    guessHistory.getItems().clear();
    //noinspection DataFlowIssue
    guessHistory.getItems().addAll(game.getGuesses());
    Platform.runLater(() -> guessHistory.scrollTo(game.getGuesses().size() - 1));
  }

  private void buildPalette() {
    ObservableList<Node> children = guessPalette.getChildren();
    children.clear();
    pool
        .codePoints()
        .mapToObj(this::buildPaletteItem)
        .forEach(children::add);
//    guessPalette.setMaxWidth(Region.USE_PREF_SIZE);
  }

  private void buildGuess() {
    initializeGuess(getLastGuess());
    selectGuessItem(0);
  }

  private void updateSend() {
    boolean sendDisabled = Boolean.TRUE.equals(game.getSolved())
        || group
        .getToggles()
        .stream()
        .anyMatch((toggle) -> ((ToggleButton) toggle).getUserData() == null);
    send.setDisable(sendDisabled);
  }

  private Labeled buildPaletteItem(int codePoint) {
    try {
      CodePointInfo info = CodePointInfo.getInstance();
      String name = info.getName(codePoint);
      Labeled node = new FXMLLoader(paletteItemUrl, resources).load();
      node.addEventHandler(ActionEvent.ACTION, this::handlePaletteItemSelection);
      node.setTooltip(new Tooltip(name));
      node.setText(buildSingleCharacterMnemonicLabel(name));
      node.setUserData(codePoint);
      node.getStyleClass().add(info.getStyleClass(codePoint));
      return node;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int[] getLastGuess() {
    //noinspection DataFlowIssue
    return game.getGuesses().isEmpty()
        ? new int[game.getLength()]
        : game
            .getGuesses()
            .getLast()
            .getText()
            .codePoints()
            .toArray();
  }

  private void initializeGuess(int[] lastGuess) {
    group = new ToggleGroup();
    ObservableList<Toggle> toggles = group.getToggles();
    ObservableList<Node> children = guessContainer.getChildren();
    children.clear();
    IntStream.range(0, game.getLength())
        .forEach((i) -> {
          ToggleButton button = buildGuessItem(lastGuess, i);
          toggles.add(button);
          children.add(button);
        });
  }

  private void selectGuessItem(int position) {
    ToggleButton firstButton = (ToggleButton) group.getToggles().get(position);
    group.selectToggle(firstButton);
    firstButton.requestFocus();
  }

  private void handlePaletteItemSelection(ActionEvent event) {
    Integer codePoint = (Integer) ((Node) event.getSource()).getUserData();
    ToggleButton button = (ToggleButton) group.getSelectedToggle();
    button.setUserData(codePoint);
    ObservableList<String> styleClasses = button.getStyleClass();
    styleClasses.subList(1, styleClasses.size()).clear();
    styleClasses.add(CodePointInfo.getInstance().getStyleClass(codePoint));
    ObservableList<Toggle> toggles = group.getToggles();
    int position = toggles.indexOf(button);
    if (position < toggles.size() - 1) {
      selectGuessItem(position + 1);
    } else {
      button.requestFocus();
    }
    updateSend();
  }

  // TODO: Consider modifying method to control inclusion of mnemonic prefix via boolean parameter.
  private String buildSingleCharacterMnemonicLabel(String name) {
    int[] mnemonicCodePoints = IntStream.concat(
            IntStream.of(MNEMONIC_PREFIX),
            name.codePoints().limit(1)
        )
        .toArray();
    return new String(mnemonicCodePoints, 0, mnemonicCodePoints.length);
  }

  private ToggleButton buildGuessItem(int[] lastGuess, int i) {
    try {
      ToggleButton button = new FXMLLoader(guessItemUrl, resources).load();
      String styleClass = CodePointInfo.getInstance().getStyleClass(lastGuess[i]);
      if (styleClass != null) {
        button.getStyleClass().add(styleClass);
        button.setUserData(lastGuess[i]);
        // TODO: Set text of button to first character (without mnemonic) of name for the code point.
      }
      return button;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
