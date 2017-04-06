package com.sanchez.fmf;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.sanchez.fmf.event.GetMarketListFailEvent;
import com.sanchez.fmf.event.GetMarketListSuccessEvent;
import com.sanchez.fmf.event.MapFABClickEvent;
import com.sanchez.fmf.event.PlaceTitleResolvedEvent;
import com.sanchez.fmf.event.RetryGetMarketListEvent;
import com.sanchez.fmf.fragment.MarketListFragment;
import com.sanchez.fmf.fragment.MarketMapFragment;
import com.sanchez.fmf.model.MarketListResponseModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketListActivity extends AppCompatActivity {

    public static final String TAG = MarketListActivity.class.getSimpleName();

    public static final String EXTRA_COORDINATES = "com.sanchez.fmf.extra_coordinates";
    public static final String EXTRA_PLACE_TITLE = "com.sanchez.fmf.extra_place_title";
    public static final String EXTRA_PLACE_ID = "com.sanchez.fmf.extra_place_id";
    public static final String EXTRA_USED_DEVICE_COORDINATES = "com.sanchez.fmf.extra_used_device_coordinates";
    public static final String EXTRA_DISTANCE_CONSTRAINT = "com.sanchez.fmf.distance_constraint";

    // service for API
    private MarketService mMarketService;

    private double[] mCoordinates;
    private String mPlaceTitle;
    private int mDistanceConstraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);
        ButterKnife.bind(this);

        // get coordinates from MainFragment intent to pass to MarketListFragment
        mCoordinates = getIntent().getDoubleArrayExtra(EXTRA_COORDINATES);
        String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        String placeTitle = getIntent().getStringExtra(EXTRA_PLACE_TITLE);
        boolean usedDeviceCoordinates = getIntent().getBooleanExtra(EXTRA_USED_DEVICE_COORDINATES, false);
        mDistanceConstraint = Integer.parseInt(getIntent().getStringExtra(EXTRA_DISTANCE_CONSTRAINT));

        // color nav bar with app color
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        if (usedDeviceCoordinates) {
            // user clicked "use current location button", meaning we need to reverse geocode
            getLocationName(mCoordinates, this);
        }
        if (null != placeTitle) {
            mPlaceTitle = placeTitle;
        }

        RestClient client = new RestClient();
        mMarketService = client.getMarketService();
        getMarkets(mDistanceConstraint);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window w = getWindow();
                    w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    if (fm.getBackStackEntryCount() == 1) {
                            w.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                    } else if (fm.getBackStackEntryCount() == 2) {
                            w.setStatusBarColor(getResources().getColor(R.color.primary_dark));
                    }
                }
            }
        });
        
        Fragment listFragment = MarketListFragment.newInstance(placeTitle, placeId, usedDeviceCoordinates);
        fm.beginTransaction()
                .add(R.id.container_market_list_activity, listFragment, MarketListFragment.TAG)
                .addToBackStack(MarketListFragment.TAG)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeStickyEvent(GetMarketListSuccessEvent.class);
        EventBus.getDefault().removeStickyEvent(PlaceTitleResolvedEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // TODO: Stuff this in a worker fragment with setRetainInstance(true)
    private void getMarkets(int distance) {
        /**
         * get markets from USDA API
         * coordinates[0] is latitude
         * coordinates[1] is longitude
         */
        // TODO: Add distance picker
        mMarketService.getMarkets(mCoordinates[1], mCoordinates[0], distance, new Callback<MarketListResponseModel>() {
            @Override
            public void success(MarketListResponseModel marketListModel, Response response) {
                EventBus.getDefault().postSticky(new GetMarketListSuccessEvent(marketListModel));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().postSticky(new GetMarketListFailEvent());
                Log.e(TAG, error.toString());
            }
        });
    }

    private void showMap() {
        MarketMapFragment frag = MarketMapFragment.newInstance(mCoordinates, mPlaceTitle);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container_market_list_activity, frag, MarketMapFragment.TAG)
                .addToBackStack(MarketMapFragment.TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // get a location name based on latitude and longitude (reverse geocoding)
    public void getLocationName(final double[] coords, Context c) {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(c, Locale.ENGLISH);
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(coords[0], coords[1], 1);
                } catch (IOException e) {
                    Log.e(TAG, "Error getting location from coordinates");
                }

                if(results == null || results.size() < 1) {
                    return getResources().getString(R.string.markets);
                }

                String result;
                if (null != results.get(0).getLocality()) {
                    result = results.get(0).getLocality();
                } else if (null != results.get(0).getSubLocality()) {
                    result = results.get(0).getSubLocality();
                } else {
                    result = getResources().getString(R.string.markets);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    mPlaceTitle = result;
                    EventBus.getDefault().postSticky(new PlaceTitleResolvedEvent(result));
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 2) {
            // Map fragment is on the stack right now, pop it off
            fm.popBackStackImmediate();
        } else {
            finish();
            //fm.popBackStackImmediate();
            //super.onBackPressed();
        }
    }

    public void onEvent(RetryGetMarketListEvent event) {
        getMarkets(mDistanceConstraint);
    }

    public void onEvent(MapFABClickEvent event) {
        showMap();
    }
}
