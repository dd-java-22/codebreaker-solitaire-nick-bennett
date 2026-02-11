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
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("NullableProblems")
class CodebreakerServiceImpl implements CodebreakerService {

  private static final String PROPERTIES_FILE = "service.properties";
  private static final String LOG_LEVEL_KEY = "logLevel";
  private static final String BASE_URL_KEY = "baseUrl";
  private static final int MIN_CODE_LENGTH = 1;
  private static final int MAX_CODE_LENGTH = 20;
  private static final int MIN_POOL_LENGTH = 1;
  private static final int MAX_POOL_LENGTH = 255;

  private static final Map<Integer, Supplier<Throwable>> CODES_TO_EXCEPTIONS = Map.ofEntries(
      Map.entry(400, InvalidPayloadException::new),
      Map.entry(404, ResourceNotFoundException::new),
      Map.entry(409, GameSolvedException::new),
      Map.entry(500, UnknownServiceException::new)
  );

  private final OkHttpClient client;
  private final CodebreakerApi api;

  private CodebreakerServiceImpl() {
    Properties properties = loadProperties();
    Gson gson = buildGson();
    client = buildClient(properties);
    api = buildApi(properties, gson, client);
  }

  static CodebreakerServiceImpl getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    return isValidGame(game)
        ? buildStartGameFuture(game)
        : CompletableFuture.failedFuture(new InvalidPayloadException());
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    return buildGetGameFuture(gameId);
  }

  @Override
  public CompletableFuture<Void> deleteGame(String gameId) {
    return buildDeleteGameFuture(gameId);
  }

  @Override
  public CompletableFuture<Guess> submitGuess(Game game, Guess guess) {
    return isValidGuess(game, guess)
        ? buildSubmitGuessFuture(game, guess)
        : CompletableFuture.failedFuture(new InvalidPayloadException());
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    return buildGetGuessFuture(gameId, guessId);
  }

  @Override
  public void shutdown() {
    try (ExecutorService executor = client.dispatcher().executorService()) {
      executor.shutdown();
      client.connectionPool().evictAll();
    }
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

  private static boolean isValidGame(Game game) {
    int codeLength = game.getLength();
    String pool = game.getPool();
    int poolLength = pool.length();
    return codeLength >= MIN_CODE_LENGTH
        && codeLength <= MAX_CODE_LENGTH
        && poolLength >= MIN_POOL_LENGTH
        && poolLength <= MAX_POOL_LENGTH
        && pool.codePoints()
        .allMatch((codePoint) ->
            Character.isDefined(codePoint)
                && !Character.isWhitespace(codePoint)
                && !Character.isISOControl(codePoint));
  }

  private static boolean isValidGuess(Game game, Guess guess) {
    boolean valid = true;
    if (guess.getText().length() != game.getLength()) {
      valid = false;
    } else {
      Set<Integer> poolCodePoints = game
          .getPool()
          .codePoints()
          .boxed()
          .collect(Collectors.toSet());
      valid = guess
          .getText()
          .codePoints()
          .allMatch(poolCodePoints::contains);
    }
    return valid;
  }

  private CompletableFuture<Game> buildStartGameFuture(Game game) {
    CompletableFuture<Game> future = new CompletableFuture<>();
    api.startGame(game).enqueue(new ServiceCallback<>(future));
    return future;
  }

  private CompletableFuture<Game> buildGetGameFuture(String gameId) {
    CompletableFuture<Game> future = new CompletableFuture<>();
    api.getGame(gameId).enqueue(new ServiceCallback<>(future));
    return future;
  }

  private CompletableFuture<Void> buildDeleteGameFuture(String gameId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    api.deleteGame(gameId).enqueue(new ServiceCallback<>(future));
    return future;
  }

  private CompletableFuture<Guess> buildSubmitGuessFuture(Game game, Guess guess) {
    CompletableFuture<Guess> future = new CompletableFuture<>();
    api.submitGuess(game.getId(), guess).enqueue(new ServiceCallback<>(future));
    return future;
  }

  private CompletableFuture<Guess> buildGetGuessFuture(String gameId, String guessId) {
    CompletableFuture<Guess> future = new CompletableFuture<>();
    api.getGuess(gameId, guessId).enqueue(new ServiceCallback<>(future));
    return future;
  }

  private static class ServiceCallback<T> implements Callback<T> {

    private final CompletableFuture<T> future;

    private ServiceCallback(CompletableFuture<T> future) {
      this.future = future;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
      CompletableFuture<T> future = future();
      if (response.isSuccessful()) {
        future.complete(response.body());
      } else {
        future.completeExceptionally(
            CODES_TO_EXCEPTIONS.getOrDefault(response.code(), UnknownServiceException::new).get());
      }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
      future.completeExceptionally(throwable);
    }

    protected CompletableFuture<T> future() {
      return future;
    }

  }

  private static class Holder {

    static final CodebreakerServiceImpl INSTANCE = new CodebreakerServiceImpl();

  }

}
