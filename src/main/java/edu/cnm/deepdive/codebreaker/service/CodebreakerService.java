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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

enum CodebreakerService implements AbstractCodebreakerService {

  INSTANCE; // public static final CodebreakerService INSTANCE = new CodebreakerService();

  private static final String PROPERTIES_FILE = "service.properties";
  private static final String LOG_LEVEL_KEY = "logLevel";
  private static final String BASE_URL_KEY = "baseUrl";

  private final CodebreakerApi api;

  CodebreakerService() {
    // TODO: 2026-02-09 DO initalization of Gson, Retrofit, and CodebreakerApi.
    Properties properties = loadProperties();
    Gson gson = buildGson();
    OkHttpClient client = buildClient(properties);
    api = buildApi(properties, gson, client);
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CompletableFuture<Void> delete(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CompletableFuture<Guess> submitGuess(String gameId, Guess guess) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    throw new UnsupportedOperationException("Not yet implemented.");
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
        CodebreakerService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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

}
