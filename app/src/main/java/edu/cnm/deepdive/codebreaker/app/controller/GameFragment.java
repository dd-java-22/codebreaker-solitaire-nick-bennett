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
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.api.model.Game;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.adapter.GuessesAdapter;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap.SymbolAttributes;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import edu.cnm.deepdive.codebreaker.client.service.GameSolvedException;
import edu.cnm.deepdive.codebreaker.client.service.InvalidPayloadException;
import edu.cnm.deepdive.codebreaker.client.service.ResourceNotFoundException;
import edu.cnm.deepdive.codebreaker.client.service.UnknownServiceException;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  private static final String TAG = GameFragment.class.getSimpleName();

  @Inject
  SymbolMap symbolMap;

  @Inject
  GuessesAdapter adapter;

  private FragmentGameBinding binding;
  private GameViewModel gameViewModel;
  private Game game;

  @Override
  public @Nullable View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    binding.guesses.setAdapter(adapter);
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
    gameViewModel.startGame("ROYGBIV", 4);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void submitGuess() {
    int[] guessCodePoints = IntStream.range(0, binding.guessControls.getChildCount())
        .mapToObj((pos) -> binding.guessControls.getChildAt(pos))
        .mapToInt((v) -> (Integer) v.getTag())
        .toArray();
    gameViewModel.submitGuess(new String(guessCodePoints, 0, guessCodePoints.length));
  }

  private void handleGame(Game game) {
    buildGuessControls(game);
    buildPaletteControls(game);
    updateGuessList(game);
  }

  private void handleSolved(Boolean solved) {
    boolean inProgress = Boolean.FALSE.equals(solved);
    IntStream.range(0, binding.guessControls.getChildCount())
        .mapToObj((pos) -> binding.guessControls.getChildAt(pos))
        .forEach((v) -> v.setEnabled(inProgress));
    IntStream.range(0, binding.palette.getChildCount())
        .mapToObj((pos) -> binding.palette.getChildAt(pos))
        .forEach((v) -> v.setEnabled(inProgress));
    binding.submit.setEnabled(inProgress && isGuessComplete());
  }

  private void handleGuess(Guess guess) {
    // TODO: 2026-03-06 Handle updates to the most recent guess.
  }

  private void handleError(Throwable error) {
    int messageId = switch (error) {
      case GameSolvedException ignored -> R.string.game_solved;
      case InvalidPayloadException ignored -> R.string.invalid_payload;
      case ResourceNotFoundException ignored -> R.string.resource_not_found;
      case UnknownServiceException ignored -> R.string.unknown_service_error;
      default -> R.string.generic_error;
    };
    Snackbar.make(binding.getRoot(), messageId, Snackbar.LENGTH_LONG).show();
  }

  private void updateGuessList(Game game) {
    List<Guess> guesses = game.getGuesses();
    //noinspection DataFlowIssue
    if (guesses.size() < adapter.getItemCount() || game != this.game) {
      adapter.clear();
      adapter.addAll(guesses);
    } else if (guesses.size() > adapter.getItemCount()) {
      adapter.addAll(guesses.subList(adapter.getItemCount(), guesses.size()));
    }
    this.game = game;
  }

  private void buildGuessControls(Game game) {
    int[] previousGuess = getLastGuessCodePoints(game);
    binding.guessControls.removeAllViews();
    IntStream.of(previousGuess)
        .mapToObj(this::buildGuessButton)
        .forEach(binding.guessControls::addView);
    binding.guessControls.check(-1);
    if (binding.guessControls.getChildCount() > 0) {
      ((RadioButton) binding.guessControls.getChildAt(0)).setChecked(true);
    }
  }

  private void buildPaletteControls(Game game) {
    binding.palette.removeAllViews();
    game.getPool()
        .codePoints()
        .mapToObj(this::buildPaletteButton)
        .forEach(binding.palette::addView);
  }

  private static int[] getLastGuessCodePoints(Game game) {
    List<Guess> guesses = game.getGuesses();
    //noinspection DataFlowIssue,SequencedCollectionMethodCanBeUsed
    return guesses.isEmpty()
        ? new int[game.getLength()]
        : guesses
            .get(guesses.size() - 1)
            .getText()
            .codePoints()
            .toArray();
  }

  private @NonNull CompoundButton buildGuessButton(int codePoint) {
    CompoundButton guessControl = (CompoundButton) getLayoutInflater()
        .inflate(R.layout.button_guess, binding.palette, false);
    attachText(codePoint, guessControl);
    attachBackground(codePoint, guessControl);
    guessControl.setTag(codePoint);
    return guessControl;
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
    LayerDrawable checkedState = (LayerDrawable) ResourcesCompat.getDrawable(
        getResources(), R.drawable.guess_symbol_selected, null);
    LayerDrawable uncheckedState = (LayerDrawable) ResourcesCompat.getDrawable(
        getResources(), R.drawable.guess_symbol_unselected, null);
    if (symbolMap.hasSymbol(codePoint)) {
      Drawable drawable = ResourcesCompat.getDrawable(
          getResources(), symbolMap.getDrawableId(codePoint), null).mutate();
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

  private boolean isGuessComplete() {
    return IntStream.range(0, binding.guessControls.getChildCount())
        .map((pos) -> (Integer) binding.guessControls.getChildAt(pos).getTag())
        .allMatch(symbolMap::hasSymbol);
  }

}
