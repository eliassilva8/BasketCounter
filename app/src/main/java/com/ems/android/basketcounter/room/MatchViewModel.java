package com.ems.android.basketcounter.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Elias on 20/11/2018.
 */
public class MatchViewModel extends AndroidViewModel {
    private MatchRepository mRepository;
    private LiveData<List<Match>> mAllMatches;

    public MatchViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MatchRepository(application);
        mAllMatches = mRepository.getAllMatches();
    }

    public LiveData<List<Match>> getAllMatches() {
        return mAllMatches;
    }

    public void insert(Match match) {
        mRepository.insert(match);
    }

    public void delete(Match match) { mRepository.delete(match);}
}
