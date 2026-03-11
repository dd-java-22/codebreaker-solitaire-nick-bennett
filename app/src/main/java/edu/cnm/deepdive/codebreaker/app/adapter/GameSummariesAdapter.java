package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGameSummaryBinding;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import javax.inject.Inject;

public class GameSummariesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofLocalizedDateTime(FormatStyle.SHORT)
      .withZone(ZoneId.systemDefault());

  private final Context context;
  private List<GameSummary> summaries;

  @Inject
  public GameSummariesAdapter(@ActivityContext Context context) {
    this.context = context;
  }

  public List<GameSummary> getSummaries() {
    return summaries;
  }

  public void setSummaries(List<GameSummary> summaries) {
    this.summaries = summaries;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemGameSummaryBinding binding = ItemGameSummaryBinding.inflate(
        LayoutInflater.from(context), parent, false);
    return new Holder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((Holder) holder).bind(summaries.get(position), position);
  }

  @Override
  public int getItemCount() {
    return (summaries != null) ? summaries.size() : 0;
  }

  private class Holder extends ViewHolder {

    private final ItemGameSummaryBinding binding;

    public Holder(@NonNull ItemGameSummaryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(GameSummary summary, int position) {
      binding.lastPlayed.setText(FORMATTER.format(summary.getLastPlayed()));
      binding.poolSize.setText(String.valueOf(summary.getPoolSize()));
      binding.codeLength.setText(String.valueOf(summary.getCodeLength()));
      binding.guessCount.setText(String.valueOf(summary.getGuessCount()));
    }

  }

}
