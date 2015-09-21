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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.event.MapClosedEvent;
import com.sanchez.fmf.event.MarketsDetailsRetrievedEvent;
import com.sanchez.fmf.event.PlaceTitleResolvedEvent;
import com.sanchez.fmf.model.MarketDetailModel;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.util.MarketUtils;
import com.sanchez.fmf.util.ViewUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MarketMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = MarketMapFragment.class.getSimpleName();

    @Bind(R.id.toolbar_market_map_fragment)
    Toolbar mToolbar;
    @Bind(R.id.market_map)
    MapView mMapView;
//    @Bind(R.id.progress_bar)
//    View mProgressBar;

    private Bundle mBundle;
    private GoogleMap mMap;

    private double[] mCoordinates = null;
    private String mPlaceTitle = null;

    private HashMap<MarketListItemModel, MarketDetailModel> mMarkets = null;

    public static MarketMapFragment newInstance(double[] coords, String placeTitle) {
        MarketMapFragment frag = new MarketMapFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MarketListActivity.EXTRA_COORDINATES, coords);
        args.putString(MarketListActivity.EXTRA_PLACE_TITLE, placeTitle);
        frag.setArguments(args);
        return frag;
    }

    public MarketMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;

        if (getArguments() != null) {
            mCoordinates = getArguments().getDoubleArray(MarketListActivity.EXTRA_COORDINATES);
            mPlaceTitle = getArguments().getString(MarketListActivity.EXTRA_PLACE_TITLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().post(new MapClosedEvent());
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

        // give primary_dark a little transparency
        int color = getResources().getColor(R.color.primary_dark);
        mToolbar.setBackgroundColor(Color.argb(144, Color.red(color), Color.green(color), Color.blue(color)));

        if (null != mPlaceTitle) {
            mToolbar.setTitle(mPlaceTitle);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        mToolbar.setNavigationOnClickListener((v) -> {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        });

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Pad toolbar because fitsSystemWindows="true" isn't respected for some reason
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
        if (null != mMarkets) {
            setUpMap();
        }
    }

    private void setUpMap() {

        double lowestLat = mCoordinates[0];
        double highestLat = mCoordinates[0];
        double lowestLng = mCoordinates[1];
        double highestLng = mCoordinates[1];

        ArrayList<MarketListItemModel> keys = new ArrayList<>(mMarkets.keySet());
        for (int i = 0; i < keys.size(); i++) {
            double[] coords = MarketUtils.getCoordinatesFromMapUrl(mMarkets.get(keys.get(i)).getMapLink());

            if (coords[0] < lowestLat) {
                lowestLat = coords[0];
            } else if (coords[0] > highestLat) {
                highestLat = coords[0];
            }
            if (coords[1] < lowestLng) {
                lowestLng = coords[1];
            } else if (coords[1] > highestLng) {
                highestLng = coords[1];
            }

            mMap.addMarker(new MarkerOptions()
                    .title(MarketUtils.getNameFromMarketString(keys.get(i).getName()))
                    .position(new LatLng(coords[0], coords[1])));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                        new LatLng(lowestLat, lowestLng),
                        new LatLng(highestLat, highestLng)),
                        100));

        mMapView.setVisibility(View.VISIBLE);
//        mMapView.setVisibility(View.GONE);
//        ViewUtils.crossfadeTwoViews(mMapView, mProgressBar,
//                getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    public void onEvent(MarketsDetailsRetrievedEvent event) {
        mMarkets = event.getMarketDetailModels();

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
