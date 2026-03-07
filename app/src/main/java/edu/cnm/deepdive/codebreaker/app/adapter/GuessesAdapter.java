package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import jakarta.inject.Inject;

public class GuessesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final LayoutInflater inflater;

  @Inject
  public GuessesAdapter(@ActivityContext Context context) {
    inflater = LayoutInflater.from(context);
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
    throw new UnsupportedOperationException();
  }

  private static class GuessHolder extends ViewHolder {

    public GuessHolder(@NonNull android.view.View itemView) {
      super(itemView);
    }

  }

}
