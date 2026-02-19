package edu.cnm.deepdive.codebreaker.client.viewmodel;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 * Provides access to the game logic and state for the JavaFX client. This class follows the
 * Singleton design pattern and uses the {@link CodebreakerService} to communicate with the
 * Codebreaker web service. It manages game and guess data and provides a mechanism for observing
 * changes to the game state.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class GameViewModel {

  private final CodebreakerService service;
  private final List<Consumer<Game>> gameObservers;
  private final List<Consumer<Guess>> guessObservers;
  private final List<Consumer<Throwable>> errorObservers;
  private final List<Consumer<Boolean>> solvedObservers;

  private Game game;
  private Guess guess;
  private Boolean solved;
  private Throwable error;

  private GameViewModel() {
    service = CodebreakerService.getInstance();
    gameObservers = new LinkedList<>();
    guessObservers = new LinkedList<>();
    errorObservers = new LinkedList<>();
    solvedObservers = new LinkedList<>();
  }

  /**
   * Returns a reference to an instance of the class. This class follows the Singleton design
   * pattern; that is, repeated (or concurrent) calls to this method will all return the same
   * reference.
   *
   * @return The singleton instance of the {@code GameViewModel} class.
   */
  public static GameViewModel getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Starts a new game with the specified pool of characters and length of the code.
   *
   * @param pool   The string of characters from which the code will be generated.
   * @param length The length of the code to be guessed.
   */
  public void startGame(String pool, int length) {
    Game game = new Game()
        .pool(pool)
        .length(length);
    service
        .startGame(game)
        .thenApply((startedGame) -> setGame(startedGame).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Retrieves the game with the specified ID.
   *
   * @param gameId The unique identifier of the game to be retrieved.
   */
  public void getGame(String gameId) {
    service
        .getGame(gameId)
        .thenApply((game) -> setGame(game).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Deletes the game with the specified ID.
   *
   * @param gameId The unique identifier of the game to be deleted.
   */
  public void deleteGame(String gameId) {
    service
        .deleteGame(gameId)
        .exceptionally(this::logError);
  }

  /**
   * Deletes the current game being managed by this view model.
   */
  public void deleteGame() {
    service
        .deleteGame(game.getId())
        .thenRun(() -> setGame(null))
        .exceptionally(this::logError);
  }

  /**
   * Submits a guess with the specified text for the current game.
   *
   * @param text The text of the guess to be submitted.
   */
  public void submitGuess(String text) {
    Guess guess = new Guess().text(text);
    service
        .submitGuess(game, guess)
        .thenApply(this::setGuess)
        .thenAccept((guessResponse) -> {
          if (Boolean.TRUE.equals(guessResponse.getSolution())) {
            getGame(game.getId());
          } else {
            //noinspection DataFlowIssue
            game.getGuesses().add(guessResponse);
            setGame(game);
          }
        })
        .exceptionally(this::logError);
  }

  /**
   * Retrieves the guess with the specified ID for the current game.
   *
   * @param guessId The unique identifier of the guess to be retrieved.
   */
  public void getGuess(String guessId) {
    service
        .getGuess(game.getId(), guessId)
        .thenAccept(this::setGuess)
        .exceptionally(this::logError);
  }

  /**
   * Shuts down the underlying {@link CodebreakerService} and stops any background processing.
   */
  public void shutdown() {
    service.shutdown();
  }

  /**
   * Registers an observer to be notified when the current game changes.
   *
   * @param observer The consumer that will be notified with the updated game.
   */
  public void registerGameObserver(Consumer<Game> observer) {
    gameObservers.add(observer);
    if (game != null) {
      observer.accept(game);
    }
  }

  /**
   * Registers an observer to be notified when the current guess changes.
   *
   * @param observer The consumer that will be notified with the updated guess.
   */
  public void registerGuessObserver(Consumer<Guess> observer) {
    guessObservers.add(observer);
    if (guess != null) {
      observer.accept(guess);
    }
  }

  /**
   * Registers an observer to be notified when the solved status of the current game changes.
   *
   * @param observer The consumer that will be notified with the updated solved status.
   */
  public void registerSolvedObserver(Consumer<Boolean> observer) {
    solvedObservers.add(observer);
    if (solved != null) {
      observer.accept(solved);
    }
  }

  /**
   * Registers an observer to be notified when an error occurs during game or guess operations.
   *
   * @param observer The consumer that will be notified with the throwable that represents the error.
   */
  public void registerErrorObserver(Consumer<Throwable> observer) {
    errorObservers.add(observer);
    if (error != null) {
      observer.accept(error);
    }
  }

  private Game setGame(Game game) {
    this.game = game;
    Platform.runLater(() -> gameObservers
        .forEach((consumer) -> consumer.accept(game)));
    return game;
  }

  private Boolean setSolved(Boolean solved) {
    this.solved = solved;
    Platform.runLater(() -> solvedObservers
        .forEach((consumer) -> consumer.accept(solved)));
    return solved;
  }

  private Void logError(Throwable error) {
    //noinspection ThrowableNotThrown
    setError(error.getCause() != null ? error.getCause() : error);
//    this.error.printStackTrace();
    return null;
  }

  private Guess setGuess(Guess guess) {
    this.guess = guess;
    Platform.runLater(() -> guessObservers
        .forEach((consumer) -> consumer.accept(guess)));
    return guess;
  }

  private Throwable setError(Throwable error) {
    this.error = error;
    Platform.runLater(() -> errorObservers
        .forEach((consumer) -> consumer.accept(error)));
    return error;
  }

  private static class Holder {

    static final GameViewModel INSTANCE = new GameViewModel();

  }

}
