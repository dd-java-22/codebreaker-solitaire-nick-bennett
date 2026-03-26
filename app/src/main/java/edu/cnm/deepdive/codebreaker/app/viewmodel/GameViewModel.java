package edu.cnm.deepdive.codebreaker.app.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.service.GameService;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@HiltViewModel
public class GameViewModel extends ViewModel {

  private static final String TAG = GameViewModel.class.getSimpleName();

  private final GameService gameService;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Guess> guess;
  private final LiveData<Boolean> solved;
  private final MutableLiveData<Throwable> error;

  private final IntSupplier codeLengthSupplier;
  private final Supplier<String> codePoolSupplier;

  @Inject
  GameViewModel(@ApplicationContext Context context, GameService gameService) {
    this.gameService = gameService;
    game = new MutableLiveData<>();
    guess = new MutableLiveData<>();
    solved = Transformations.distinctUntilChanged(Transformations.map(game, Game::getSolved));
    error = new MutableLiveData<>();

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Resources resources = context.getResources();
    codeLengthSupplier = getCodeLengthSupplier(prefs, resources);
    codePoolSupplier = getCodePoolSupplier(prefs, resources);

    startGame();
  }

  public void startGame() {
    int length = codeLengthSupplier.getAsInt();
    String pool = codePoolSupplier.get();
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

  private IntSupplier getCodeLengthSupplier(SharedPreferences prefs, Resources resources) {
    String codeLengthPrefKey = resources.getString(R.string.code_length_pref_key);
    int codeLengthPrefDefault = resources.getInteger(R.integer.code_length_pref_default);
    return () -> prefs.getInt(codeLengthPrefKey, codeLengthPrefDefault);
  }

  private Supplier<String> getCodePoolSupplier(SharedPreferences prefs, Resources resources) {
    String codePoolPrefKey = resources.getString(R.string.code_pool_pref_key);
    String[] codePoolPrefDefault = resources.getStringArray(R.array.symbols);
    Map<Integer, Integer> symbolPositions = IntStream.range(0, codePoolPrefDefault.length)
        .boxed()
        .collect(Collectors.toMap((pos) -> codePoolPrefDefault[pos].codePointAt(0), (pos) -> pos));
    Comparator<Integer> symbolComparator = Comparator.comparing(symbolPositions::get);
    return () -> prefs.getStringSet(codePoolPrefKey, Set.of(codePoolPrefDefault))
        .stream()
        .map((symbol) -> symbol.codePointAt(0))
        .sorted(symbolComparator)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private Void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    error.postValue(throwable);
    return null;
  }

}
