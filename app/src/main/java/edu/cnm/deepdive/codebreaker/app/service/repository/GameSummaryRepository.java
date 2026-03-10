package edu.cnm.deepdive.codebreaker.app.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameSummaryRepository {

  CompletableFuture<Void> summarize(Game game);

  CompletableFuture<Integer> remove(GameSummary summary);

  CompletableFuture<Integer> removeAll(Collection<GameSummary> summaries);

  LiveData<List<GameSummary>> selectInProgress();

  LiveData<List<GameSummary>> selectCompleted(int poolSize, int codeLength);

}
