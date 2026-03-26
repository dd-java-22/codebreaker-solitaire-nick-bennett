package edu.cnm.deepdive.codebreaker.app.service;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 * Provides an interface for interacting with the Codebreaker game service. This service provides
 * asynchronous methods for starting, retrieving, and deleting games, as well as submitting and
 * retrieving guesses.
 */
public interface GameService {

  /**
   * Starts a new game based on the properties of the specified {@link Game} object.
   *
   * @param game The {@link Game} object containing the configuration for the new game.
   * @return A {@link CompletableFuture} that will complete with the started {@link Game} object.
   */
  CompletableFuture<Game> startGame(Game game);

  /**
   * Retrieves the game with the specified ID.
   *
   * @param gameId The unique identifier of the game to be retrieved.
   * @return A {@link CompletableFuture} that will complete with the retrieved {@link Game} object.
   */
  CompletableFuture<Game> getGame(String gameId);

  /**
   * Deletes the game with the specified ID.
   *
   * @param gameId The unique identifier of the game to be deleted.
   * @return A {@link CompletableFuture} that will complete when the game has been deleted.
   */
  CompletableFuture<Void> deleteGame(String gameId);

  /**
   * Submits a guess for the specified game.
   *
   * @param game  The {@link Game} for which the guess is being submitted.
   * @param guess The {@link Guess} object containing the guess text.
   * @return A {@link CompletableFuture} that will complete with the submitted {@link Guess}
   * response.
   */
  CompletableFuture<Guess> submitGuess(Game game, Guess guess);

  /**
   * Retrieves the guess with the specified ID for a given game.
   *
   * @param gameId  The unique identifier of the game.
   * @param guessId The unique identifier of the guess to be retrieved.
   * @return A {@link CompletableFuture} that will complete with the retrieved {@link Guess}
   * object.
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

}
