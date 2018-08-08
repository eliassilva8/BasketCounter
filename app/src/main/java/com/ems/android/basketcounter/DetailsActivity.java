package com.ems.android.basketcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ems.android.basketcounter.data.GamePOJO;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        GamePOJO game = intent.getParcelableExtra(getString(R.string.game_clicked));

        mDate.setText(game.getDate());
        mHomeName.setText(game.getHomeTeamName());
        mGuestName.setText(game.getGuestTeamName());
        mHomeScore.setText(game.getHomeTeamPoints());
        mGuestScore.setText(game.getGuestTeamPoints());

        int homeScore = Integer.parseInt(game.getHomeTeamPoints());
        int guestScore = Integer.parseInt(game.getGuestTeamPoints());
        if (homeScore > guestScore) {
            mWinnerString.setText(game.getHomeTeamName() + " " + getString(R.string.won));
        } else if (homeScore < guestScore) {
            mWinnerString.setText(game.getGuestTeamName() + " " + getString(R.string.won));
        } else {
            mWinnerString.setText(getString(R.string.draw));
        }
    }
}
