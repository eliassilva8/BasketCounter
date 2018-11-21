package com.ems.android.basketcounter.free;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.room.Match;
import com.ems.android.basketcounter.room.MatchListAdapter;
import com.ems.android.basketcounter.room.MatchViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements /*LoaderManager.LoaderCallbacks<List<GamePOJO>>, GameAdapter.GameAdapterOnClickHandler*/ MatchListAdapter.MatchAdapterOnClickHandler {
    @BindView(R.id.new_game_fab)
    FloatingActionButton mNewGameFab;
    @BindView(R.id.games_rv)
    RecyclerView mGamesRecyclerView;
    @BindView(R.id.adView_main)
    AdView mMainAdView;
    private MatchListAdapter mMatchAdapter;
    private LinearLayoutManager mLayoutManager;
    private MatchViewModel mMatchViewModel;
    public static final int NEW_DISPLAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_DETAILS_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        AdRequest adRequest = new AdRequest.Builder().build();
        mMainAdView.loadAd(adRequest);
        mMainAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mMainAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                mMainAdView.setVisibility(View.GONE);
            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMatchAdapter = new MatchListAdapter(this, this);
        mGamesRecyclerView.setAdapter(mMatchAdapter);
        mGamesRecyclerView.setLayoutManager(mLayoutManager);
        mGamesRecyclerView.setHasFixedSize(true);

        mMatchViewModel = ViewModelProviders.of(this).get(MatchViewModel.class);
        mMatchViewModel.getAllMatches().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(@Nullable List<Match> matches) {
                mMatchAdapter.setMatchData(matches);
            }
        });

        mNewGameFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivityForResult(intent, NEW_DISPLAY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NEW_DISPLAY_ACTIVITY_REQUEST_CODE:
                    Match matchToSave = data.getParcelableExtra(getString(R.string.match_to_save));
                    mMatchViewModel.insert(matchToSave);
                    break;
                case NEW_DETAILS_ACTIVITY_REQUEST_CODE:
                    Match matchToDelete = data.getParcelableExtra(getString(R.string.match_to_delete));
                    mMatchViewModel.delete(matchToDelete);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(Match match) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(getString(R.string.match_clicked), match);
        startActivityForResult(intent, NEW_DETAILS_ACTIVITY_REQUEST_CODE);
    }
}
