package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  @Inject
  SymbolMap symbolMap;
  
  private FragmentGameBinding binding;
  private GameViewModel gameViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    gameViewModel
        .getGame()
        .observe(lifecycleOwner, (game) -> {
          // TODO: 2026-03-06 Handle updates to the game.
          //  1. Clear all children from binding.palette.
          //  2. Add a new child for every symbol in the game.getPool().
          //     a. Inflate a layout for the symbol.
          //     b. Set the symbol text (contentDescription and tooltip).
          //     c. Set the symbol drawable.
          //     d. Set the symbol drawable's tint.
          //     e. Add the symbol widget to the binding.palette children.
          binding.palette.removeAllViews();
          game.getPool()
              .codePoints()
              .mapToObj((codePoint) -> {
                // TODO: 2026-03-06 Inflate a layout and return the inflated widget.
                return (ImageButton) null;
              })
              .map((symbolWidget) -> {
                // TODO: 2026-03-06 Set the symbol text.
                return symbolWidget;
              })
              .map((symbolWidget) -> {
                // TODO: 2026-03-06 Set the symbol's drawable.
                return symbolWidget;
              })
              .map((symbolWidget) -> {
                // TODO: 2026-03-06 Set the symbol's drawable's tint.
                return symbolWidget;
              })
              .forEach(binding.palette::addView);
        });
    gameViewModel
        .getSolved()
        .observe(lifecycleOwner, (solved) -> {
          // TODO: 2026-03-06 Handle changes to the solved state of the game.
        });
    gameViewModel
        .getGuess()
        .observe(lifecycleOwner, (guess) -> {
          // TODO: 2026-03-06 Handles updates to the most recent guess.
        });
    gameViewModel
        .getError()
        .observe(lifecycleOwner, (error) -> {
          // TODO: 2026-03-06 Handle error.
        });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}
