package com.sanchez.fmf.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sanchez.fmf.MarketDetailActivity;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.event.GetMarketListSuccessEvent;
import com.sanchez.fmf.event.PlaceTitleResolvedEvent;
import com.sanchez.fmf.model.MarketListItemModel;

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
    @Bind(R.id.map_market_details_popup)
    View mDetailsPopup;
    @Bind(R.id.open_in_maps_button)
    Button mOpenInMapsButton;
    @Bind(R.id.view_market_details_button)
    Button mViewMarketDetailsButton;

    private Bundle mBundle;
    private GoogleMap mMap;

    private double[] mCoordinates = null;
    private String mPlaceTitle = null;

    private MarketListItemModel mCurrentMarket;
    private double[] mCurrentMarketCoordinates;

    private List<MarketListItemModel> mMarkets;

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
        mToolbar.setBackgroundColor(Color.argb(200, Color.red(color), Color.green(color), Color.blue(color)));

        if (null != mPlaceTitle) {
            mToolbar.setTitle(mPlaceTitle);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        mToolbar.setNavigationOnClickListener((v) -> {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        });

        mOpenInMapsButton.setOnClickListener((v) -> {
            double x = mCurrentMarket.getX();
            double y = mCurrentMarket.getY();

            Uri gmmIntentUri = Uri.parse("geo:" + y + "," + x + "?q=" + Uri.encode(mCurrentMarket.getName()));

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Snackbar.make(mMapView, R.string.no_maps_installed, Snackbar.LENGTH_SHORT).show();
            }
        });

        mViewMarketDetailsButton.setOnClickListener((v) -> {
            Intent i = new Intent(getActivity(), MarketDetailActivity.class);
            i.putExtra(MarketDetailActivity.EXTRA_MARKET_ID, mCurrentMarket.getId());
            i.putExtra(MarketDetailActivity.EXTRA_MARKET_NAME, mCurrentMarket.getName());
            startActivity(i);
        });

        mMapView.getMapAsync(this);

        return view;
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

        for (int i = 0; i < mMarkets.size(); i++) {
            double[] coords = { mMarkets.get(i).getY(), mMarkets.get(i).getX() };

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
                    .title(mMarkets.get(i).getName())
                    .position(new LatLng(coords[0], coords[1])));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                        new LatLng(lowestLat, lowestLng),
                        new LatLng(highestLat, highestLng)),
                100));

        mMap.setOnMarkerClickListener((marker) -> {
            mDetailsPopup.setVisibility(View.VISIBLE);
            double lat = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;

            for (int i = 0; i < mMarkets.size(); i++) {
                double[] coords = { mMarkets.get(i).getY(), mMarkets.get(i).getX() };
                if (lat == coords[0] && lng == coords[1]) {
                    mCurrentMarket = mMarkets.get(i);
                    mCurrentMarketCoordinates = coords;
                }
            }
            return false;
        });

        mMapView.setVisibility(View.VISIBLE);
    }

    public void onEvent(GetMarketListSuccessEvent event) {
        mMarkets = event.getMarketList().getMarkets();
        EventBus.getDefault().removeStickyEvent(GetMarketListSuccessEvent.class);
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
