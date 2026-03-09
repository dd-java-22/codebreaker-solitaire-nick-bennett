package edu.cnm.deepdive.codebreaker.app.service;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.codebreaker.client.service.CodebreakerService;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class ServiceModule {

  @Provides
  @Singleton
  CodebreakerService provideCodebreakerService() {
    return CodebreakerService.getInstance();
  }

}
