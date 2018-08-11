package com.ems.android.basketcounter;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ems.android.basketcounter.data.GameDbContract;
import com.ems.android.basketcounter.data.GamePOJO;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.date_details_activity)
    TextView mDate;
    @BindView(R.id.tv_left_team_details)
    TextView mHomeName;
    @BindView(R.id.tv_right_team_details)
    TextView mGuestName;
    @BindView(R.id.tv_score_left_details)
    TextView mHomeScore;
    @BindView(R.id.tv_score_right_details)
    TextView mGuestScore;
    @BindView(R.id.winner_text)
    TextView mWinnerString;
    @BindView(R.id.adView_details)
    AdView mDetailsAdView;

    GamePOJO mGame;
    private InterstitialAd mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mInterstitial.loadAd(adRequest2);

        AdRequest adRequest = new AdRequest.Builder().build();
        mDetailsAdView.loadAd(adRequest);

        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Toast.makeText(DetailsActivity.this, getString(R.string.game_deleted), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        mGame = intent.getParcelableExtra(getString(R.string.game_clicked));

        mDate.setText(mGame.getDate());
        mHomeName.setText(mGame.getHomeTeamName());
        mGuestName.setText(mGame.getGuestTeamName());
        mHomeScore.setText(mGame.getHomeTeamPoints());
        mGuestScore.setText(mGame.getGuestTeamPoints());

        int homeScore = Integer.parseInt(mGame.getHomeTeamPoints());
        int guestScore = Integer.parseInt(mGame.getGuestTeamPoints());
        if (homeScore > guestScore) {
            mWinnerString.setText(mGame.getHomeTeamName() + " " + getString(R.string.won));
        } else if (homeScore < guestScore) {
            mWinnerString.setText(mGame.getGuestTeamName() + " " + getString(R.string.won));
        } else {
            mWinnerString.setText(getString(R.string.draw));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (mInterstitial.isLoaded()) {
                    deleteMatch();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMatch() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = GameDbContract.GameEntry.buildGameUri(mGame.getId());
        contentResolver.delete(uri, null, null);
        mInterstitial.show();
    }
}
