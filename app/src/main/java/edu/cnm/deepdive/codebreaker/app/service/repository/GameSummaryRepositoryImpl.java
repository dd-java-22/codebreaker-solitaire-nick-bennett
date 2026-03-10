package edu.cnm.deepdive.codebreaker.app.service.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import edu.cnm.deepdive.codebreaker.app.service.dao.GameSummaryDao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class GameSummaryRepositoryImpl implements GameSummaryRepository {

  private final GameSummaryDao dao;

  @Inject
  GameSummaryRepositoryImpl(GameSummaryDao dao) {
    this.dao = dao;
  }

  @Override
  public CompletableFuture<Void> summarize(Game game) {
    return CompletableFuture.runAsync(() -> {
      GameSummary summary = dao.selectByExternalKey(game.getId());
      if (summary == null) {
        summary = createSummary(game);
      }
      updateSummary(game, summary);
      saveSummary(summary);
    });
  }

  @Override
  public CompletableFuture<GameSummary> getByExternalKey(String externalKey) {
    return CompletableFuture.supplyAsync(() -> dao.selectByExternalKey(externalKey));
  }

  @Override
  public CompletableFuture<Integer> remove(GameSummary summary) {
    return CompletableFuture.supplyAsync(() -> dao.delete(summary));
  }

  @Override
  public CompletableFuture<Integer> removeAll(Collection<GameSummary> summaries) {
    return CompletableFuture.supplyAsync(() -> dao.deleteAll(summaries));
  }

  @Override
  public LiveData<List<GameSummary>> selectInProgress() {
    return dao.selectInProgress();
  }

  @Override
  public LiveData<List<GameSummary>> selectCompleted(int poolSize, int codeLength) {
    return dao.selectCompleted(poolSize, codeLength);
  }

  @SuppressWarnings("DataFlowIssue")
  private static @NonNull GameSummary createSummary(Game game) {
    GameSummary summary;
    summary = new GameSummary();
    summary.setExternalKey(game.getId());
    summary.setStarted(game.getCreated().toInstant());
    String pool = game.getPool();
    summary.setPool(pool);
    summary.setPoolSize((int) pool.codePoints().count());
    summary.setCodeLength(game.getLength());
    return summary;
  }

  @SuppressWarnings("DataFlowIssue")
  private static void updateSummary(Game game, GameSummary summary) {
    summary.setSolved(Boolean.TRUE.equals(game.getSolved()));
    List<Guess> guesses = game.getGuesses();
    int size = guesses.size();
    summary.setGuessCount(size);
    if (size > 0) {
      Guess lastGuess = guesses.get(size - 1);
      summary.setLastPlayed(lastGuess.getCreated().toInstant());
      summary.setExactMatches(lastGuess.getExactMatches());
      summary.setNearMatches(lastGuess.getNearMatches());
    } else {
      summary.setLastPlayed(null);
      summary.setExactMatches(0);
      summary.setNearMatches(0);
    }
  }

  private void saveSummary(GameSummary summary) {
    if (summary.getId() == 0) {
      dao.insert(summary);
    } else {
      dao.update(summary);
    }
  }

}
