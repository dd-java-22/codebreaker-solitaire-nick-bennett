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

  private final CodebreakerService service;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Guess> guess;
  private final LiveData<Boolean> solved;
  private final MutableLiveData<Throwable> error;

  @Inject
  GameViewModel(CodebreakerService service) {
    this.service = service;
    game = new MutableLiveData<>();
    guess = new MutableLiveData<>();
    solved = Transformations.map(game, Game::getSolved);
    error = new MutableLiveData<>();
  }

  public void startGame(String pool, int length) {
    Game game = new Game()
        .pool(pool)
        .length(length);
    service.startGame(game)
        .thenAccept(this.game::postValue)
        .exceptionally(this::postThrowable);
  }

  public void getGame(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void deleteGame(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void deleteGame() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void submitGuess(String text) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public void getGuess(String guessId) {
    throw new UnsupportedOperationException("Not yet implemented");
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
