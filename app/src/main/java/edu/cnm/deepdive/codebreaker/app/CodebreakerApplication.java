package edu.cnm.deepdive.codebreaker.app;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CodebreakerApplication extends Application {

  // Invoked when application loads.
  @Override
  public void onCreate() {
    super.onCreate();
    // TODO: 2026-03-09 Perform any necessary app-level configuration.
  }

  @Override
  public void onTerminate() {
    // TODO: 2026-03-09 Shut down any service singletons that need it.
    super.onTerminate();
  }

}
