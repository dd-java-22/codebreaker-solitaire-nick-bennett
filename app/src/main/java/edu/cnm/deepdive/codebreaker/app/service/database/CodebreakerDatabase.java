package edu.cnm.deepdive.codebreaker.app.service.database;

import static edu.cnm.deepdive.codebreaker.app.service.database.CodebreakerDatabase.DATABASE_VERSION;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import edu.cnm.deepdive.codebreaker.app.service.dao.GameSummaryDao;
import edu.cnm.deepdive.codebreaker.app.service.database.CodebreakerDatabase.Converters;
import java.time.Instant;


@Database(entities = GameSummary.class, version = DATABASE_VERSION)
@TypeConverters(Converters.class)
public abstract class CodebreakerDatabase extends RoomDatabase {

  static final int DATABASE_VERSION = 1;
  static final String DATABASE_NAME = "codebreaker-db";

  public abstract GameSummaryDao getGameSummaryDao();

  public static class Converters {

    @TypeConverter
    public static @Nullable Long toLong(@Nullable Instant value) {
      return (value != null) ? value.toEpochMilli() : null;
    }

    @TypeConverter
    public static @Nullable Instant toInstant(@Nullable Long value) {
      return (value != null) ? Instant.ofEpochMilli(value) : null;
    }

  }

}
