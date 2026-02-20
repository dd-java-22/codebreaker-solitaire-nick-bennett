package edu.cnm.deepdive.codebreaker.client.adapter;

import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.client.util.CodePointInfo;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class GuessAdapter implements Callback<ListView<Guess>, ListCell<Guess>> {

  private static final String GUESS_HISTORY_LAYOUT_KEY = "guess_history_layout";
  private static final String GUESS_CHARACTER_LAYOUT_KEY = "guess_character_layout";

  private final ResourceBundle resources;
  private final URL itemLayoutLocation;
  private final URL characterLayoutLocation;

  public GuessAdapter(ResourceBundle resources) {
    this.resources = resources;
    itemLayoutLocation = getClass().getResource(resources.getString(GUESS_HISTORY_LAYOUT_KEY));
    characterLayoutLocation =
        getClass().getResource(resources.getString(GUESS_CHARACTER_LAYOUT_KEY));
  }

  @Override
  public ListCell<Guess> call(ListView<Guess> guessListView) {
    try {
      return new GuessCell(guessListView);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class GuessCell extends ListCell<Guess> {

    private final ListView<Guess> listView;
    private final Parent root;

    @FXML
    private HBox guessContent;
    @FXML
    private HBox guessScore;
    @FXML
    private Text exactCount;
    @FXML
    private Text nearCount;

    private GuessCell(ListView<Guess> listView) throws IOException {
      this.listView = listView;
      FXMLLoader loader = new FXMLLoader(itemLayoutLocation, resources);
      loader.setController(this);
      root = loader.load();
      setText(null);
      setGraphic(null);
    }

    @Override
    protected void updateItem(Guess guess, boolean empty) {
      super.updateItem(guess, empty);
      if (empty || guess == null) {
        setGraphic(null);
      } else {
        // TODO: 2026-02-19 Manipulate nodes referenced by the @FXML-annotated fields to present our model state.
        List<Node> children = guessContent.getChildren();
        children.clear();
        CodePointInfo info = CodePointInfo.getInstance();
        guess
            .getText()
            .codePoints()
            .mapToObj((codePoint) -> {
              return buildGuessCharacterItem(codePoint, info);
            })
            .forEach(children::add);
        exactCount.setText(String.valueOf(guess.getExactMatches()));
        nearCount.setText(String.valueOf(guess.getNearMatches()));
        setGraphic(root);
      }
    }

    private Label buildGuessCharacterItem(int codePoint, CodePointInfo info) {
      try {
        FXMLLoader loader = new FXMLLoader(characterLayoutLocation, resources);
        Label label = loader.load();
        label.setUserData(codePoint);
        label.setTooltip(new Tooltip(info.getName(codePoint)));
        label.getStyleClass().add(info.getStyleClass(codePoint));
        return label;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

  }


}
