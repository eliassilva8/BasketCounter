package com.ems.android.basketcounter.free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.databinding.ActivityMainBinding;
import com.ems.android.basketcounter.room.Match;
import com.ems.android.basketcounter.room.MatchListAdapter;
import com.ems.android.basketcounter.room.MatchViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MatchListAdapter.MatchAdapterOnClickHandler {

    private ActivityMainBinding mBinding;
    private MatchListAdapter mMatchAdapter;
    private LinearLayoutManager mLayoutManager;
    private MatchViewModel mMatchViewModel;
    public static final int NEW_DISPLAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_DETAILS_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));

        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adViewMain.loadAd(adRequest);
        mBinding.adViewMain.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mBinding.adViewMain.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                mBinding.adViewMain.setVisibility(View.GONE);
            }
        });

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMatchAdapter = new MatchListAdapter(this, this);
        mBinding.gamesRv.setAdapter(mMatchAdapter);
        mBinding.gamesRv.setLayoutManager(mLayoutManager);
        mBinding.gamesRv.setHasFixedSize(true);

        mMatchViewModel = ViewModelProviders.of(this).get(MatchViewModel.class);
        mMatchViewModel.getAllMatches().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(@Nullable List<Match> matches) {
                mMatchAdapter.setMatchData(matches);
            }
        });

        mBinding.newGameFab.setOnClickListener(new View.OnClickListener() {
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
            if (data != null) {
                switch (requestCode) {
                    case NEW_DISPLAY_ACTIVITY_REQUEST_CODE:
                        Match matchToSave = data.getParcelableExtra(getString(R.string.match_to_save));
                        mMatchViewModel.insert(matchToSave);
                        break;
                    case NEW_DETAILS_ACTIVITY_REQUEST_CODE:
                        Match matchToDelete = data.getParcelableExtra(getString(R.string.match_to_delete));
                        mMatchViewModel.delete(matchToDelete);
                        break;
                }
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
