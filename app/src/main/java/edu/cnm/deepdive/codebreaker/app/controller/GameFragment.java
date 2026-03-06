package edu.cnm.deepdive.codebreaker.app.controller;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap.SymbolAttributes;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;
import java.util.stream.IntStream;

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
        .observe(lifecycleOwner, this::handleGame);
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
    gameViewModel.startGame("ROYGBIV", 4);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void handleGame(Game game) {
    buildPalette(game);
    buildGuessControls(game);
  }

  private void buildPalette(Game game) {
    binding.palette.removeAllViews();
    game.getPool()
        .codePoints()
        .mapToObj(this::buildPaletteButton)
        .forEach(binding.palette::addView);
  }

  @SuppressWarnings("NewApi")
  private void buildGuessControls(Game game) {
    //noinspection DataFlowIssue
    int[] previousGuess = game.getGuesses().isEmpty()
        ? new int[game.getLength()]
        : game
            .getGuesses()
            .getLast()
            .getText()
            .codePoints()
            .toArray();
    binding.guessControls.removeAllViews();
    IntStream.of(previousGuess)
        .mapToObj(this::buildGuessButton)
        .forEach(binding.guessControls::addView);
    if (binding.guessControls.getChildCount() > 0) {
      ((RadioButton) binding.guessControls.getChildAt(0)).setChecked(true);
    }
  }

  private @NonNull ImageButton buildPaletteButton(int codePoint) {
    ImageButton paletteButton = (ImageButton) getLayoutInflater()
        .inflate(R.layout.button_palette, binding.palette, false);
    SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
    paletteButton.setContentDescription(attributes.getName());
    paletteButton.setTooltipText(attributes.getName());
    paletteButton.setImageDrawable(attributes.getDrawable());
    paletteButton.setImageTintList(ColorStateList.valueOf(attributes.getColor()));
    paletteButton.setOnClickListener((v) -> setSymbolOnChecked(codePoint));
    return paletteButton;
  }

  private @NonNull CompoundButton buildGuessButton(int codePoint) {
    CompoundButton paletteControl = (CompoundButton) getLayoutInflater()
        .inflate(R.layout.button_guess, binding.palette, false);
    if (symbolMap.hasSymbol(codePoint)) {
      SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
      String name = attributes.getName();
      paletteControl.setContentDescription(name);
      paletteControl.setTooltipText(name);
    } else {
      String noSymbolName = getString(R.string.no_symbol);
      paletteControl.setContentDescription(noSymbolName);
      paletteControl.setTooltipText(noSymbolName);
    }
    paletteControl.setBackground(buildCompoundButtonBackground(codePoint));
    paletteControl.setTag(codePoint);
    return paletteControl;
  }

  private void setSymbolOnChecked(int codePoint) {
    int selectedGuessWidgetId = binding.guessControls.getCheckedRadioButtonId();
    if (selectedGuessWidgetId != -1) {
      RadioButton guessButton = binding.guessControls.findViewById(selectedGuessWidgetId);
      guessButton.setBackground(buildCompoundButtonBackground(codePoint));
      int nextPosition = binding.guessControls.indexOfChild(guessButton) + 1;
      if (nextPosition < binding.guessControls.getChildCount()) {
        RadioButton nextButton = ((RadioButton) binding.guessControls.getChildAt(nextPosition));
        binding.guessControls.check(nextButton.getId());
      }
    }
  }

  @SuppressWarnings("DataFlowIssue")
  private StateListDrawable buildCompoundButtonBackground(int codePoint) {
    LayerDrawable checkedState = (LayerDrawable) ResourcesCompat.getDrawable(
        getResources(), R.drawable.guess_symbol_selected, null);
    LayerDrawable uncheckedState = (LayerDrawable) ResourcesCompat.getDrawable(
        getResources(), R.drawable.guess_symbol_unselected, null);
    if (symbolMap.hasSymbol(codePoint)) {
      SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
      Drawable drawable = attributes.getDrawable();
      drawable.setTint(attributes.getColor());
      checkedState.setDrawableByLayerId(R.id.symbol_layer, drawable);
      uncheckedState.setDrawableByLayerId(R.id.symbol_layer, drawable);
    } else {
      checkedState.setDrawableByLayerId(R.id.symbol_layer, null);
      uncheckedState.setDrawableByLayerId(R.id.symbol_layer, null);
    }
    StateListDrawable stateListDrawable = new StateListDrawable();
    stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedState);
    stateListDrawable.addState(new int[]{}, uncheckedState);
    return stateListDrawable;
  }

}
