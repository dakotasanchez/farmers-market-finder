package com.sanchez.fmf.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.MarketListAdapter;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.util.MarketUtils;
import com.sanchez.fmf.util.ViewUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarketListFragment extends Fragment {

    @Bind(R.id.market_list)
    RecyclerView mMarketList;
    @Bind(R.id.fragment_default_suggestion)
    View mSuggestion;
    @Bind(R.id.progress_bar)
    View mProgressBar;

    private RecyclerView.Adapter mAdapter;

    private double[] coordinates = new double[2];

    public static MarketListFragment newInstance(double[] coords) {
        MarketListFragment fragment = new MarketListFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MarketListActivity.EXTRA_COORDINATES, coords);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coordinates = getArguments().getDoubleArray(MarketListActivity.EXTRA_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_list, container, false);
        ButterKnife.bind(this, v);

        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getContext());
        mMarketList.setLayoutManager(linearLM);

        ArrayList<MarketListItemModel> mMarkets = MarketUtils.getExampleMarkets();

        mAdapter = new MarketListAdapter(mMarkets);
        mMarketList.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Snackbar.make(getView(), "lat = " + coordinates[0] + " lon = " + coordinates[1], Snackbar.LENGTH_LONG).show();

        mMarketList.postDelayed(() -> {
            int shortAnimation = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            ViewUtils.crossfadeTwoViews(mMarketList, mProgressBar, shortAnimation);
        }, 3000);
    }
}
