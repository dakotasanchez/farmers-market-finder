package com.sanchez.fmf;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.sanchez.fmf.event.GetMarketListFailEvent;
import com.sanchez.fmf.event.GetMarketListSuccessEvent;
import com.sanchez.fmf.event.RetryGetMarketListEvent;
import com.sanchez.fmf.fragment.MarketListFragment;
import com.sanchez.fmf.model.MarketListModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketListActivity extends AppCompatActivity {

    public static final String TAG = MarketListActivity.class.getSimpleName();

    public static final String EXTRA_COORDINATES = "com.sanchez.extra_coordinates";
    public static final String EXTRA_PLACE_TITLE = "com.sanchez.extra_place_title";
    public static final String EXTRA_PLACE_ID = "com.sanchez.extra_place_id";
    public static final String EXTRA_USED_DEVICE_COORDINATES = "com.sanchez.extra_used_device_coordinates";

    // service for USDA API
    private MarketService mMarketService;

    private double[] mCoordinates;

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

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        RestClient client = new RestClient();
        mMarketService = client.getMarketService();
        retrieveMarkets();

        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentById(R.id.container_market_list_activity);

        if (listFragment == null) {
            listFragment = MarketListFragment.newInstance(mCoordinates, placeTitle, placeId, usedDeviceCoordinates);
            fm.beginTransaction()
                    .add(R.id.container_market_list_activity, listFragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void retrieveMarkets() {
        /**
         * get markets from USDA API
         * coordinates[0] is latitude
         * coordinates[1] is longitude
         */
        mMarketService.getMarkets(mCoordinates[0], mCoordinates[1], new Callback<MarketListModel>() {
            @Override
            public void success(MarketListModel marketListModel, Response response) {
                EventBus.getDefault().postSticky(new GetMarketListSuccessEvent(marketListModel));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().postSticky(new GetMarketListFailEvent());
                Log.e(TAG, error.toString());
            }
        });
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

    public void onEvent(RetryGetMarketListEvent event) {
        retrieveMarkets();
    }
}
