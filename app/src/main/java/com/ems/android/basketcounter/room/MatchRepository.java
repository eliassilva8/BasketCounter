package com.ems.android.basketcounter.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Elias on 20/11/2018.
 */
public class MatchRepository {
    private MatchDao mMatchDao;
    private LiveData<List<Match>> mAllMatches;

    MatchRepository(Application application) {
        MatchRoomDatabase db = MatchRoomDatabase.getDatabase(application);
        mMatchDao = db.matchDao();
        mAllMatches = mMatchDao.getAllMatches();
    }

    LiveData<List<Match>> getAllMatches() {
        return mAllMatches;
    }

    public void insert(Match match) {
        new insertAsyncTask(mMatchDao).execute(match);
    }

    private class insertAsyncTask extends AsyncTask<Match, Void, Void> {
        private MatchDao mAsyncTaskDao;

        insertAsyncTask(MatchDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Match... matches) {
            mAsyncTaskDao.Insert(matches[0]);
            return null;
        }
    }

    public void delete(Match match) {
        new deleteAsyncTask(mMatchDao).execute(match);
    }

    private class deleteAsyncTask extends AsyncTask<Match, Void, Void> {
        private MatchDao mAsyncTaskDao;

        deleteAsyncTask(MatchDao dao) {mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(Match... matches) {
            mAsyncTaskDao.Delete(matches[0]);
            return null;
        }
    }

}
