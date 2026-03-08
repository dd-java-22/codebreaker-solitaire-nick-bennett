package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGuessBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap.SymbolAttributes;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuessesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final LayoutInflater inflater;
  private final SymbolMap symbolMap;
  private final String matchCountFormat;
  private final List<Guess> guesses;

  @Inject
  public GuessesAdapter(@ActivityContext Context context, SymbolMap symbolMap) {
    inflater = LayoutInflater.from(context);
    this.symbolMap = symbolMap;
    matchCountFormat = context.getString(R.string.match_count_format);
    guesses = new ArrayList<>();
  }

  @Override
  public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GuessHolder(ItemGuessBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((GuessHolder) holder).bind(guesses.get(position));
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  public void addAll(List<Guess> guesses) {
    int startPosition = this.guesses.size();
    this.guesses.addAll(guesses);
    notifyItemRangeInserted(startPosition, guesses.size());
  }

  public void clear() {
    int size = guesses.size();
    guesses.clear();
    notifyItemRangeRemoved(0, size);
  }

  private class GuessHolder extends RecyclerView.ViewHolder {

    private final ItemGuessBinding binding;

    public GuessHolder(@NonNull ItemGuessBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(Guess guess) {
      binding.exactMatches.setText(String.format(matchCountFormat, guess.getExactMatches()));
      binding.nearMatches.setText(String.format(matchCountFormat, guess.getNearMatches()));
      binding.symbols.removeAllViews();
      guess
          .getText()
          .codePoints()
          .forEach((codePoint) -> {
            ImageView character = (ImageView) inflater.inflate(
                R.layout.item_guess_character, binding.symbols, false);
            SymbolAttributes attributes = symbolMap.getAttributes(codePoint);
            character.setImageResource(attributes.getDrawableId());
            character.setImageTintList(ColorStateList.valueOf(attributes.getColor()));
            character.setContentDescription(attributes.getName());
            character.setTooltipText(attributes.getName());
            binding.symbols.addView(character);
          });
    }

  }

}
