package com.ems.android.basketcounter.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ems.android.basketcounter.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elias on 20/11/2018.
 */
public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MatchViewHolder> {
    private List<Match> mMatches;
    final private MatchAdapterOnClickHandler mClickHandler;
    private final LayoutInflater mInflater;

    public MatchListAdapter(Context context, MatchAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
        mInflater = LayoutInflater.from(context);
    }

    public interface MatchAdapterOnClickHandler {
        void onClick(Match match);
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.match_item, parent, false);
        return new MatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        if (mMatches != null) {
            Match current = mMatches.get(position);
            holder.mDateItem.setText(current.getDate());
            holder.mHomeNameItem.setText(current.getHomeTeamName());
            holder.mGuestNameItem.setText(current.getGuestTeamName());
            holder.mHomePointsItem.setText(current.getHomeTeamPoints());
            holder.mGuestPointsItem.setText(current.getGuestTeamPoints());
        } else {
            // Covers the case of data not being ready yet.
            holder.mDateItem.setText("No match");
        }
    }

    public void setMatchData(List<Match> matches) {
        mMatches = matches;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMatches != null) {
            return mMatches.size();
        } else {
            return 0;
        }
    }

    class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.date_lv)
        TextView mDateItem;
        @BindView(R.id.home_team_name_lv)
        TextView mHomeNameItem;
        @BindView(R.id.guest_team_name_lv)
        TextView mGuestNameItem;
        @BindView(R.id.home_team_points_lv)
        TextView mHomePointsItem;
        @BindView(R.id.guest_team_points_lv)
        TextView mGuestPointsItem;

        private MatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Match matchClicked = mMatches.get(getAdapterPosition());
            mClickHandler.onClick(matchClicked);
        }
    }
}
