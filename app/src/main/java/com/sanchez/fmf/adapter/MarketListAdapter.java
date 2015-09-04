package com.sanchez.fmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanchez.fmf.R;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.util.MarketUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder> {
    private ArrayList<MarketListItemModel> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MarketListAdapter(ArrayList<MarketListItemModel> dataSet) {
        mDataset = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_market, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = mDataset.get(position).getName();
        String dist = MarketUtils.getDistanceFromName(name);

        holder.mMarketName.setText(name.substring(dist.length() + 1));

        holder.mDistance.setText(dist);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.market_name)
        TextView mMarketName;
        @Bind(R.id.market_distance)
        TextView mDistance;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}