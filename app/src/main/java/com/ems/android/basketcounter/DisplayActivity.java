package com.ems.android.basketcounter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.ems.android.basketcounter.data.GameDbContract.GameEntry;
import com.ems.android.basketcounter.data.GameDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayActivity extends AppCompatActivity {
    private int mScoreLeftTeam = 0;
    private int mScoreRightTeam = 0;
    private int mFoulsLeftTeam = 0;
    private int mFoulsRightTeam = 0;
    private long mQuarterTime = 0;
    private int mBonusSituation = 0;
    private int mOverBonus = 0;

    private TextView mTvLeftTeam;
    private TextView mTvRightTeam;
    private TextView mTvTimer;
    private TextView tvFoulsLeft;
    private TextView tvFoulsRight;
    private ImageButton btPauseTimer;
    private ImageButton btPlayTimer;

    private CountDownTimer mCountDownTimer;
    private long mTimeRemaining;
    private boolean isTimerPaused;
    private GameDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();

        mDbHelper = new GameDbHelper(this);

        mTvLeftTeam = findViewById(R.id.tv_left_team);
        mTvLeftTeam.setText(intent.getExtras().getString(getString(R.string.left_team), getString(R.string.left_team)));

        mTvRightTeam = findViewById(R.id.tv_right_team);
        mTvRightTeam.setText(intent.getExtras().getString(getString(R.string.right_team), getString(R.string.right_team)));

        tvFoulsLeft = findViewById(R.id.tv_foul_left);
        tvFoulsRight = findViewById(R.id.tv_foul_right);
        mBonusSituation = Integer.parseInt(intent.getExtras().getString(getString(R.string.bonus_situation), "4"));
        mOverBonus = mBonusSituation + 1;

        mTvTimer = findViewById(R.id.tv_quarter_time_display);
        String timeString = intent.getExtras().getString(getString(R.string.time_per_quarter), "10");
        mQuarterTime = minutesToMilliseconds(timeString);
        mTvTimer.setText(formatTime(mQuarterTime));

        btPauseTimer = findViewById(R.id.bt_pause_timer);
        btPauseTimer.setVisibility(View.INVISIBLE);
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
                mTvTimer.setText(formatTime(0));
                btPlayTimer.setVisibility(View.INVISIBLE);
                btPauseTimer.setVisibility(View.INVISIBLE);
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
     * Displays the score or the fouls at the moment
     *
     * @param score/fouls - current number of points/fouls
     */
    private void displayScoreLeftTeam(int score) {
        TextView scoreView = findViewById(R.id.tv_score_left);
        scoreView.setText(String.valueOf(score));
    }

    private void displayScoreRightTeam(int score) {
        TextView scoreView = findViewById(R.id.tv_score_right);
        scoreView.setText(String.valueOf(score));
    }

    private void displayFoulsLeftTeam(int fouls) {
        tvFoulsLeft.setText(String.valueOf(fouls));
    }

    private void displayFoulsRightTeam(int fouls) {
        tvFoulsRight.setText(String.valueOf(fouls));
    }

    /**
     * Adds the specific number of points/fouls
     *
     * @param view
     */
    public void threePointLeftClicked(View view) {
        mScoreLeftTeam += 3;
        displayScoreLeftTeam(mScoreLeftTeam);
    }

    public void twoPointLeftClicked(View view) {
        mScoreLeftTeam += 2;
        displayScoreLeftTeam(mScoreLeftTeam);
    }

    public void freeThrowLeftClicked(View view) {
        mScoreLeftTeam++;
        displayScoreLeftTeam(mScoreLeftTeam);
    }

    public void minusPointLeftClicked(View view) {
        if (mScoreLeftTeam > 0) {
            mScoreLeftTeam--;
            displayScoreLeftTeam(mScoreLeftTeam);
        }
    }

    public void foulsLeftClicked(View view) {
        mFoulsLeftTeam++;
        if (mFoulsLeftTeam == mBonusSituation) {
            tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            displayFoulsLeftTeam(mFoulsLeftTeam);
        } else if (mFoulsLeftTeam > mBonusSituation) {
            tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            mFoulsLeftTeam = mOverBonus;
            displayFoulsLeftTeam(mOverBonus);
        } else {
            displayFoulsLeftTeam(mFoulsLeftTeam);
        }

    }

    public void minusFoulLeftClicked(View view) {
        if (mFoulsLeftTeam > 0) {
            mFoulsLeftTeam--;
            if (mFoulsLeftTeam == mBonusSituation) {
                tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            } else {
                tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorAccentLight));
            }
        }
        displayFoulsLeftTeam(mFoulsLeftTeam);
    }

    public void threePointRightClicked(View view) {
        mScoreRightTeam += 3;
        displayScoreRightTeam(mScoreRightTeam);
    }

    public void twoPointRightClicked(View view) {
        mScoreRightTeam += 2;
        displayScoreRightTeam(mScoreRightTeam);
    }

    public void freeThrowRightClicked(View view) {
        mScoreRightTeam++;
        displayScoreRightTeam(mScoreRightTeam);
    }

    public void minusPointRightClicked(View view) {
        if (mScoreRightTeam > 0) {
            mScoreRightTeam--;
            displayScoreRightTeam(mScoreRightTeam);
        }
    }

    public void foulsRightClicked(View view) {
        mFoulsRightTeam++;
        if (mFoulsRightTeam == mBonusSituation) {
            tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            displayFoulsRightTeam(mFoulsRightTeam);
        } else if (mFoulsRightTeam > mBonusSituation) {
            tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            mFoulsRightTeam = mOverBonus;
            displayFoulsRightTeam(mOverBonus);
        } else {
            displayFoulsRightTeam(mFoulsRightTeam);
        }

    }

    public void minusFoulRightClicked(View view) {
        if (mFoulsRightTeam > 0) {
            mFoulsRightTeam--;
            if (mFoulsRightTeam == mBonusSituation) {
                tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            } else {
                tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorAccentLight));
            }
        }
        displayFoulsRightTeam(mFoulsRightTeam);
    }

    /**
     * Reset points and fouls
     *
     * @param view
     */
    public void reset(View view) {
        mScoreLeftTeam = 0;
        mScoreRightTeam = 0;
        mFoulsLeftTeam = 0;
        mFoulsRightTeam = 0;
        displayScoreLeftTeam(mScoreLeftTeam);
        displayScoreRightTeam(mScoreRightTeam);
        displayFoulsLeftTeam(mFoulsLeftTeam);
        displayFoulsRightTeam(mFoulsRightTeam);
        tvFoulsLeft.setBackgroundColor(getResources().getColor(R.color.colorAccentLight));
        tvFoulsRight.setBackgroundColor(getResources().getColor(R.color.colorAccentLight));
        restartTimer(view);
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
                saveGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGame() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String date = dateFormat.format(currentDate);

        ContentValues values = new ContentValues();
        values.put(GameEntry.COLUMN_DATE, date);
        values.put(GameEntry.COLUMN_HOME_TEAM, mTvLeftTeam.toString());
        values.put(GameEntry.COLUMN_GUEST_TEAM, mTvRightTeam.toString());
        values.put(GameEntry.COLUMN_HOME_POINTS, String.valueOf(mScoreLeftTeam));
        values.put(GameEntry.COLUMN_GUEST_POINTS, String.valueOf(mScoreRightTeam));

        long newRowId = db.insert(GameEntry.TABLE_NAME, null, values);

        if (newRowId > -1) {
            Toast.makeText(DisplayActivity.this, getString(R.string.game_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DisplayActivity.this, getString(R.string.error_saving_game), Toast.LENGTH_SHORT).show();
        }
    }
}
