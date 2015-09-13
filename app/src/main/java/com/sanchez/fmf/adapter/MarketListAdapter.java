package com.sanchez.fmf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanchez.fmf.R;
import com.sanchez.fmf.event.MarketClickEvent;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.util.MarketUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder> {

    private ArrayList<MarketListItemModel> mDataset;
    private boolean mDisplayDistance;

    public MarketListAdapter(ArrayList<MarketListItemModel> dataSet, boolean displayDistance) {
        mDataset = dataSet;
        mDisplayDistance = displayDistance;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_market, parent, false);

        view.setOnClickListener((v) -> EventBus.getDefault().post(new MarketClickEvent(v)));

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = mDataset.get(position).getName();
        String dist = MarketUtils.getDistanceFromMarketString(name);

        holder.mMarketName.setText(name.substring(dist.length() + 1));

        if(mDisplayDistance) {
            holder.mDistance.setText(dist);
            holder.mDistance.setVisibility(View.VISIBLE);
            holder.mMiles.setVisibility(View.VISIBLE);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<MarketListItemModel> getDataSet() {
        return mDataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.market_name)
        TextView mMarketName;
        @Bind(R.id.market_distance)
        TextView mDistance;
        @Bind(R.id.miles)
        TextView mMiles;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}