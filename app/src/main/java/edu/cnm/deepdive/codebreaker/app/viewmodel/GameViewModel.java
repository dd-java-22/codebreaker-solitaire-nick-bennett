package edu.cnm.deepdive.codebreaker.app.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import jakarta.inject.Inject;

@HiltViewModel
public class GameViewModel extends ViewModel {

  private static final String TAG = GameViewModel.class.getSimpleName();

  private final CodebreakerService gameService;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Guess> guess;
  private final LiveData<Boolean> solved;
  private final MutableLiveData<Throwable> error;

  @Inject
  GameViewModel(CodebreakerService gameService) {
    this.gameService = gameService;
    game = new MutableLiveData<>();
    guess = new MutableLiveData<>();
    solved = Transformations.distinctUntilChanged(Transformations.map(game, Game::getSolved));
    error = new MutableLiveData<>();
  }

  public void startGame(String pool, int length) {
    Game game = new Game()
        .pool(pool)
        .length(length);
    gameService.startGame(game)
        .thenAccept(this.game::postValue)
        .exceptionally(this::postThrowable);
  }

  public void fetchGame(String gameId) {
    gameService
        .getGame(gameId)
        .thenAccept(this.game::postValue)
        .exceptionally(this::postThrowable);
  }

  public void deleteGame(String gameId) {
    gameService
        .deleteGame(gameId)
        .exceptionally(this::postThrowable);
  }

  public void deleteGame() {
    Game game = this.game.getValue();
    this.game.setValue(null);
    if (game != null) {
      //noinspection DataFlowIssue
      gameService
          .deleteGame(game.getId())
          .exceptionally(this::postThrowable);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  public void submitGuess(String text) {
    Guess guess = new Guess().text(text);
    Game game = this.game.getValue();
    gameService
        .submitGuess(game, guess)
        .thenApply((g) -> {
          this.guess.postValue(g);
          return g;
        })
        .thenAccept((g) -> {
          if (Boolean.TRUE.equals(g.getSolution())) {
            fetchGame(game.getId());
          } else {
            game.getGuesses().add(g);
            this.game.postValue(game);
          }
        });
  }

  public void fetchGuess(String guessId) {
    //noinspection DataFlowIssue
    gameService
        .getGuess(game.getValue().getId(), guessId)
        .thenAccept(guess::postValue)
        .exceptionally(this::postThrowable);
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Guess> getGuess() {
    return guess;
  }

  public LiveData<Boolean> getSolved() {
    return solved;
  }

  public LiveData<Throwable> getError() {
    return error;
  }

  private Void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    error.postValue(throwable);
    return null;
  }

}
