package edu.cnm.deepdive.codebreaker.app.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.adapter.GuessesAdapter;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap.SymbolAttributes;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  private static final String TAG = GameFragment.class.getSimpleName();

  @Inject
  SymbolMap symbolMap;
  @Inject
  GuessesAdapter guessesAdapter;

  private FragmentGameBinding binding;
  private GameViewModel gameViewModel;

  @Override
  public @Nullable View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.guesses.setAdapter(guessesAdapter);
    binding.submit.setOnClickListener((v) -> submitGuess());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    gameViewModel.getGame().observe(lifecycleOwner, this::handleGame);
    gameViewModel.getSolved().observe(lifecycleOwner, this::handleSolved);
    gameViewModel.getGuess().observe(lifecycleOwner, this::handleGuess);
    gameViewModel.getError().observe(lifecycleOwner, this::handleError);
    gameViewModel.startGame("ROYGBIV", 6);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void submitGuess() {
    int[] guessCodePoints = IntStream.range(0, binding.guessControls.getChildCount())
        .mapToObj(binding.guessControls::getChildAt)
        .mapToInt((view) -> (Integer) view.getTag())
        .toArray();
    String guessText = new String(guessCodePoints, 0, guessCodePoints.length);
    gameViewModel.submitGuess(guessText);
  }

  private void handleGame(Game game) {
    updateGuessList(game);
    buildGuessControls(game, lastGuess(game));
    buildPaletteControls(game);
  }

  private void handleSolved(Boolean solved) {
    // TODO: 2026-03-09 Enable/disable buttons for solved state.
  }

  private void handleGuess(Guess guess) {
    // TODO: 2026-03-06 Handle display updates (if needed) for the most recent guess.
  }

  private void handleError(Throwable error) {
    // TODO: 2026-03-06 Display a Snackbar to the user, with message customized for the error type.
  }

  private void updateGuessList(Game game) {
    List<Guess> guesses = game.getGuesses();
    Game previousGame = (Game) binding.guesses.getTag();
    int oldSize = guessesAdapter.getItemCount();
    //noinspection DataFlowIssue
    int newSize = guesses.size();
    if (newSize < oldSize || game != previousGame) {
      guessesAdapter.clear();
      oldSize = 0;
    }
    if (newSize > oldSize) {
      guessesAdapter.addAll(guesses.subList(oldSize, newSize));
      binding.guesses.scrollToPosition(newSize - 1);
    }
    binding.guesses.setTag(game);
  }

  private Guess lastGuess(Game game) {
    //noinspection DataFlowIssue,SequencedCollectionMethodCanBeUsed
    return game.getGuesses().isEmpty()
        ? null
        : game.getGuesses().get(game.getGuesses().size() - 1);
  }

  private void buildGuessControls(Game game, Guess baseGuess) {
    int[] previousGuess = (baseGuess == null)
        ? new int[game.getLength()]
        : baseGuess.getText().codePoints().toArray();
    binding.guessControls.removeAllViews();
    IntStream.of(previousGuess)
        .mapToObj(this::buildGuessButton)
        .forEach(binding.guessControls::addView);
    binding.guessControls.check(-1);
    if (binding.guessControls.getChildCount() > 0) {
      ((RadioButton) binding.guessControls.getChildAt(0)).setChecked(true);
    }
    binding.submit.setEnabled(isGuessComplete());
  }

  private void buildPaletteControls(Game game) {
    binding.palette.removeAllViews();
    game
        .getPool()
        .codePoints()
        .mapToObj(this::buildPaletteButton)
        .forEach(binding.palette::addView);
  }

  private boolean isGuessComplete() {
    return IntStream.range(0, binding.guessControls.getChildCount())
        .map((pos) -> (Integer) binding.guessControls.getChildAt(pos).getTag())
        .allMatch(symbolMap::hasSymbol);
  }

  private @NonNull ImageView buildPaletteButton(int codePoint) {
    ImageView paletteControl = (ImageView) getLayoutInflater()
        .inflate(R.layout.button_palette, binding.palette, false);
    SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
    paletteControl.setContentDescription(attributes.getName());
    paletteControl.setTooltipText(attributes.getName());
    paletteControl.setImageResource(attributes.getDrawableId());
    paletteControl.setImageTintList(ColorStateList.valueOf(attributes.getColor()));
    paletteControl.setOnClickListener(this::handlePaletteClick);
    paletteControl.setTag(codePoint);
    return paletteControl;
  }

  private @NonNull CompoundButton buildGuessButton(int codePoint) {
    CompoundButton guessControl = (CompoundButton) getLayoutInflater()
        .inflate(R.layout.button_guess, binding.palette, false);
    attachText(codePoint, guessControl);
    attachBackground(codePoint, guessControl);
    guessControl.setTag(codePoint);
    return guessControl;
  }

  private void handlePaletteClick(View control) {
    int codePoint = (Integer) control.getTag();
    int selectedGuessWidgetId = binding.guessControls.getCheckedRadioButtonId();
    if (selectedGuessWidgetId != -1) {
      CompoundButton guessControl = binding.guessControls.findViewById(selectedGuessWidgetId);
      attachText(codePoint, guessControl);
      attachBackground(codePoint, guessControl);
      guessControl.setTag(codePoint);
      int nextPosition = Math.min(
          binding.guessControls.indexOfChild(guessControl) + 1,
          binding.guessControls.getChildCount() - 1);
      binding.guessControls.check(binding.guessControls.getChildAt(nextPosition).getId());
    }
    binding.submit.setEnabled(isGuessComplete());
  }

  private void attachText(int codePoint, CompoundButton guessControl) {
    if (symbolMap.hasSymbol(codePoint)) {
      SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
      String name = attributes.getName();
      guessControl.setContentDescription(name);
      guessControl.setTooltipText(name);
    } else {
      String noSymbolName = getString(R.string.no_symbol);
      guessControl.setContentDescription(noSymbolName);
      guessControl.setTooltipText(noSymbolName);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  private void attachBackground(int codePoint, CompoundButton control) {
    Context context = requireContext();
    LayerDrawable checkedState = (LayerDrawable) AppCompatResources.getDrawable(
        context, R.drawable.guess_symbol_selected).mutate();
    LayerDrawable uncheckedState = (LayerDrawable) AppCompatResources.getDrawable(
        context, R.drawable.guess_symbol_unselected).mutate();
    if (symbolMap.hasSymbol(codePoint)) {
      Drawable drawable = AppCompatResources.getDrawable(
          context, symbolMap.getDrawableId(codePoint)).mutate();
      drawable.setTint(symbolMap.getColor(codePoint));
      checkedState.setDrawableByLayerId(R.id.symbol_layer, drawable);
      uncheckedState.setDrawableByLayerId(R.id.symbol_layer, drawable);
    } else {
      checkedState.setDrawableByLayerId(R.id.symbol_layer, null);
      uncheckedState.setDrawableByLayerId(R.id.symbol_layer, null);
    }
    StateListDrawable stateListDrawable = new StateListDrawable();
    stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedState);
    stateListDrawable.addState(new int[]{}, uncheckedState);
    control.setBackground(stateListDrawable);
  }

}
