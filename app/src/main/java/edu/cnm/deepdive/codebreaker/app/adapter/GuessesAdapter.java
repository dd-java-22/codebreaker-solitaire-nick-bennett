package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.api.model.Guess;
import edu.cnm.deepdive.codebreaker.app.R;
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
    return null;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((GuessHolder) holder).bind(position);
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  private class GuessHolder extends ViewHolder {

    private GuessHolder(@NonNull View itemView) {
      super(itemView);
    }

    private void bind(int position) {
      throw new UnsupportedOperationException("bind() method not implemented");
    }

  }

}
