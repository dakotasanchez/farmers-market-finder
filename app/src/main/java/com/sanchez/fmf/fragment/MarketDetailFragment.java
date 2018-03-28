package com.sanchez.fmf.fragment;


import android.app.Activity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketDetailFragment extends Fragment {

    public static final String TAG = MarketDetailFragment.class.getSimpleName();

    @BindView(R.id.progress_bar)
    View mProgressBar;
    @BindView(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;
    @BindView(R.id.layout_try_again)
    ViewGroup mTryAgain;
    @BindView(R.id.try_again_button)
    Button mTryAgainButton;
    @BindView(R.id.layout_market_details)
    View detailsLayout;

    @BindView(R.id.market_name)
    TextView marketName;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.dates)
    TextView dates;
    @BindView(R.id.times)
    TextView times;
    @BindView(R.id.last_update_time)
    TextView lastUpdateTime;
    @BindView(R.id.website)
    TextView website;
    @BindView(R.id.facebook)
    TextView facebook;
    @BindView(R.id.twitter)
    TextView twitter;
    @BindView(R.id.other_media)
    TextView otherMedia;
    @BindView(R.id.credit)
    TextView credit;
    @BindView(R.id.organic)
    TextView organic;
    @BindView(R.id.snap)
    TextView snap;
    @BindView(R.id.sfmnp)
    TextView sfmnp;
    @BindView(R.id.wic)
    TextView wic;
    @BindView(R.id.wic_cash)
    TextView wicCash;


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

        Activity parent = getActivity();

        String sMarketName = parent.getString(R.string.market_name) + market.getMarket_name();
        String sAddress = parent.getString(R.string.address)
                + market.getStreet() + ", " + market.getCity() + ", "
                + market.getState() + ", " + market.getZipcode();
        String sLocation  = parent.getString(R.string.location) + (market.getLocation() == null ? "" : market.getLocation());
        String sDates = parent.getString(R.string.dates) + (market.getSeason_1_date() == null ? "" : market.getSeason_1_date());
        String sTimes = parent.getString(R.string.times) + (market.getSeason_1_time() == null ? "" : market.getSeason_1_time());
        String sLastUpdateTime = parent.getString(R.string.last_update_time) + (market.getUpdate_time() == null ? "" : market.getUpdate_time());
        String sWebsite = parent.getString(R.string.website) + (market.getWebsite() == null ? "" : market.getWebsite());
        String sFacebook = parent.getString(R.string.facebook) + (market.getFacebook() == null ? "" : market.getFacebook());
        String sTwitter = parent.getString(R.string.twitter) + (market.getTwitter() == null ? "" : market.getTwitter());
        String sOtherMedia = parent.getString(R.string.other_media) + (market.getOther_media() == null ? "" : market.getOther_media());
        String sCredit = parent.getString(R.string.credit) + (market.getCredit() == null ? "" : market.getCredit());
        String sOrganic = parent.getString(R.string.organic) + (market.getOrganic() == null ? "" : market.getOrganic());
        String sSnap = parent.getString(R.string.snap) + (market.getSnap() == null ? "" : market.getSnap());
        String sSfmnp = parent.getString(R.string.sfmnp) + (market.getSfmnp() == null ? "" : market.getSfmnp());
        String sWic = parent.getString(R.string.wic) + (market.getWic() == null ? "" : market.getWic());
        String sWicCash = parent.getString(R.string.wic_cash) + (market.getWic_cash() == null ? "" : market.getWic_cash());

        marketName.setText(sMarketName);
        address.setText(sAddress);
        location.setText(sLocation);
        dates.setText(sDates);
        times.setText(sTimes);
        lastUpdateTime.setText(sLastUpdateTime);
        website.setText(sWebsite);
        facebook.setText(sFacebook);
        twitter.setText(sTwitter);
        otherMedia.setText(sOtherMedia);
        credit.setText(sCredit);
        organic.setText(sOrganic);
        snap.setText(sSnap);
        sfmnp.setText(sSfmnp);
        wic.setText(sWic);
        wicCash.setText(sWicCash);

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
