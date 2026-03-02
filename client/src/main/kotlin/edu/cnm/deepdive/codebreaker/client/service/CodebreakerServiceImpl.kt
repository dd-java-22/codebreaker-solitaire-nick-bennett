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
package edu.cnm.deepdive.codebreaker.client.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import edu.cnm.deepdive.codebreaker.api.model.Game
import edu.cnm.deepdive.codebreaker.api.model.Guess
import edu.cnm.deepdive.codebreaker.api.service.CodebreakerApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import java.util.stream.Collectors

internal object CodebreakerServiceImpl : CodebreakerService {
    private val client: OkHttpClient
    private val api: CodebreakerApi

    init {
        val properties = loadProperties()
        val gson = buildGson()
        client = buildClient(properties)
        api = buildApi(properties, gson, client)
    }

    override fun startGame(game: Game): CompletableFuture<Game> {
        return if (isValidGame(game))
            buildStartGameFuture(game)
        else
            CompletableFuture.failedFuture(InvalidPayloadException())
    }

    override fun getGame(gameId: String): CompletableFuture<Game> {
        return buildGetGameFuture(gameId)
    }

    override fun deleteGame(gameId: String): CompletableFuture<Void?> {
        return buildDeleteGameFuture(gameId)
    }

    override fun submitGuess(game: Game, guess: Guess): CompletableFuture<Guess> {
        return if (isValidGuess(game, guess))
            buildSubmitGuessFuture(game, guess)
        else
            CompletableFuture.failedFuture(InvalidPayloadException())
    }

    override fun getGuess(gameId: String, guessId: String): CompletableFuture<Guess> {
        return buildGetGuessFuture(gameId, guessId)
    }

    override fun shutdown() {
        client.dispatcher.executorService.use { executor ->
            executor.shutdown()
            client.connectionPool.evictAll()
        }
    }

    private fun buildStartGameFuture(game: Game): CompletableFuture<Game> {
        return CompletableFuture<Game>().apply {
            api.startGame(game).enqueue(ServiceCallback(this))
        }
    }

    private fun buildGetGameFuture(gameId: String): CompletableFuture<Game> {
        return CompletableFuture<Game>().apply {
            api.getGame(gameId).enqueue(ServiceCallback(this))
        }
    }

    private fun buildDeleteGameFuture(gameId: String): CompletableFuture<Void?> {
        return CompletableFuture<Void?>().apply {
            api.deleteGame(gameId).enqueue(ServiceCallback(this))
        }
    }

    private fun buildSubmitGuessFuture(game: Game, guess: Guess): CompletableFuture<Guess> {
        return CompletableFuture<Guess>().apply {
            api.submitGuess(game.id, guess).enqueue(ServiceCallback(this))
        }
    }

    private fun buildGetGuessFuture(gameId: String, guessId: String): CompletableFuture<Guess> {
        return CompletableFuture<Guess>().apply {
            api.getGuess(gameId, guessId).enqueue(ServiceCallback(this))
        }
    }

}

private class OffsetDateTimeAdapter : TypeAdapter<OffsetDateTime?>() {
    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, offsetDateTime: OffsetDateTime?) {
        jsonWriter.jsonValue(offsetDateTime?.toString())
    }

    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): OffsetDateTime {
        return OffsetDateTime.parse(jsonReader.nextString())
    }
}

private class ServiceCallback<T>(private val future: CompletableFuture<T>) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val future = this.future
        if (response.isSuccessful) {
            future.complete(response.body())
        } else {
            future.completeExceptionally(
                CODES_TO_EXCEPTIONS.getOrDefault(
                    response.code(),
                    Supplier {
                        UnknownServiceException()
                    }
                )
                    .get()
            )
        }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
        future.completeExceptionally(throwable)
    }

}

private const val PROPERTIES_FILE = "service.properties"
private const val LOG_LEVEL_KEY = "logLevel"
private const val BASE_URL_KEY = "baseUrl"
private const val MIN_CODE_LENGTH = 1
private const val MAX_CODE_LENGTH = 20
private const val MIN_POOL_LENGTH = 1
private const val MAX_POOL_LENGTH = 255

private val CODES_TO_EXCEPTIONS: Map<Int, Supplier<Throwable>> = mapOf(
    400 to Supplier { InvalidPayloadException() },
    404 to Supplier { ResourceNotFoundException() },
    409 to Supplier { GameSolvedException() },
    500 to Supplier { UnknownServiceException() }
)

private fun loadProperties(): Properties {
    val properties = Properties()
    try {
        CodebreakerServiceImpl::class.java
            .classLoader
            .getResourceAsStream(
                PROPERTIES_FILE
            )
            .use {
                properties.load(it)
                return properties
            }
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

private fun buildGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
        .create()
}

private fun buildClient(properties: Properties): OkHttpClient {
    val interceptor: Interceptor = HttpLoggingInterceptor()
        .setLevel(
            HttpLoggingInterceptor.Level.valueOf(
                properties.getProperty(LOG_LEVEL_KEY).uppercase()
            )
        )
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
}

private fun buildApi(
    properties: Properties,
    gson: Gson,
    @Suppress("SameParameterValue") client: OkHttpClient
): CodebreakerApi {
    return Retrofit.Builder()
        .baseUrl(properties.getProperty(BASE_URL_KEY))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(CodebreakerApi::class.java)
}

private fun isValidGame(game: Game): Boolean {
    val codeLength = game.length
    val pool = game.pool
    val poolLength = pool.length
    return codeLength in MIN_CODE_LENGTH..MAX_CODE_LENGTH
            && poolLength in MIN_POOL_LENGTH..MAX_POOL_LENGTH
            && pool.codePoints()
        .allMatch { codePoint: Int -> Character.isDefined(codePoint)
                    && !Character.isWhitespace(codePoint)
                && !Character.isISOControl(codePoint)
        }
}

private fun isValidGuess(game: Game, guess: Guess): Boolean {
    var valid: Boolean
    if (guess.text.length != game.length) {
        valid = false
    } else {
        val poolCodePoints = game.pool
            .codePoints()
            .boxed()
            .collect(Collectors.toSet())
        valid = guess
            .text
            .codePoints()
            .allMatch { poolCodePoints.contains(it) }
    }
    return valid
}


