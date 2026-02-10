package edu.cnm.deepdive.codebreaker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class CodebreakerServiceImpl implements CodebreakerService {

  private static final String PROPERTIES_FILE = "service.properties";
  private static final String LOG_LEVEL_KEY = "logLevel";
  private static final String BASE_URL_KEY = "baseUrl";

  private final CodebreakerApi api;

  private CodebreakerServiceImpl() {
    Properties properties = loadProperties();
    Gson gson = buildGson();
    OkHttpClient client = buildClient(properties);
    api = buildApi(properties, gson, client);
  }

  static CodebreakerServiceImpl getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    return isValidGame(game)
        ? buildStartGameFuture(game)
        : CompletableFuture.failedFuture(new IllegalArgumentException());
  }

  @NotNull
  private CompletableFuture<Game> buildStartGameFuture(Game game) {
    CompletableFuture<Game> future = new CompletableFuture<>();
    api.startGame(game)
        .enqueue(new StartGameCallback(future));
    return future;
  }

  private static boolean isValidGame(Game game) {
    return game.getLength() > 0 && game.getLength() < 20;
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    CompletableFuture<Game> future = new CompletableFuture<>();
    api
        .getGame(gameId)
        .enqueue(new Callback<>() {
          @Override
          public void onResponse(Call<Game> call, Response<Game> response) {
            if (response.isSuccessful()) {
              future.complete(response.body());
            } else {
              future.completeExceptionally(
                  new IllegalArgumentException("Specified game doesn't exist!"));
            }
          }

          @Override
          public void onFailure(Call<Game> call, Throwable t) {
            future.completeExceptionally(t);
          }
        });
    return future;
  }

  @Override
  public CompletableFuture<Void> delete(String gameId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    api
        .deleteGame(gameId)
        .enqueue(new Callback<>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
              future.complete(null);
            } else {
              future.completeExceptionally(new IllegalArgumentException("Specified game doesn't exist!"));
            }
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            future.completeExceptionally(t);
          }
        });
    return future;
  }

  @Override
  public CompletableFuture<Guess> submitGuess(Game game, Guess guess) {
    CompletableFuture<Guess> future;
    if (guess.getText().length() == game.getLength()) {
      future = new CompletableFuture<>();
      api.submitGuess(game.getId(), guess)
          .enqueue(new Callback<Guess>() {
            @Override
            public void onResponse(Call<Guess> call, Response<Guess> response) {
              if (response.isSuccessful()) {
                future.complete(response.body());
              } else {
                switch (response.code()) {
                  case 400 -> future.completeExceptionally(
                      new IllegalArgumentException("Invalid guess content!"));
                  case 404 -> future.completeExceptionally(
                      new IllegalArgumentException("Game not found!"));
                  default -> future.completeExceptionally(
                      new IllegalArgumentException("Unknown error!"));
                }
              }
            }

            @Override
            public void onFailure(Call<Guess> call, Throwable t) {
              future.completeExceptionally(t);
            }
          });
    } else {
      future = CompletableFuture.failedFuture(new IllegalArgumentException());
    }
    return future;
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    CompletableFuture<Guess> future = new CompletableFuture<>();
    api.getGuess(gameId, guessId)
        .enqueue(new Callback<Guess>() {
          @Override
          public void onResponse(Call<Guess> call, Response<Guess> response) {
            if (response.isSuccessful()) {
              future.complete(response.body());
            } else {
              switch (response.code()) {
                case 404 -> future.completeExceptionally(
                    new IllegalArgumentException("Game or guess not found!"));
                default -> future.completeExceptionally(
                    new IllegalArgumentException("Unknown error!"));
              }
            }
          }

          @Override
          public void onFailure(Call<Guess> call, Throwable t) {

          }
        });
    return future;
  }

  private static Gson buildGson() {
    return new GsonBuilder()
        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
        .create();
  }

  private static OkHttpClient buildClient(Properties properties) {
    Interceptor interceptor = new HttpLoggingInterceptor()
        .setLevel(Level.valueOf(properties.getProperty(LOG_LEVEL_KEY).toUpperCase()));
    return new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build();
  }

  private static CodebreakerApi buildApi(Properties properties, Gson gson, OkHttpClient client) {
    return new Retrofit.Builder()
        .baseUrl(properties.getProperty(BASE_URL_KEY))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(CodebreakerApi.class);
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();
    try (InputStream input =
        CodebreakerServiceImpl.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
      properties.load(input);
      return properties;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, OffsetDateTime offsetDateTime) throws IOException {
      jsonWriter.jsonValue(offsetDateTime != null ? offsetDateTime.toString() : null);
    }

    @Override
    public OffsetDateTime read(JsonReader jsonReader) throws IOException {
      return OffsetDateTime.parse(jsonReader.nextString());
    }

  }

  private static class Holder {

    static final CodebreakerServiceImpl INSTANCE = new CodebreakerServiceImpl();

  }

  private static class StartGameCallback implements Callback<Game> {

    private final CompletableFuture<Game> future;

    public StartGameCallback(CompletableFuture<Game> future) {
      this.future = future;
    }

    @Override
    public void onResponse(Call<Game> call, Response<Game> response) {
      if (response.isSuccessful()) {
        future.complete(response.body());
      } else {
        // TODO: 2026-02-09 Make this specific to error types.
        future.completeExceptionally(new IllegalArgumentException(response.message()));
      }
    }

    @Override
    public void onFailure(Call<Game> call, Throwable throwable) {
      future.completeExceptionally(throwable);
    }
  }
}
