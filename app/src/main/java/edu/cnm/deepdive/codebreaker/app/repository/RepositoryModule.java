package edu.cnm.deepdive.codebreaker.app.repository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public interface RepositoryModule {

  @SuppressWarnings("ClassEscapesDefinedScope")
  @Binds
  SummaryRepository bindSummaryService(SummaryRepositoryImpl implementation);

}
