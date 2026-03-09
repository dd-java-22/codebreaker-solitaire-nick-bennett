package edu.cnm.deepdive.codebreaker.app.service.database;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import edu.cnm.deepdive.codebreaker.app.service.database.GameDatabase.Converters;
import java.time.Instant;

@Database(entities = {GameSummary.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class GameDatabase extends RoomDatabase {

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
