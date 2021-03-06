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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder> {

    private ArrayList<MarketListItemModel> mDataset;
    private boolean mDisplayDistance;

    public MarketListAdapter(ArrayList<MarketListItemModel> dataset, boolean displayDistance) {
        mDataset = dataset;
        mDisplayDistance = displayDistance;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_market, parent, false);

        view.setOnClickListener((v) -> EventBus.getDefault().post(new MarketClickEvent(v)));

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = mDataset.get(position).getName();
        String dist = mDataset.get(position).getDistance();
        dist = dist.substring(0, 4);

        holder.mMarketName.setText(name);

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
        @BindView(R.id.market_name)
        TextView mMarketName;
        @BindView(R.id.market_distance)
        TextView mDistance;
        @BindView(R.id.miles)
        TextView mMiles;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}