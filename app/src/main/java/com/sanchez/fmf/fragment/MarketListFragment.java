package com.sanchez.fmf.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.MarketAdapter;
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

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static MarketListFragment newInstance() {
        MarketListFragment fragment = new MarketListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public MarketListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_list, container, false);
        ButterKnife.bind(this, v);

        mLayoutManager = new LinearLayoutManager(getContext());
        mMarketList.setLayoutManager(mLayoutManager);

        ArrayList<MarketListItemModel> mMarkets = MarketUtils.getExampleMarkets();

        mAdapter = new MarketAdapter(mMarkets);
        mMarketList.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mMarketList.postDelayed(new Runnable() {
            @Override
            public void run() {
                int shortAnimation = getResources().getInteger(android.R.integer.config_shortAnimTime);
                ViewUtils.crossfadeTwoViews(mMarketList, mSuggestion, shortAnimation);
            }
        }, 3000);
    }
}
