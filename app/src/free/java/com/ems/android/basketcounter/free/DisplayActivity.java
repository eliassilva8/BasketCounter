package com.ems.android.basketcounter.free;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.room.Match;
import com.ems.android.basketcounter.utils.NetworkReceiver;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayActivity extends AppCompatActivity implements NetworkReceiver.NetworkReceiverListener {
    private int mScoreLeftTeam = 0;
    private int mScoreRightTeam = 0;
    private int mFoulsLeftTeam = 0;
    private int mFoulsRightTeam = 0;
    private long mQuarterTime = 0;
    private int mBonusSituation = 0;
    private int mQuarter = 1;
    private InterstitialAd mInterstitial;
    private CountDownTimer mCountDownTimer;
    private long mTimeRemaining;
    private boolean isTimerPaused;
    private NetworkReceiver mReceiver;
    private String mHomeName;
    private String mGuestName;
    private int mItemMenuSelected;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private Match mMatchToSave;

    @BindView(R.id.tv_left_team)
    TextView mTvLeftTeam;
    @BindView(R.id.tv_right_team)
    TextView mTvRightTeam;
    @BindView(R.id.tv_score_left)
    TextView mTvLeftScore;
    @BindView(R.id.tv_score_right)
    TextView mTvRightScore;
    @BindView(R.id.tv_quarter_time_display)
    TextView mTvTimer;
    @BindView(R.id.tv_foul_left)
    TextView tvFoulsLeft;
    @BindView(R.id.tv_foul_right)
    TextView tvFoulsRight;
    @BindView(R.id.bt_pause_timer)
    ImageButton btPauseTimer;
    @BindView(R.id.bt_play_timer)
    ImageButton btPlayTimer;
    @BindView(R.id.quarter_tv)
    TextView mQuarterTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
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
                    case R.id.save:
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.match_to_save), mMatchToSave);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case R.id.share:
                        shareOnFacebook();
                        break;
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(DisplayActivity.this, getString(R.string.action_cannot_be_executed), Toast.LENGTH_SHORT).show();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
            startActivity(intent);
        }

        Intent intent = getIntent();
        mHomeName = intent.getExtras().getString(getString(R.string.left_team), getString(R.string.left_team));
        mGuestName = intent.getExtras().getString(getString(R.string.right_team), getString(R.string.right_team));
        mTvLeftTeam.setText(mHomeName);
        mTvRightTeam.setText(mGuestName);
        mBonusSituation = Integer.parseInt(intent.getExtras().getString(getString(R.string.bonus_situation), "4"));
        String timeString = intent.getExtras().getString(getString(R.string.time_per_quarter), "10");
        mQuarterTime = minutesToMilliseconds(timeString);
        mTvTimer.setText(formatTime(mQuarterTime));
        btPauseTimer.setVisibility(View.INVISIBLE);
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

    /**
     * Manage the CountDownTimer
     */
    private void createTimer(long time) {

        mCountDownTimer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                mTvTimer.setText(formatTime(millisUntilFinished));
                mTimeRemaining = millisUntilFinished;
            }

            public void onFinish() {
                mTvTimer.setText(formatTime(mQuarterTime));
                btPauseTimer.setVisibility(View.INVISIBLE);
                btPlayTimer.setVisibility(View.VISIBLE);
            }
        };
    }

    public void playTimer(View view) {
        btPlayTimer = findViewById(R.id.bt_play_timer);
        btPlayTimer.setVisibility(View.INVISIBLE);
        btPauseTimer.setVisibility(View.VISIBLE);

        if (isTimerPaused) {
            createTimer(mTimeRemaining);
        } else {
            createTimer(mQuarterTime);
        }
        mCountDownTimer.start();
    }

    public void pauseTimer(View view) {
        mCountDownTimer.cancel();
        isTimerPaused = true;
        btPlayTimer.setVisibility(View.VISIBLE);
        btPauseTimer.setVisibility(View.INVISIBLE);
    }

    public void restartTimer(View view) {
        if (mCountDownTimer == null) {
            return;
        }
        mCountDownTimer.cancel();
        createTimer(mQuarterTime);
        btPlayTimer.setVisibility(View.VISIBLE);
        btPauseTimer.setVisibility(View.INVISIBLE);
        mTvTimer.setText(formatTime(mQuarterTime));
        isTimerPaused = false;
    }

    /**
     * Sets the time format for the mTvTimer
     *
     * @param millis - time in miliseconds
     * @return - String with the specified format
     */
    private String formatTime(long millis) {
        String output;
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String sec = String.valueOf(seconds);
        String min = String.valueOf(minutes);

        if (seconds < 10)
            sec = "0" + seconds;
        if (minutes < 10)
            min = "0" + minutes;

        output = min + " : " + sec;
        return output;
    }

    /**
     * Adds the specific number of points to the home team
     *
     * @param view
     */
    public void addPointsLeft(View view) {
        switch (view.getId()) {
            case R.id.bt_three_points_left:
                mScoreLeftTeam += 3;
                break;
            case R.id.bt_two_points_left:
                mScoreLeftTeam += 2;
                break;
            case R.id.bt_free_throw_left:
                mScoreLeftTeam++;
                break;
            case R.id.bt_minus_one_point_left:
                if (mScoreLeftTeam > 0) {
                    mScoreLeftTeam--;
                }
                break;
        }
        mTvLeftScore.setText(String.valueOf(mScoreLeftTeam));
    }

    /**
     * Adds the specific number of points to the guest team
     *
     * @param view
     */
    public void addPointsRight(View view) {
        switch (view.getId()) {
            case R.id.bt_three_points_right:
                mScoreRightTeam += 3;
                break;
            case R.id.bt_two_points_right:
                mScoreRightTeam += 2;
                break;
            case R.id.bt_free_throw_right:
                mScoreRightTeam++;
                break;
            case R.id.bt_minus_one_point_right:
                if (mScoreRightTeam > 0) {
                    mScoreRightTeam--;
                }
                break;
        }
        mTvRightScore.setText(String.valueOf(mScoreRightTeam));
    }

    /**
     * Adds or subtract a foul to the home team
     *
     * @param view
     */
    public void foulsLeftClicked(View view) {
        if (view.getId() == R.id.bt_foul_left) {
            mFoulsLeftTeam++;
            if (mFoulsLeftTeam >= mBonusSituation) {
                tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mFoulsLeftTeam = mBonusSituation;
            }
        } else {
            if (mFoulsLeftTeam > 0) {
                mFoulsLeftTeam--;
                if (mFoulsLeftTeam < mBonusSituation) {
                    tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                } else {
                    tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        }
        tvFoulsLeft.setText(String.valueOf(mFoulsLeftTeam));
    }

    /**
     * Adds or subtract a foul to the guest team
     *
     * @param view
     */
    public void foulsRightClicked(View view) {
        if (view.getId() == R.id.bt_foul_right) {
            mFoulsRightTeam++;
            if (mFoulsRightTeam >= mBonusSituation) {
                tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mFoulsRightTeam = mBonusSituation;
            }
        } else {
            if (mFoulsRightTeam > 0) {
                mFoulsRightTeam--;
                if (mFoulsRightTeam < mBonusSituation) {
                    tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                } else {
                    tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        }
        tvFoulsRight.setText(String.valueOf(mFoulsRightTeam));
    }

    public void quarterButtonClicked(View view) {
        if (view.getId() == R.id.bt_minus_quarter && mQuarter > 1) {
            mQuarter--;
        } else if (view.getId() == R.id.bt_plus_quarter && mQuarter < 5) {
            mQuarter++;
        }
        displayQuarter();
    }

    public void displayQuarter() {
        if (mQuarter == 5) {
            mQuarterTv.setText(getString(R.string.over_time));
        } else {
            mQuarterTv.setText(String.valueOf(mQuarter) + getString(R.string.quarter));
        }
    }

    /**
     * Convert minutes to milliseconds
     *
     * @param minutes - input from user
     * @return the minutes in milliseconds
     */
    private Long minutesToMilliseconds(String minutes) {
        double minutesInDouble = Double.parseDouble(minutes);
        return (long) (60000 * minutesInDouble);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (mReceiver.isConnected(this)) {
                    if (mInterstitial.isLoaded()) {
                        mItemMenuSelected = R.id.save;
                        saveGame();
                    }
                    return true;
                } else {
                    Toast.makeText(DisplayActivity.this, getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
                }
            case R.id.share:
                if (mReceiver.isConnected(this)) {
                    if (mInterstitial.isLoaded()) {
                        mItemMenuSelected = R.id.share;
                        mInterstitial.show();
                    }
                    return true;
                } else {
                    Toast.makeText(DisplayActivity.this, getString(R.string.connect_to_internet), Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGame() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String date = dateFormat.format(currentDate);
        String homeTeam = mTvLeftTeam.getText().toString();
        String guestTeam = mTvRightTeam.getText().toString();
        String homePoints = String.valueOf(mScoreLeftTeam);
        String guestPoints = String.valueOf(mScoreRightTeam);
        mMatchToSave = new Match(0, date, homeTeam, guestTeam, homePoints, guestPoints);

        mInterstitial.show();

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
    public void onConnectionChange(boolean isConnected) {
        if (mReceiver.isConnected(this)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitial.loadAd(adRequest);
        }
    }

    /**
     * Publish match result in facebook
     */
    private void shareOnFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(getString(R.string.result) + " " + String.valueOf(mQuarter) + getString(R.string.quarter) + "\n" + mHomeName + " - " + String.valueOf(mScoreLeftTeam) + " | " + mGuestName + " - " + String.valueOf(mScoreRightTeam))
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
