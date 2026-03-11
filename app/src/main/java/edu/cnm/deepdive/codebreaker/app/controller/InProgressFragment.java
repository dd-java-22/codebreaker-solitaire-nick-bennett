package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentInProgressBinding;
import edu.cnm.deepdive.codebreaker.app.viewmodel.SummaryViewModel;

public class InProgressFragment extends Fragment {

  private FragmentInProgressBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentInProgressBinding.inflate(inflater, container, false);
    // TODO: 2026-03-11 Attach adapter to recyclerview.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SummaryViewModel viewModel = new ViewModelProvider(requireActivity()).get(SummaryViewModel.class);
    viewModel
        .getInProgressSummaries()
        .observe(getViewLifecycleOwner(), (summaries) -> {
          // TODO: 2026-03-11 Populate recyclerview adapter with summaries.
        });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}