package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuessesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final LayoutInflater inflater;
  private final List<Guess> guesses;

  @Inject
  public GuessesAdapter(@ActivityContext Context context) {
    inflater = LayoutInflater.from(context);
    guesses = new ArrayList<>();
  }

  @Override
  public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    guesses.clear();
    notifyDataSetChanged();
  }

  private static class GuessHolder extends ViewHolder {

    public GuessHolder(@NonNull android.view.View itemView) {
      super(itemView);
    }

  }

}
