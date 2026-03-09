package edu.cnm.deepdive.codebreaker.app.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(
    tableName = "game_summary",
    indices = {
        @Index(value = "external_key", unique = true),
        @Index({"solved", "started", "last_played"}),
        @Index({"solved", "pool_size", "code_length", "guess_count", "last_played", "started"})
    }
)
public class GameSummary {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "game_summary_id")
  private long id;

  @ColumnInfo(name = "external_key")
  private @NonNull String externalKey = "";

  private @NonNull String pool = "";

  @ColumnInfo(name = "pool_size")
  private int poolSize;

  @ColumnInfo(name = "code_length")
  private int codeLength;

  private @NonNull Instant started = Instant.now();

  @ColumnInfo(name = "guess_count")
  private int guessCount;

  private boolean solved;

  @ColumnInfo(name = "last_played")
  private Instant lastPlayed;

  @ColumnInfo(name = "exact_matches")
  private int exactMatches;

  @ColumnInfo(name = "near_matches")
  private int nearMatches;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public @NonNull String getExternalKey() {
    return externalKey;
  }

  public void setExternalKey(@NonNull String externalKey) {
    this.externalKey = externalKey;
  }

  public @NonNull String getPool() {
    return pool;
  }

  public void setPool(@NonNull String pool) {
    this.pool = pool;
  }

  public int getPoolSize() {
    return poolSize;
  }

  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
  }

  public int getCodeLength() {
    return codeLength;
  }

  public void setCodeLength(int codeLength) {
    this.codeLength = codeLength;
  }

  public @NonNull Instant getStarted() {
    return started;
  }

  public void setStarted(@NonNull Instant started) {
    this.started = started;
  }

  public int getGuessCount() {
    return guessCount;
  }

  public void setGuessCount(int guessCount) {
    this.guessCount = guessCount;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }

  public Instant getLastPlayed() {
    return lastPlayed;
  }

  public void setLastPlayed(Instant lastPlayed) {
    this.lastPlayed = lastPlayed;
  }

  public int getExactMatches() {
    return exactMatches;
  }

  public void setExactMatches(int exactMatches) {
    this.exactMatches = exactMatches;
  }

  public int getNearMatches() {
    return nearMatches;
  }

  public void setNearMatches(int nearMatches) {
    this.nearMatches = nearMatches;
  }

}
