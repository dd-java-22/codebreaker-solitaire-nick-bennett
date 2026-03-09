package edu.cnm.deepdive.codebreaker.app;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;
import edu.cnm.deepdive.codebreaker.app.repository.SummaryRepository;
import jakarta.inject.Inject;

@HiltAndroidApp
public class CodebreakerApplication extends Application {

  @Inject
  SummaryRepository summaryRepository;

  // Invoked when application loads.
  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void onTerminate() {
    summaryRepository.shutdown();
    super.onTerminate();
  }

}
