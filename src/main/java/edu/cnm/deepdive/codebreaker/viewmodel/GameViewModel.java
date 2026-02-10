package edu.cnm.deepdive.codebreaker.viewmodel;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class GameViewModel {

  private final CodebreakerService service;
  private final List<Consumer<Game>> gameObservers;
  private final List<Consumer<Guess>> guessObservers;
  private final List<Consumer<Throwable>> errorObservers;

  private Game game;
  private Guess guess;
  private Throwable error;

  public GameViewModel() {
    service = CodebreakerService.getInstance();
    gameObservers = new LinkedList<>();
    guessObservers = new LinkedList<>();
    errorObservers = new LinkedList<>();
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

  private Throwable setError(Throwable error) {
    this.error = error;
    errorObservers
        .forEach((consumer) -> consumer.accept(error));
    return error;
  }

  public void startGame(String pool, int length) {
    Game game = new Game.Builder()
        .pool(pool)
        .length(length)
        .build();
    service
        .startGame(game)
        .thenAccept(this::setGame)
        .exceptionally(this::logError);
  }

  // TODO: 2026-02-10 Add methods to get and delete game, submit and get guess.

  public void registerGameObserver(Consumer<Game> observer) {
    gameObservers.add(observer);
  }

  // TODO: 2026-02-10 Add registration methods for guess observers and error observers.

  private Void logError(Throwable error) {
    //noinspection ThrowableNotThrown
    setError(error);
    error.printStackTrace();
    return null;
  }

}
