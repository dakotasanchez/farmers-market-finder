package com.sanchez.fmf.fragment;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sanchez.fmf.MarketDetailActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.application.FMFApplication;
import com.sanchez.fmf.event.GetMarketDetailFailEvent;
import com.sanchez.fmf.event.GetMarketDetailSuccessEvent;
import com.sanchez.fmf.event.RetryGetMarketDetailEvent;
import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.util.MarketUtils;
import com.sanchez.fmf.util.ViewUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketDetailFragment extends Fragment {

    public static final String TAG = MarketDetailFragment.class.getSimpleName();

    @Bind(R.id.progress_bar)
    View mProgressBar;
    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;
    @Bind(R.id.layout_try_again)
    ViewGroup mTryAgain;
    @Bind(R.id.try_again_button)
    Button mTryAgainButton;
    @Bind(R.id.layout_market_details)
    View detailsLayout;

    @Bind(R.id.test)
    TextView test;

    private static int MED_ANIM_TIME;
    private String mMarketId;
    private String mMarketName;

    private MarketDetailModel mMarket;

    public MarketDetailFragment() {
    }

    public static MarketDetailFragment newInstance(String id, String name) {
        MarketDetailFragment fragment = new MarketDetailFragment();
        Bundle args = new Bundle();
        args.putString(MarketDetailActivity.EXTRA_MARKET_ID, id);
        args.putString(MarketDetailActivity.EXTRA_MARKET_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MED_ANIM_TIME = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        if (getArguments() != null) {
            mMarketId = getArguments().getString(MarketDetailActivity.EXTRA_MARKET_ID);
            mMarketName = getArguments().getString(MarketDetailActivity.EXTRA_MARKET_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_detail, container, false);
        ButterKnife.bind(this, view);

        // set backgroundTint programmatically as xml method is undefined...
        int pureWhite = getResources().getColor(R.color.pure_white);
        ColorStateList cSL2 = new ColorStateList(new int[][]{new int[0]}, new int[]{pureWhite});
        ((AppCompatButton)mTryAgainButton).setSupportBackgroundTintList(cSL2);

        setupFAB();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTryAgainButton.setOnClickListener((v) -> {
            mProgressBar.setVisibility(View.VISIBLE);
            mTryAgain.setVisibility(View.GONE);
            EventBus.getDefault().post(new RetryGetMarketDetailEvent());
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void setupFAB() {
        int marketColor = getResources().getColor(MarketUtils.getRandomMarketColor());
        ColorStateList cSL = new ColorStateList(new int[][]{new int[0]}, new int[]{marketColor});
        mFavoriteFab.setBackgroundTintList(cSL);

        LinkedHashMap<String, String> favoriteMarkets =
                FMFApplication.getGlobalPreferences().getFavoriteMarkets();

        if (null != favoriteMarkets && favoriteMarkets.containsKey(mMarketId)) {
            mFavoriteFab.setEnabled(false);
            mFavoriteFab.setBackgroundTintList(
                    new ColorStateList(new int[][]{new int[0]}, new int[]{Color.GRAY})
            );
        }

        mFavoriteFab.setOnClickListener((v) -> {

            Snackbar.make(v, R.string.added_to_favorites, Snackbar.LENGTH_LONG).show();
            mFavoriteFab.setEnabled(false);
            mFavoriteFab.setBackgroundTintList(
                    new ColorStateList(new int[][]{new int[0]}, new int[]{Color.GRAY})
            );

            // update favorite markets list
            LinkedHashMap<String, String> favorites = FMFApplication
                    .getGlobalPreferences()
                    .getFavoriteMarkets();
            if(null == favorites) {
                favorites = new LinkedHashMap<>();
            }
            favorites.put(mMarketId, mMarketName);

            FMFApplication.getGlobalPreferences().setFavoriteMarkets(favorites);
        });
    }

    private void showMarket(MarketDetailModel market) {

        test.setText(market.getMarket_name());

        ViewUtils.crossfadeTwoViews(detailsLayout, mProgressBar, MED_ANIM_TIME);
    }

    public void onEvent(GetMarketDetailSuccessEvent event) {
        mMarket = event.getMarketDetail().getMarketdetails();
        showMarket(mMarket);
    }

    public void onEvent(GetMarketDetailFailEvent event) {
        mProgressBar.setVisibility(View.GONE);
        mTryAgain.setVisibility(View.VISIBLE);
        EventBus.getDefault().removeStickyEvent(GetMarketDetailFailEvent.class);
    }
}
