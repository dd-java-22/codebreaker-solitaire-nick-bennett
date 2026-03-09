package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    EdgeToEdge.enable(this);
    ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), MainActivity::adjustInsets);
    setContentView(binding.getRoot());
  }

  private static @NonNull WindowInsetsCompat adjustInsets(
      @NonNull View view, @NonNull WindowInsetsCompat insets) {
    Insets bounds = insets.getInsets(WindowInsetsCompat.Type.systemBars());
    MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
    params.setMargins(bounds.left, bounds.top, bounds.right, bounds.bottom);
    view.setLayoutParams(params);
    return WindowInsetsCompat.CONSUMED;
  }

}
