package edu.cnm.deepdive.codebreaker.app.database;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

  @Provides
  @Singleton
  GameDatabase provideDatabase(@ApplicationContext Context context) {
    return Room.databaseBuilder(context, GameDatabase.class, GameDatabase.DATABASE_NAME)
        .build();
  }

  @Provides
  @Singleton
  GameSummaryDao provideGameSummaryDao(GameDatabase database) {
    return database.getGameSummaryDao();
  }

}
