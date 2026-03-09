package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGuessBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GuessesAdapter extends RecyclerView.Adapter<ViewHolder>{

  private final LayoutInflater inflater;
  private final SymbolMap symbolMap;
  private final String guessNumberFormat;
  private final String matchCountFormat;
  private final List<Guess> guesses;

  @Inject
  public GuessesAdapter(@ActivityContext Context context, SymbolMap symbolMap) {
    inflater = LayoutInflater.from(context);
    this.symbolMap = symbolMap;
    guessNumberFormat = context.getString(R.string.guess_number_format);
    matchCountFormat = context.getString(R.string.match_count_format);
    guesses = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GuessHolder(ItemGuessBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((GuessHolder) holder).bind(position);
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  public void clear() {
    int size = guesses.size();
    guesses.clear();
    notifyItemRangeRemoved(0, size);
  }

  public void addAll(List<Guess> guesses) {
    int startPosition = this.guesses.size();
    this.guesses.addAll(guesses);
    notifyItemRangeInserted(startPosition, guesses.size());
  }

  private class GuessHolder extends ViewHolder {

    private final ItemGuessBinding binding;

    private GuessHolder(@NonNull ItemGuessBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      Guess guess = guesses.get(position);
      binding.number.setText(String.format(guessNumberFormat, position + 1));
      binding.exactMatches.setText(String.format(matchCountFormat, guess.getExactMatches()));
      binding.nearMatches.setText(String.format(matchCountFormat, guess.getNearMatches()));
      // TODO: 2026-03-09 Populate the binding.symbols LinearLayout with the appropriate symbols.
    }

  }

}
