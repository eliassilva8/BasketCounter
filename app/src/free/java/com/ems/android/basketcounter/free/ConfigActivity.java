package com.ems.android.basketcounter.free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.databinding.ActivityConfigBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Elias on 19/01/2018.
 */
public class ConfigActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private String mLeftTeamString;
    private String mRightTeamString;
    private String mTimeString;
    private String mBonusString;
    private ActivityConfigBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_config);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adViewConfig.loadAd(adRequest);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mBinding.confirmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "confirm");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                manageUserInput();
                Intent intent = new Intent(ConfigActivity.this, DisplayActivity.class);
                intent.putExtra(getString(R.string.left_team), mLeftTeamString);
                intent.putExtra(getString(R.string.right_team), mRightTeamString);
                intent.putExtra(getString(R.string.time_per_quarter), mTimeString);
                intent.putExtra(getString(R.string.bonus_situation), mBonusString);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });
    }

    private void manageUserInput() {
        if (mBinding.etLeftTeamName.getText().toString().matches("")) {
            mLeftTeamString = null;
        } else {
            mLeftTeamString = mBinding.etLeftTeamName.getText().toString();
        }

        if (mBinding.etRightTeamName.getText().toString().matches("")) {
            mRightTeamString = null;
        } else {
            mRightTeamString = mBinding.etRightTeamName.getText().toString();
        }

        if (mBinding.etTimer.getText().toString().matches("")) {
            mTimeString = null;
        } else {
            mTimeString = mBinding.etTimer.getText().toString();
        }

        if (mBinding.etBonusSituation.getText().toString().matches("")) {
            mBonusString = null;
        } else {
            mBonusString = mBinding.etBonusSituation.getText().toString();
        }
    }
}
