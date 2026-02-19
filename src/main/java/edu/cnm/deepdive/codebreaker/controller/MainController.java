package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class MainController {

  private static final String POOL_KEY = "pool";
  private static final String LENGTH_KEY = "length";
  private static final Pattern PROPERTY_LIST_DELIMITER = Pattern.compile("\\s*,\\s*");
  private static final String GUESS_ITEM_KEY = "guess_item_layout";
  private static final String PALETTE_ITEM_KEY = "palette_item_layout";

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
  private Map<Integer, String> codePointNames;
  private Map<Integer, String> codePointClasses;
  private URL paletteItemUrl;
  private URL guessItemUrl;
  private ToggleGroup group;

  @FXML
  private void initialize() throws IOException {
    paletteItemUrl = getItemUrl(resources.getString(PALETTE_ITEM_KEY));
    guessItemUrl = getItemUrl(resources.getString(GUESS_ITEM_KEY));

    buildCodePointMaps();
    viewModel = connectToViewModel();
    startGame();
  }

  private void buildCodePointMaps() {
    List<Integer> poolCodePoints = resources
        .getString(POOL_KEY)
        .codePoints()
        .boxed()
        .toList();
    List<String> poolNames = buildPoolMap("pool_names");
    List<String> poolClasses = buildPoolMap("pool_classes");

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
  }

  private List<String> buildPoolMap(String key) {
    return PROPERTY_LIST_DELIMITER
        .splitAsStream(resources.getString(key))
        .filter(Predicate.not(String::isEmpty))
        .toList();
  }

  @FXML
  private void submitGuess() {
    System.out.println();
//    String guessText = guessInput.getText().strip();
//    if (guessText.length() == game.getLength()) {
//      viewModel.submitGuess(guessText);
//    }
  }

  private GameViewModel connectToViewModel() {
    GameViewModel viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver(this::handleGame);
    viewModel.registerErrorObserver((throwable) -> { /* TODO Display or log this throwable. */ });
    return viewModel;
  }

  private void handleGame(Game game) {
    this.game = game;
    gameState.setText(game.toString()); // FIXME: 2026-02-18 Remove and replace with list view.
    buildPalette();
    buildGuess();
    updateSend();
  }

  private void buildGuess() {
    group = new ToggleGroup();
    initializeGuess(getLastGuess());
    selectGuessItem(group.getToggles().getFirst());
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
    ObservableList<Toggle> toggles = group.getToggles();
    ObservableList<Node> children = guessContainer.getChildren();
    children.clear();
    IntStream.range(0, game.getLength())
        .forEach((i) -> {
          try {
            ToggleButton button = new FXMLLoader(guessItemUrl, resources).load();
            String styleClass = codePointClasses.get(lastGuess[i]);
            if (styleClass != null) {
              button.getStyleClass().add(styleClass);
              button.setUserData(lastGuess[i]);
            }
            toggles.add(button);
            children.add(button);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private void selectGuessItem(Toggle toggle) {
    ToggleButton firstButton = (ToggleButton) toggle;
    group.selectToggle(firstButton);
    firstButton.requestFocus();
  }

  private void buildPalette() {
    EventHandler<ActionEvent> handler = (event) -> {
      Integer codePoint = (Integer) ((Node) event.getSource()).getUserData();
      ToggleButton button = (ToggleButton) group.getSelectedToggle();
      button.setUserData(codePoint);
      ObservableList<String> styleClasses = button.getStyleClass();
      styleClasses.subList(1, styleClasses.size()).clear();
      styleClasses.add(codePointClasses.get(codePoint));
      ObservableList<Toggle> toggles = group.getToggles();
      int position = toggles.indexOf(button);
      if (position < toggles.size() - 1) {
        selectGuessItem(toggles.get(position + 1));
      } else {
        button.requestFocus();
      }
      updateSend();
    };
    ObservableList<Node> children = guessPalette.getChildren();
    children.clear();
    codePointClasses
        .entrySet()
        .stream()
        .map((entry) -> {
          try {
            Integer key = entry.getKey();
            String name = codePointNames.get(key);
            Labeled node = new FXMLLoader(paletteItemUrl, resources).load();
            node.addEventHandler(ActionEvent.ACTION, handler);
            node.setTooltip(new Tooltip(name));
            node.setText(buildSingleCharacterMnemonicLabel(name));
            node.setUserData(key);
            node.getStyleClass().add(entry.getValue());
            return node;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .forEach(children::add);
  }

  private void updateSend() {
    boolean sendDisabled = group
        .getToggles()
        .stream()
        .anyMatch((toggle) -> ((ToggleButton) toggle).getUserData() == null);
    send.setDisable(sendDisabled);
  }

  private URL getItemUrl(String itemPath) {
    return getClass()
        .getClassLoader()
        .getResource(itemPath);
  }

  private String buildSingleCharacterMnemonicLabel(String name) {
    return IntStream.concat(
            IntStream.of('_'),
            name.codePoints().limit(1)
        )
        .boxed()
        .reduce(new StringBuilder(), StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private void startGame() throws IOException {
    String pool = resources.getString(POOL_KEY);
    int length = Integer.parseInt(resources.getString(LENGTH_KEY));
    viewModel.startGame(pool, length);
  }

}
