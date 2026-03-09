package edu.cnm.deepdive.codebreaker.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Dao
public interface GameSummaryDao {

  @Insert
  long insert(GameSummary summary);

  @Update
  int update(GameSummary summary);

  @Delete
  int delete(GameSummary summary);

  @Delete
  int delete(Collection<GameSummary> summaries);

  @Query("DELETE FROM game_summary WHERE external_key = :externalKey")
  int delete(String externalKey);

  @Query("DELETE FROM game_summary")
  int deleteAll();

  @Query("SELECT * FROM game_summary WHERE external_key = :externalKey")
  GameSummary selectByExternalKey(String externalKey);

  @Query("SELECT * FROM game_summary WHERE solved = :solved AND pool_size = :poolSize AND code_length = :codeLength ORDER BY guess_count ASC, (last_played - started) ASC")
  LiveData<List<GameSummary>> selectSummaries(boolean solved, int poolSize, int codeLength);

  @Query("SELECT * FROM game_summary WHERE solved = :solved ORDER BY last_played DESC")
  LiveData<List<GameSummary>> selectSummaries(boolean solved);

}
