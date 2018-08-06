package com.ems.android.basketcounter.data;

import android.content.Context;
import android.support.annotation.NonNull;
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
 * Created by Elias on 02/08/2018.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private List<GamePOJO> mGames;

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.mDateItem.setText(mGames.get(position).getDate());
        holder.mHomeNameItem.setText(mGames.get(position).getHomeTeamName());
        holder.mGuestNameItem.setText(mGames.get(position).getGuestTeamName());
        holder.mHomePointsItem.setText(mGames.get(position).getHomeTeamPoints());
        holder.mGuestPointsItem.setText(mGames.get(position).getGuestTeamPoints());
    }

    @Override
    public int getItemCount() {
        if (mGames == null) {
            return 0;
        }
        return mGames.size();
    }

    public void setGameData(List<GamePOJO> games) {
        mGames = games;
        notifyDataSetChanged();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {
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

        public GameViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
