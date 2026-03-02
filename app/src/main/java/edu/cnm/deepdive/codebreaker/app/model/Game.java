package edu.cnm.deepdive.codebreaker.app.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(
    tableName = "game",
    indices = {
        @Index(value = "external_key", unique = true),
        @Index(value = {"started", "solved", "last_played"}),
        @Index(value = {"length", "guess_count"})
    }
)
public class Game {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "game_id")
  private long id;

  @NonNull
  @ColumnInfo(name = "external_key")
  private String externalKey = "";

  @NonNull
  private String pool = "";

  private int length;

  @NonNull
  private Instant started = Instant.now();

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

  @NonNull
  public String getExternalKey() {
    return externalKey;
  }

  public void setExternalKey(@NonNull String externalKey) {
    this.externalKey = externalKey;
  }

  @NonNull
  public String getPool() {
    return pool;
  }

  public void setPool(@NonNull String pool) {
    this.pool = pool;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @NonNull
  public Instant getStarted() {
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
