package com.ems.android.basketcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Elias on 19/01/2018.
 */

public class ConfigActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;

    EditText mLeftTeam;
    EditText mRightTeam;
    EditText mTimePerQuarter;
    EditText mBonus;

    String mLeftTeamString;
    String mRightTeamString;
    String mTimeString;
    String mBonusString;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        MobileAds.initialize(this, "ca-app-pub-7487555387883892~3043909438");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);

        mLeftTeam = findViewById(R.id.et_left_team_name);
        mRightTeam = findViewById(R.id.et_right_team_name);
        mTimePerQuarter = findViewById(R.id.et_timer);
        mBonus = findViewById(R.id.et_bonus_situation);
    }

    /**
     * Sends the data from the user input to the main activity
     *
     * @param view
     */
    public void confirm(View view) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "confirm");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        manageUserInput();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.left_team), mLeftTeamString);
        intent.putExtra(getString(R.string.right_team), mRightTeamString);
        intent.putExtra(getString(R.string.time_per_quarter), mTimeString);
        intent.putExtra(getString(R.string.bonus_situation), mBonusString);
        startActivity(intent);
    }

    private void manageUserInput() {
        if (mLeftTeam.getText().toString().matches("")) {
            mLeftTeamString = null;
        } else {
            mLeftTeamString = mLeftTeam.getText().toString();
        }

        if (mRightTeam.getText().toString().matches("")) {
            mRightTeamString = null;
        } else {
            mRightTeamString = mRightTeam.getText().toString();
        }

        if (mTimePerQuarter.getText().toString().matches("")) {
            mTimeString = null;
        } else {
            mTimeString = mTimePerQuarter.getText().toString();
        }

        if (mBonus.getText().toString().matches("")) {
            mBonusString = null;
        } else {
            mBonusString = mBonus.getText().toString();
        }
    }

}
