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

  String IN_PROGRESS_QUERY = """
      SELECT
        *
      FROM
        game_summary
      WHERE
        NOT solved
      ORDER BY
        IFNULL(last_played, started) DESC""";

  String COMPLETED_RANKING_QUERY = """
      SELECT
        *
      FROM
        game_summary
      WHERE
        solved
        AND pool_size = :poolSize
        AND code_length = :codeLength
      ORDER BY
        guess_count ASC, 
        (last_played - started) ASC""";

  @Insert
  long insert(GameSummary summary);

  @Update
  int update(GameSummary summary);

  @Delete
  int delete(GameSummary summary);

  @Delete
  int deleteAll(Collection<GameSummary> summaries);

  @Query(IN_PROGRESS_QUERY)
  LiveData<List<GameSummary>> selectInProgress();

  @Query(COMPLETED_RANKING_QUERY)
  LiveData<List<GameSummary>> selectCompleted(int poolSize, int codeLength);

}




