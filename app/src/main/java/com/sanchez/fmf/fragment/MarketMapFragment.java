package com.sanchez.fmf.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.sanchez.fmf.R;
import com.sanchez.fmf.event.MarketsDetailsRetrievedEvent;
import com.sanchez.fmf.event.PlaceTitleResolvedEvent;
import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.util.ViewUtils;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = MarketMapFragment.class.getSimpleName();

    @Bind(R.id.toolbar_market_map_fragment)
    Toolbar mToolbar;
    @Bind(R.id.market_map)
    MapView mMapView;
    @Bind(R.id.progress_bar)
    View mProgressBar;

    private Bundle mBundle;
    private GoogleMap mMap;

    private double[] mCoordinates = null;
    private List<MarketDetailModel> mMarketsDetails = null;

    public static MarketMapFragment newInstance() {
        return new MarketMapFragment();
    }

    public MarketMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
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

    /**
     * Relay lifecycle methods to MapView as per Google guidelines
     */

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_map, container, false);
        ButterKnife.bind(this, view);

        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(mBundle);

        //ColorStateList cSL = new ColorStateList(new int[][]{new int[0]}, new int[]{Color.TRANSPARENT});
        //mToolbar.setBackgroundColor(Color.parseColor("#D09E9E9E"));
        int color = getResources().getColor(R.color.primary_dark);

        mToolbar.setBackgroundColor(Color.argb(144, Color.red(color), Color.green(color), Color.blue(color)));

        mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        mToolbar.setNavigationOnClickListener((v) -> {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Rect insets = new Rect();
        Window window = getActivity().getWindow();
        try {
            Class clazz = Class.forName("com.android.internal.policy.impl.PhoneWindow");
            Field field = clazz.getDeclaredField("mDecor");
            field.setAccessible(true);
            Object decorView = field.get(window);
            Field insetsField = decorView.getClass().getDeclaredField("mFrameOffsets");
            insetsField.setAccessible(true);
            insets = (Rect) insetsField.get(decorView);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        mToolbar.setPadding(0, insets.top, 0, 0);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(mCoordinates != null && mMarketsDetails != null) {
            setUpMap();
        }
    }

    private void setUpMap() {

        LatLng marketArea = new LatLng(mCoordinates[0], mCoordinates[1]);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marketArea, 12));

        mMapView.setVisibility(View.GONE);
        ViewUtils.crossfadeTwoViews(mMapView, mProgressBar,
                getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    public void onEvent(MarketsDetailsRetrievedEvent event) {
        mCoordinates = event.getCoordinates();
        mMarketsDetails = event.getMarketDetailModels();

        if (null != mMap) {
            setUpMap();
        }
    }

    public void onEvent(PlaceTitleResolvedEvent event) {
        if(null != mToolbar) {
            mToolbar.setTitle(event.getPlaceTitle());
        }
    }
}
