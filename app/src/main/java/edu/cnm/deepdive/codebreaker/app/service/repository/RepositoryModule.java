package edu.cnm.deepdive.codebreaker.app.service.repository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public interface RepositoryModule {

  @Binds
  GameSummaryRepository bindGameSummaryRepository(GameSummaryRepositoryImpl implementation);
  
}
