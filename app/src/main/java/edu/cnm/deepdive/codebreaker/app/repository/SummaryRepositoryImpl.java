package edu.cnm.deepdive.codebreaker.app.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import jakarta.inject.Singleton;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.database.GameSummaryDao;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
class SummaryRepositoryImpl implements SummaryRepository {

  private final GameSummaryDao dao;
  private final ExecutorService executor;

  @Inject
  SummaryRepositoryImpl(GameSummaryDao dao) {
    this.dao = dao;
    executor = Executors.newSingleThreadExecutor();
  }

  @Override
  public CompletableFuture<GameSummary> summarize(Game game) {
    return CompletableFuture.supplyAsync(() -> {
      GameSummary summary = dao.selectByExternalKey(game.getId());
      if (summary == null) {
        summary = createSummary(game);
      }
      updateSummary(summary, game);
      addOrInsert(summary);
      return summary;
    }, executor);
  }

  @Override
  public CompletableFuture<Integer> remove(Game game) {
    return CompletableFuture.supplyAsync(() -> dao.delete(game.getId()), executor);
  }

  @Override
  public CompletableFuture<Integer> remove(GameSummary summary) {
    return CompletableFuture.supplyAsync(() -> dao.delete(summary), executor);
  }

  @Override
  public CompletableFuture<Integer> remove(Collection<GameSummary> summaries) {
    return CompletableFuture.supplyAsync(() -> dao.delete(summaries), executor);
  }

  @Override
  public LiveData<List<GameSummary>> get(boolean solved, int poolSize, int codeLength) {
    return dao.selectSummaries(solved, poolSize, codeLength);
  }

  @Override
  public LiveData<List<GameSummary>> get(boolean solved) {
    return dao.selectSummaries(solved);
  }

  @Override
  public void shutdown() {
    executor.shutdown();
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
  private static void updateSummary(GameSummary summary, Game game) {
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

  private void addOrInsert(GameSummary summary) {
    if (summary.getId() == 0) {
      long id = dao.insert(summary);
      summary.setId(id);
    } else {
      dao.update(summary);
    }
  }

}
