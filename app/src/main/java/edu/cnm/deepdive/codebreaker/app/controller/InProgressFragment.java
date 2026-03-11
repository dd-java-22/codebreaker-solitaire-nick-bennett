package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.adapter.GameSummariesAdapter;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentInProgressBinding;
import edu.cnm.deepdive.codebreaker.app.viewmodel.SummaryViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class InProgressFragment extends Fragment {

  @Inject
  GameSummariesAdapter adapter;

  private FragmentInProgressBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentInProgressBinding.inflate(inflater, container, false);
    binding.summaries.setAdapter(adapter);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SummaryViewModel viewModel = new ViewModelProvider(requireActivity()).get(SummaryViewModel.class);
    viewModel
        .getInProgressSummaries()
        .observe(getViewLifecycleOwner(), (summaries) -> {
          adapter.getSummaries().clear();
          adapter.getSummaries().addAll(summaries);
          adapter.notifyDataSetChanged();
        });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}