package edu.cnm.deepdive.codebreaker.app.service.database;
 
import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.codebreaker.app.service.dao.GameSummaryDao;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

  @Provides
  @Singleton
  CodebreakerDatabase provideDatabase(@ApplicationContext Context context) {
    return Room.databaseBuilder(
        context, CodebreakerDatabase.class, CodebreakerDatabase.DATABASE_NAME)
        .build();
  }
  
  @Provides
  @Singleton
  GameSummaryDao provideGameSummaryDao(CodebreakerDatabase database) {
    return database.getGameSummaryDao();
  }
  
}
