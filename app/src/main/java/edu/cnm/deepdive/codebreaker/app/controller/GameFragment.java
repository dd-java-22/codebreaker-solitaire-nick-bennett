package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  @Inject
  SymbolMap symbolMap;
  
  private FragmentGameBinding binding;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}
