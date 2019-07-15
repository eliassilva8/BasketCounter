package com.ems.android.basketcounter.free;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.databinding.ActivityDetailsBinding;
import com.ems.android.basketcounter.room.Match;
import com.ems.android.basketcounter.utils.NetworkReceiver;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import bolts.AppLinks;

public class DetailsActivity extends AppCompatActivity implements NetworkReceiver.NetworkReceiverListener {
    Match mMatch;
    private InterstitialAd mInterstitial;
    private NetworkReceiver mReceiver;
    private String mHomeNameString;
    private String mGuestNameString;
    private String mHomeScoreString;
    private String mGuestScoreString;
    private int mItemMenuSelected;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private ActivityDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

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
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.match_to_delete), mMatch);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case R.id.share:
                        shareOnFacebook();
                        break;
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }

        Intent intent = getIntent();
        mMatch = intent.getParcelableExtra(getString(R.string.match_clicked));
        mHomeNameString = mMatch.getHomeTeamName();
        mGuestNameString = mMatch.getGuestTeamName();
        mHomeScoreString = mMatch.getHomeTeamPoints();
        mGuestScoreString = mMatch.getGuestTeamPoints();

        mBinding.dateDetailsActivity.setText(mMatch.getDate());
        mBinding.tvLeftTeamDetails.setText(mHomeNameString);
        mBinding.tvRightTeamDetails.setText(mGuestNameString);
        mBinding.tvScoreLeftDetails.setText(mHomeScoreString);
        mBinding.tvScoreRightDetails.setText(mGuestScoreString);

        int homeScore = Integer.parseInt(mMatch.getHomeTeamPoints());
        int guestScore = Integer.parseInt(mMatch.getGuestTeamPoints());
        if (homeScore > guestScore) {
            mBinding.winnerText.setText(mMatch.getHomeTeamName() + " " + getString(R.string.won));
        } else if (homeScore < guestScore) {
            mBinding.winnerText.setText(mMatch.getGuestTeamName() + " " + getString(R.string.won));
        } else {
            mBinding.winnerText.setText(getString(R.string.draw));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkReceiver();
        this.registerReceiver(mReceiver, filter);
        mReceiver.setNetworkReceiverListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
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
                        mInterstitial.show();
                    } else {
                        Toast.makeText(DetailsActivity.this, getString(R.string.action_cannot_be_executed), Toast.LENGTH_SHORT).show();
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
                    } else {
                        Toast.makeText(DetailsActivity.this, getString(R.string.action_cannot_be_executed), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else {
                    Toast.makeText(DetailsActivity.this, getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
                }
            case android.R.id.home:
                setResult(RESULT_OK);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionChange(boolean isConnected) {
        if (mReceiver.isConnected(this)) {
            AdRequest adInterstitialRequest = new AdRequest.Builder().build();
            mInterstitial.loadAd(adInterstitialRequest);

            AdRequest adBannerRequest = new AdRequest.Builder().build();
            mBinding.adViewDetails.loadAd(adBannerRequest);
        }
    }

    /**
     * Publish match result in facebook
     */
    private void shareOnFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(getString(R.string.final_result) + "\n" + mHomeNameString + " - " + mHomeScoreString + " | " + mGuestNameString + " - " + mGuestScoreString)
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.ems.android.basketcounter"))
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag("#BasketCounter")
                            .build())
                    .build();
            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
