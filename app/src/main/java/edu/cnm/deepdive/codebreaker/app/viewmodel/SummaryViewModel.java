package edu.cnm.deepdive.codebreaker.app.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import edu.cnm.deepdive.codebreaker.app.service.repository.GameSummaryRepository;
import jakarta.inject.Inject;
import java.util.List;

@HiltViewModel
public class SummaryViewModel extends ViewModel {

  private final GameSummaryRepository repository;
  private final MutableLiveData<Integer> poolSize;
  private final MutableLiveData<Integer> codeLength;
  private final LiveData<List<GameSummary>> completedSummaries;

  @Inject
  SummaryViewModel(@ApplicationContext Context context, GameSummaryRepository repository) {
    this.repository = repository;
    poolSize = new MutableLiveData<>(); // FIXME: 2026-03-11 Take initial values from prefs and/or resources.
    codeLength = new MutableLiveData<>(); // FIXME: 2026-03-11 Take initial values from prefs and/or resources.
    completedSummaries = buildCompletedSummaries();
  }

  public MutableLiveData<Integer> getPoolSize() {
    return poolSize;
  }

  public MutableLiveData<Integer> getCodeLength() {
    return codeLength;
  }

  public LiveData<List<GameSummary>> getCompletedSummaries() {
    return completedSummaries;
  }

  public LiveData<List<GameSummary>> getInProgressSummaries() {
    return repository.selectInProgress();
  }

  private LiveData<List<GameSummary>> buildCompletedSummaries() {
    MediatorLiveData<QueryPair> queryPair = new MediatorLiveData<>();
    queryPair.addSource(poolSize, (newSize) ->
        queryPair.setValue(new QueryPair(newSize, codeLength.getValue())));
    queryPair.addSource(codeLength, (newLength) ->
        queryPair.setValue(new QueryPair(poolSize.getValue(), newLength)));
    return Transformations.switchMap(queryPair, (pair) ->
        repository.selectCompleted(pair.poolSize(), pair.codeLength()));
  }

  private record QueryPair(int poolSize, int codeLength) {
  }

}
