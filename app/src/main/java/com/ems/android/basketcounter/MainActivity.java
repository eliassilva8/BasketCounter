package com.ems.android.basketcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ems.android.basketcounter.data.GameAdapter;
import com.ems.android.basketcounter.data.GameDbContract;
import com.ems.android.basketcounter.data.GameLoader;
import com.ems.android.basketcounter.data.GamePOJO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<GamePOJO>>, GameAdapter.GameAdapterOnClickHandler {
    @BindView(R.id.new_game_fab)
    FloatingActionButton mNewGameFab;
    @BindView(R.id.games_rv)
    RecyclerView mGamesRecyclerView;
    @BindView(R.id.adView_main)
    AdView mMainAdView;
    private static final int LOADER_ID = 100;
    private GameAdapter mGameAdapter;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        AdRequest adRequest = new AdRequest.Builder().build();
        mMainAdView.loadAd(adRequest);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mGamesRecyclerView.setLayoutManager(mLayoutManager);
        mGamesRecyclerView.setHasFixedSize(true);
        mGameAdapter = new GameAdapter(this);
        mGamesRecyclerView.setAdapter(mGameAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        mNewGameFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<GamePOJO>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new GameLoader(this, GameDbContract.GameEntry.CONTENT_URI);
        } else {
            throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<GamePOJO>> loader, List<GamePOJO> data) {
        mGameAdapter.setGameData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<GamePOJO>> loader) {
        mGameAdapter.setGameData(null);
    }

    @Override
    public void onClick(GamePOJO game) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(getString(R.string.game_clicked), game);
        startActivity(intent);
    }
}
