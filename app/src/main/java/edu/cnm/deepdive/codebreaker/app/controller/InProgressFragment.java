package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentInProgressBinding;

public class InProgressFragment extends Fragment {

  private FragmentInProgressBinding binding;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentInProgressBinding.inflate(inflater, container, false);

    return binding.getRoot();
  }

}