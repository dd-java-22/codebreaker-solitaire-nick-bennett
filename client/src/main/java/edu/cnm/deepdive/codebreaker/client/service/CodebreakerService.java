/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.codebreaker.client.service;

import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 * Provides an interface for interacting with the Codebreaker game service. This service provides
 * asynchronous methods for starting, retrieving, and deleting games, as well as submitting and
 * retrieving guesses.
 */
public interface CodebreakerService {

  /**
   * Returns a reference to a singleton instance of the {@code CodebreakerService}. This method
   * follows the Singleton design pattern; that is, repeated (or concurrent) calls to this method
   * will all return the same reference.
   *
   * @return The singleton instance of the {@code CodebreakerService} interface.
   */
  static CodebreakerService getInstance() {
    return CodebreakerServiceImpl.getInstance();
  }

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
   * @return A {@link CompletableFuture} that will complete with the retrieved {@link Guess} object.
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

  /**
   * Terminates the service and stops any background processing.
   */
  void shutdown();

}
