package com.sanchez.fmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanchez.fmf.R;
import com.sanchez.fmf.event.FavoriteClickEvent;
import com.sanchez.fmf.event.FavoriteRemoveEvent;
import com.sanchez.fmf.model.FavoriteMarketModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by dakota on 9/13/15.
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    private ArrayList<FavoriteMarketModel> mDataset;

    public FavoriteListAdapter(ArrayList<FavoriteMarketModel> dataset) {
        mDataset = dataset;
    }

    @Override
    public FavoriteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_favorite_market, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = mDataset.get(position).getId();
        String name = mDataset.get(position).getName();

        holder.mMarketName.setText(name);

        holder.mMarketName.setOnClickListener((v) -> {
            EventBus.getDefault().post(new FavoriteClickEvent(id, name));
        });

        holder.mMarketRemove.setOnClickListener((v) -> {
           EventBus.getDefault().post(new FavoriteRemoveEvent(id));
        });

        if (position == getItemCount() - 1) {
            holder.mSeparator.setVisibility(View.GONE);
        } else {
            holder.mSeparator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<FavoriteMarketModel> getDataSet() {
        return mDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.favorite_market_name)
        TextView mMarketName;
        @Bind(R.id.favorite_market_remove)
        ImageView mMarketRemove;
        @Bind(R.id.favorite_market_separator)
        View mSeparator;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
