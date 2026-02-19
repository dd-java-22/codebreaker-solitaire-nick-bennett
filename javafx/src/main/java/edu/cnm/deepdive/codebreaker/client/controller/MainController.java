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
package edu.cnm.deepdive.codebreaker.client.controller;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.client.viewmodel.GameViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Handles UI interactions and updates for the Codebreaker game. This class is responsible for
 * managing the game's visual state, including the guess container, palette, and game state/guess
 * history. It coordinates with the {@link GameViewModel} to process user guesses and respond to
 * game state changes.
 */
public class MainController {

  private static final String POOL_KEY = "pool";
  private static final String POOL_NAMES_KEY = "pool_names";
  private static final String POOL_CLASSES_KEY = "pool_classes";
  private static final String LENGTH_KEY = "length";
  private static final String GUESS_ITEM_LAYOUT_KEY = "guess_item_layout";
  private static final String PALETTE_ITEM_LAYOUT_KEY = "palette_item_layout";
  private static final char MNEMONIC_PREFIX = '_';
  private static final Pattern PROPERTY_LIST_DELIMITER = Pattern.compile("\\s*,\\s*");

  @FXML
  private ResourceBundle resources;
  @FXML
  private ScrollPane scrollPane;
  @FXML
  private TextFlow textFlow;
  @FXML
  private Text gameState;
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
  private Map<Integer, String> codePointNames;
  private Map<Integer, String> codePointClasses;
  private URL paletteItemUrl;
  private URL guessItemUrl;
  private ToggleGroup group;

  // TODO: Add shutdown method, simply invokes viewModel.shutdown().

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

  /**
   * Processes the user's current guess and submits it to the {@link GameViewModel}. This method is
   * invoked when the user clicks the "send" button.
   */
  @FXML
  protected void submitGuess() {
    // TODO: Build guess text from codepoints in group, invoke viewModel.submitGuess(text).
  }

  private void loadGameProperties() {
    pool = resources.getString(POOL_KEY);
    length = Integer.parseInt(resources.getString(LENGTH_KEY));
    List<Integer> poolCodePoints = pool
        .codePoints()
        .boxed()
        .toList();
    List<String> poolNames = splitToList(resources.getString(POOL_NAMES_KEY));
    List<String> poolClasses = splitToList(resources.getString(POOL_CLASSES_KEY));
    codePointNames = new LinkedHashMap<>();
    codePointClasses = new LinkedHashMap<>();
    Iterator<Integer> codePointIter = poolCodePoints.iterator();
    Iterator<String> nameIter = poolNames.iterator();
    Iterator<String> classIter = poolClasses.iterator();
    while (codePointIter.hasNext() && nameIter.hasNext() && classIter.hasNext()) {
      Integer codePoint = codePointIter.next();
      codePointNames.put(codePoint, nameIter.next());
      codePointClasses.put(codePoint, classIter.next());
    }
    paletteItemUrl = getClass().getResource(resources.getString(PALETTE_ITEM_LAYOUT_KEY));
    guessItemUrl = getClass().getResource(resources.getString(GUESS_ITEM_LAYOUT_KEY));
  }

  private void connectToViewModel() {
    viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver(this::handleGame);
    viewModel.registerErrorObserver((throwable) -> { /* TODO Display or log this throwable. */ });
  }

  private void startGame() {
    viewModel.startGame(pool, length);
  }

  private List<String> splitToList(String joined) {
    return PROPERTY_LIST_DELIMITER
        .splitAsStream(joined)
        .filter(Predicate.not(String::isEmpty))
        .toList();
  }

  private void handleGame(Game game) {
    // TODO: Add logic to handle null gamee reference (e.g., after deleting current game).
    this.game = game;
    gameState.setText(game.toString()); // FIXME: Remove and replace with list view.
    buildPalette();
    buildGuess();
    updateSend();
  }

  private void buildPalette() {
    ObservableList<Node> children = guessPalette.getChildren();
    children.clear();
    codePointClasses
        .entrySet()
        .stream()
        .map(this::buildPaletteItem)
        .forEach(children::add);
//    guessPalette.setMaxWidth(Region.USE_PREF_SIZE);
  }

  private void buildGuess() {
    initializeGuess(getLastGuess());
    selectGuessItem(0);
  }

  private void updateSend() {
    boolean sendDisabled = group
        .getToggles()
        .stream()
        .anyMatch((toggle) -> ((ToggleButton) toggle).getUserData() == null);
    send.setDisable(sendDisabled);
  }

  private Labeled buildPaletteItem(Entry<Integer, String> entry) {
    try {
      Integer key = entry.getKey();
      String name = codePointNames.get(key);
      Labeled node = new FXMLLoader(paletteItemUrl, resources).load();
      node.addEventHandler(ActionEvent.ACTION, this::handlePaletteItemSelection);
      node.setTooltip(new Tooltip(name));
      node.setText(buildSingleCharacterMnemonicLabel(name));
      node.setUserData(key);
      node.getStyleClass().add(entry.getValue());
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
    styleClasses.add(codePointClasses.get(codePoint));
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
      String styleClass = codePointClasses.get(lastGuess[i]);
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
