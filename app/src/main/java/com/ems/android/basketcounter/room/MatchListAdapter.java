package com.ems.android.basketcounter.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ems.android.basketcounter.R;
import com.ems.android.basketcounter.databinding.MatchItemBinding;

import java.util.List;

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
        MatchItemBinding binding = DataBindingUtil.inflate(mInflater, R.layout.match_item, parent, false);
        return new MatchViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        if (mMatches != null) {
            Match current = mMatches.get(position);

            holder.mBinding.dateLv.setText(current.getDate());
            holder.mBinding.homeTeamNameLv.setText(current.getHomeTeamName());
            holder.mBinding.guestTeamNameLv.setText(current.getGuestTeamName());
            holder.mBinding.homeTeamPointsLv.setText(current.getHomeTeamPoints());
            holder.mBinding.guestTeamPointsLv.setText(current.getGuestTeamPoints());
        } else {
            // Covers the case of data not being ready yet.
            holder.mBinding.dateLv.setText("No match");
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
        private MatchItemBinding mBinding;
        private MatchViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Match matchClicked = mMatches.get(getAdapterPosition());
            mClickHandler.onClick(matchClicked);
        }
    }
}
