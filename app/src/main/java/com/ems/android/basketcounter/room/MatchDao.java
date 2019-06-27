package com.ems.android.basketcounter.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Created by Elias on 20/11/2018.
 */
@Dao
public interface MatchDao {

    @Insert
    void Insert(Match match);

    @Delete
    void Delete(Match match);

    @Query("SELECT * FROM games_saved ORDER BY date ASC")
    LiveData<List<Match>> getAllMatches();

    @Query("SELECT * FROM games_saved WHERE id = :id")
    Match getMatch(int id);
}
