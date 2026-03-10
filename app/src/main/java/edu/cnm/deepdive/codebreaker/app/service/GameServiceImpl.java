package edu.cnm.deepdive.codebreaker.app.service;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.service.repository.GameSummaryRepository;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class GameServiceImpl implements GameService {

  private final CodebreakerService service;
  private final GameSummaryRepository repository;

  @Inject
  GameServiceImpl(CodebreakerService service, GameSummaryRepository repository) {
    this.service = service;
    this.repository = repository;
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    return service
        .startGame(game)
        .thenCompose((startedGame) -> repository.summarize(startedGame)
            .thenApply((ignored) -> startedGame));
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    return service
        .getGame(gameId)
        .thenCompose((retrievedGame) -> repository.summarize(retrievedGame)
            .thenApply((ignored) -> retrievedGame));
  }

  @Override
  public CompletableFuture<Void> deleteGame(String gameId) {
    return service
        .deleteGame(gameId)
        .thenCompose((ignored) -> repository.getByExternalKey(gameId))
        .thenCompose((summary) ->
            (summary != null) ? repository.remove(summary) : CompletableFuture.completedFuture(null))
        .thenApply((ignored) -> null);
  }

  @Override
  public CompletableFuture<Guess> submitGuess(Game game, Guess guess) {
    return service
        .submitGuess(game, guess)
        .thenCompose((processedGuess) -> updateSummaryForGuess(game, processedGuess));
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    return service.getGuess(gameId, guessId);
  }

  private CompletableFuture<Guess> updateSummaryForGuess(Game game, Guess processedGuess) {
    if (Boolean.TRUE.equals(processedGuess.getSolution())) {
      return CompletableFuture.completedFuture(processedGuess);
    } else {
      game.getGuesses().add(processedGuess);
      return repository
          .summarize(game)
          .thenApply((ignored) -> processedGuess);
    }
  }

}
