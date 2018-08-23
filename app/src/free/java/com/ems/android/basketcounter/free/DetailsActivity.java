package com.ems.android.basketcounter.free;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.data.GameDbContract;
import com.ems.android.basketcounter.data.GamePOJO;
import com.ems.android.basketcounter.utils.NetworkReceiver;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements NetworkReceiver.NetworkReceiverListener {
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
    private NetworkReceiver mReceiver;
    private String mHomeNameString;
    private String mGuestNameString;
    private String mHomeScoreString;
    private String mGuestScoreString;
    private int mItemMenuSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                switch (mItemMenuSelected) {
                    case R.id.delete:
                        Toast.makeText(DetailsActivity.this, getString(R.string.game_deleted), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.share:
                        shareOnFacebook();
                        break;
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(DetailsActivity.this, getString(R.string.action_cannot_be_executed), Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkReceiver();
        this.registerReceiver(mReceiver, filter);
        mReceiver.setNetworkReceiverListener(this);

        Intent intent = getIntent();
        mGame = intent.getParcelableExtra(getString(R.string.game_clicked));
        mHomeNameString = mGame.getHomeTeamName();
        mGuestNameString = mGame.getGuestTeamName();
        mHomeScoreString = mGame.getHomeTeamPoints();
        mGuestScoreString = mGame.getGuestTeamPoints();

        mDate.setText(mGame.getDate());
        mHomeName.setText(mHomeNameString);
        mGuestName.setText(mGuestNameString);
        mHomeScore.setText(mHomeScoreString);
        mGuestScore.setText(mHomeScoreString);

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
                if (mReceiver.isConnected(this)) {
                    if (mInterstitial.isLoaded()) {
                        mItemMenuSelected = R.id.delete;
                        deleteMatch();
                    }
                    return true;
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
                }
            case R.id.share:
                if (mReceiver.isConnected(this)) {
                    if (mInterstitial.isLoaded()) {
                        mItemMenuSelected = R.id.share;
                        mInterstitial.show();
                    }
                    return true;
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
                }
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

    @Override
    public void onConnectionChange(boolean isConnected) {
        if (mReceiver.isConnected(this)) {
            AdRequest adInterstitialRequest = new AdRequest.Builder().build();
            mInterstitial.loadAd(adInterstitialRequest);

            AdRequest adBannerRequest = new AdRequest.Builder().build();
            mDetailsAdView.loadAd(adBannerRequest);
        }
    }

    /**
     * Publish match result in facebook
     */
    private void shareOnFacebook() {
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:url", "https://play.google.com/store/apps/details?id=com.ems.android.basketcounter&hl=en_US")
                .putString("fb:app_id", getString(R.string.facebook_app_id))
                .putString("og:title", getString(R.string.final_result))
                .putString("og:description", mHomeNameString + " - " + mHomeScoreString + " | " + mGuestNameString + " - " + mGuestScoreString)
                .putString("og:type", "website")
                .putString("og:image", "https://lh3.googleusercontent.com/ILk5NjkuOW2PaQAPG04DGt5r6t-KsC-V_aHzGqZ6_mjWGwYt3fBX5AlAnSliS16D4hA=s180-rw")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("fitness.runs")
                .putObject("match", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("match")
                .setAction(action)
                .build();
        ShareDialog.show(this, content);
    }
}
