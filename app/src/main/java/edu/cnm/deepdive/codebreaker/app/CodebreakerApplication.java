package edu.cnm.deepdive.codebreaker.app;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CodebreakerApplication extends Application {

  // Invoked when application loads.
  @Override
  public void onCreate() {
    super.onCreate();
  }

}
