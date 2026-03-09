package edu.cnm.deepdive.codebreaker.app.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameSummaryService {

  CompletableFuture<GameSummary> summarize(Game game);

  CompletableFuture<Integer> remove(Game game);

  CompletableFuture<Integer> remove(GameSummary summary);

  CompletableFuture<Integer> remove(Collection<GameSummary> summaries);

  LiveData<List<GameSummary>> get(boolean solved, int poolSize, int codeLength);

  LiveData<List<GameSummary>> get(boolean solved);

  void shutdown();

}
