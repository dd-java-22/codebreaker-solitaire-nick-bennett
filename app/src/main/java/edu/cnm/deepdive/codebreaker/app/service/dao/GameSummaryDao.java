package edu.cnm.deepdive.codebreaker.app.service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import java.util.Collection;
import java.util.List;

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

  @Query("DELETE FROM game_summary")
  int deleteAll();

  @Query("SELECT * FROM game_summary WHERE game_summary_id = :id")
  LiveData<GameSummary> selectById(long id);

  @Query("SELECT * FROM game_summary WHERE external_key = :externalKey")
  LiveData<GameSummary> selectByExternalKey(String externalKey);

  @Query("SELECT * FROM game_summary WHERE solved = :solved AND length(pool) = :poolSize AND length = :codeLength ORDER BY guess_count ASC, (last_played - started) ASC")
  LiveData<List<GameSummary>> selectSummaries(boolean solved, int poolSize, int codeLength);

  @Query("SELECT * FROM game_summary WHERE solved = :solved ORDER BY last_played DESC")
  LiveData<List<GameSummary>> selectSummaries(boolean solved);

}
