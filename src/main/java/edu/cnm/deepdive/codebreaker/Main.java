package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

  private static final String PROPERTIES_FILE = "game.properties";
  private static final String POOL_KEY = "pool";
  private static final String LENGTH_KEY = "length";
  private static final String BUNDLE_NAME = "strings";
  private static final String GUESS_PROMPT_KEY = "guessPrompt";
  private static final String WAITING_MESSAGE_KEY = "waitingMessage";
  private static final String SUCCESS_FORMAT_KEY = "successFormat";

  private ResourceBundle bundle;

  private final BlockingQueue<Game> updateQueue = new LinkedBlockingQueue<>();

  private final GameViewModel viewModel;
  private final BufferedReader reader;
  private final String guessPrompt;
  private final String waitingMessage;
  private final String successFormat;

  private Game game;

  public Main() {
    viewModel = connectToViewModel();
    reader = new BufferedReader(new InputStreamReader(System.in));
    ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
    guessPrompt = bundle.getString(GUESS_PROMPT_KEY);
    waitingMessage = bundle.getString(WAITING_MESSAGE_KEY);
    successFormat = bundle.getString(SUCCESS_FORMAT_KEY);
  }

  void main() throws IOException {
    startGame();
    boolean solved;
    do {
      solved = processGameTurn();
    } while (!solved);
    viewModel.shutdown();
  }

  private GameViewModel connectToViewModel() {
    GameViewModel viewModel = GameViewModel.getInstance();
    viewModel.registerGameObserver(updateQueue::add);
    viewModel.registerErrorObserver(throwable -> {
      System.err.println(throwable.getClass().getSimpleName());
      updateQueue.add(game); // Add current game back to queue.
    });
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

  private boolean processGameTurn() throws IOException {
    boolean solved;
    try {
      game = updateQueue.take();
      solved = Boolean.TRUE.equals(game.getSolved());
      System.out.println(game);
      if (solved) {
        //noinspection DataFlowIssue
        System.out.printf(successFormat, game.getText(), game.getGuesses().size());
      } else {
        System.out.printf(guessPrompt);
        viewModel.submitGuess(getNextGuess());
        System.out.printf(waitingMessage);
      }
    } catch (InterruptedException e) {
      updateQueue.add(game); // Add current game back to queue.
      solved = false;
    }
    return solved;
  }

  private String getNextGuess() throws IOException {
    String rawInput;
    String trimmedInput = null;
    while ((rawInput = reader.readLine()) != null) {
      trimmedInput = rawInput.strip();
      if (!trimmedInput.isEmpty()) {
        break;
      }
    }
    return trimmedInput;
  }

}
