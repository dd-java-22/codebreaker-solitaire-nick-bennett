package edu.cnm.deepdive.codebreaker.client.service

import edu.cnm.deepdive.codebreaker.api.model.Game
import edu.cnm.deepdive.codebreaker.api.model.Guess
import java.util.concurrent.CompletableFuture

/**
 * Provides an interface for interacting with the Codebreaker game service. This service provides
 * asynchronous methods for starting, retrieving, and deleting games, as well as submitting and
 * retrieving guesses.
 */
interface CodebreakerService {
    /**
     * Starts a new game based on the properties of the specified [edu.cnm.deepdive.codebreaker.api.model.Game] object.
     *
     * @param game The [edu.cnm.deepdive.codebreaker.api.model.Game] object containing the configuration for the new game.
     * @return A [java.util.concurrent.CompletableFuture] that will complete with the started [edu.cnm.deepdive.codebreaker.api.model.Game] object.
     */
    fun startGame(game: Game): CompletableFuture<Game>

    /**
     * Retrieves the game with the specified ID.
     *
     * @param gameId The unique identifier of the game to be retrieved.
     * @return A [CompletableFuture] that will complete with the retrieved [Game] object.
     */
    fun getGame(gameId: String): CompletableFuture<Game>

    /**
     * Deletes the game with the specified ID.
     *
     * @param gameId The unique identifier of the game to be deleted.
     * @return A [CompletableFuture] that will complete when the game has been deleted.
     */
    fun deleteGame(gameId: String): CompletableFuture<Void?>

    /**
     * Submits a guess for the specified game.
     *
     * @param game  The [Game] for which the guess is being submitted.
     * @param guess The [edu.cnm.deepdive.codebreaker.api.model.Guess] object containing the guess text.
     * @return A [CompletableFuture] that will complete with the submitted [edu.cnm.deepdive.codebreaker.api.model.Guess]
     * response.
     */
    fun submitGuess(game: Game, guess: Guess): CompletableFuture<Guess>

    /**
     * Retrieves the guess with the specified ID for a given game.
     *
     * @param gameId  The unique identifier of the game.
     * @param guessId The unique identifier of the guess to be retrieved.
     * @return A [CompletableFuture] that will complete with the retrieved [Guess] object.
     */
    fun getGuess(gameId: String, guessId: String): CompletableFuture<Guess>

    /**
     * Terminates the service and stops any background processing.
     */
    fun shutdown()

    companion object {
        @JvmStatic
        val instance: CodebreakerService
            /**
             * Returns a reference to a singleton instance of the `CodebreakerService`. This method
             * follows the Singleton design pattern; that is, repeated (or concurrent) calls to this method
             * will all return the same reference.
             *
             * @return The singleton instance of the `CodebreakerService` interface.
             */
            get() = CodebreakerServiceImpl
    }
}