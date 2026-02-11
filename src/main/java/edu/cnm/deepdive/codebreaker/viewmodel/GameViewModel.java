package edu.cnm.deepdive.codebreaker.viewmodel;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "CallToPrintStackTrace", "unused"})
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

  public static GameViewModel getInstance() {
    return Holder.INSTANCE;
  }

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

  public void getGame(String gameId) {
    service
        .getGame(gameId)
        .thenApply((game) -> setGame(game).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  public void deleteGame(String gameId) {
    service
        .deleteGame(gameId)
        .exceptionally(this::logError);
  }

  public void deleteGame() {
    service
        .deleteGame(game.getId())
        .thenRun(() -> setGame(null))
        .exceptionally(this::logError);
  }

  public void submitGuess(String text) {
    Guess guess = new Guess()
        .text(text);
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

  public void getGuess(String guessId) {
    service
        .getGuess(game.getId(), guessId)
        .thenAccept(this::setGuess)
        .exceptionally(this::logError);
  }

  public void shutdown() {
    service.shutdown();
  }

  public void registerGameObserver(Consumer<Game> observer) {
    gameObservers.add(observer);
    if (game != null) {
      observer.accept(game);
    }
  }

  public void registerGuessObserver(Consumer<Guess> observer) {
    guessObservers.add(observer);
    if (guess != null) {
      observer.accept(guess);
    }
  }

  public void registerSolvedObserver(Consumer<Boolean> observer) {
    solvedObservers.add(observer);
    if (solved != null) {
      observer.accept(solved);
    }
  }

  public void registerErrorObserver(Consumer<Throwable> observer) {
    errorObservers.add(observer);
    if (error != null) {
      observer.accept(error);
    }
  }

  private Game setGame(Game game) {
    this.game = game;
    gameObservers
        .forEach((consumer) -> consumer.accept(game));
    return game;
  }

  private Guess setGuess(Guess guess) {
    this.guess = guess;
    guessObservers
        .forEach((consumer) -> consumer.accept(guess));
    return guess;
  }

  private Boolean setSolved(Boolean solved) {
    this.solved = solved;
    solvedObservers
        .forEach((consumer) -> consumer.accept(solved));
    return solved;
  }

  private Throwable setError(Throwable error) {
    this.error = error;
    errorObservers
        .forEach((consumer) -> consumer.accept(error));
    return error;
  }

  private Void logError(Throwable error) {
    //noinspection ThrowableNotThrown
    setError(error.getCause() != null ? error.getCause() : error);
//    this.error.printStackTrace();
    return null;
  }

  private static class Holder {

    static final GameViewModel INSTANCE = new GameViewModel();

  }

}
