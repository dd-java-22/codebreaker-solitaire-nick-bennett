package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.ActivityMainBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  @Inject
  SymbolMap symbolMap;
  private ActivityMainBinding binding;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    GameViewModel viewModel = new ViewModelProvider(this).get(GameViewModel.class);
    viewModel
        .getGame()
        .observe(this, (game) -> binding.response.setText(game.toString()));
    binding.test.setOnClickListener((v) -> viewModel.startGame("ABCDEF", 6));
  }

}
