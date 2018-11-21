package com.ems.android.basketcounter.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
