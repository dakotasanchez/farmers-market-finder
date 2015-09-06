package com.sanchez.fmf.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.MarketListAdapter;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.model.MarketListModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;
import com.sanchez.fmf.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketListFragment extends Fragment {

    public static final String TAG = MarketListFragment.class.getSimpleName();

    @Bind(R.id.market_list)
    RecyclerView mMarketList;
    @Bind(R.id.fragment_default_suggestion)
    View mSuggestion;
    @Bind(R.id.progress_bar)
    View mProgressBar;

    private RecyclerView.Adapter mAdapter;

    // service for USDA API
    private MarketService mMarketService;

    private double[] coordinates = new double[2];

    // grab coordinates sent from MainFragment intent
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

        RestClient client = new RestClient();
        mMarketService = client.getMarketService();

        if (getArguments() != null) {
            coordinates = getArguments().getDoubleArray(MarketListActivity.EXTRA_COORDINATES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_list, container, false);
        ButterKnife.bind(this, v);

        // linear RecyclerView
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getContext());
        mMarketList.setLayoutManager(linearLM);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * get markets from USDA API
         * coordinates[0] is latitude
         * coordinates[1] is longitude
         */
        mMarketService.getMarkets(coordinates[0], coordinates[1], new Callback<MarketListModel>() {
            @Override
            public void success(MarketListModel marketListModel, Response response) {
                showMarkets(marketListModel.getMarkets());
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(getView(), "Failed to get markets!", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void showMarkets(List<MarketListItemModel> markets) {
        mAdapter = new MarketListAdapter(new ArrayList<>(markets));
        mMarketList.setAdapter(mAdapter);

        int shortAnimation = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        ViewUtils.crossfadeTwoViews(mMarketList, mProgressBar, shortAnimation);
    }
}
