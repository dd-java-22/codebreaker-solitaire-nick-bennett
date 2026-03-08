package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGuessBinding;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuessesAdapter extends RecyclerView.Adapter<GuessesAdapter.GuessHolder> {

  private final LayoutInflater inflater;
  private final List<Guess> guesses;

  @Inject
  public GuessesAdapter(@ActivityContext Context context) {
    inflater = LayoutInflater.from(context);
    guesses = new ArrayList<>();
  }

  @Override
  public @NonNull GuessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GuessHolder(ItemGuessBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull GuessHolder holder, int position) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  public void addAll(Collection<Guess> guesses) {
    int startPosition = this.guesses.size();
    this.guesses.addAll(guesses);
    notifyItemRangeInserted(startPosition, guesses.size());
  }

  public void clear() {
    int size = guesses.size();
    guesses.clear();
    notifyItemRangeRemoved(0, size);
  }

  static class GuessHolder extends RecyclerView.ViewHolder {

    private final ItemGuessBinding binding;

    public GuessHolder(@NonNull ItemGuessBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

  }

}
