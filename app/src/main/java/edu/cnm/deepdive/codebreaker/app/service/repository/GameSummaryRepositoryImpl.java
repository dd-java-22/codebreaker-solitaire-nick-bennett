package edu.cnm.deepdive.codebreaker.app.service.repository;

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
        summary = new GameSummary();
        summary.setExternalKey(game.getId());
        summary.setStarted(game.getCreated().toInstant());
        String pool = game.getPool();
        summary.setPool(pool);
        summary.setPoolSize((int) pool.codePoints().count());
        summary.setCodeLength(game.getLength());
      }
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
      if (summary.getId() == 0) {
        dao.insert(summary);
      } else {
        dao.update(summary);
      }
    });
  }

  @Override
  public CompletableFuture<Integer> remove(GameSummary summary) {
    return null;
  }

  @Override
  public CompletableFuture<Integer> removeAll(Collection<GameSummary> summaries) {
    return null;
  }

  @Override
  public LiveData<List<GameSummary>> selectInProgress() {
    return dao.selectInProgress();
  }

  @Override
  public LiveData<List<GameSummary>> selectCompleted(int poolSize, int codeLength) {
    return dao.selectCompleted(poolSize, codeLength);
  }

}
