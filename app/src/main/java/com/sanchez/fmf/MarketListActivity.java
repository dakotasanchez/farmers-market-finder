package com.sanchez.fmf;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.sanchez.fmf.fragment.MarketListFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarketListActivity extends AppCompatActivity {

    public static final String EXTRA_COORDINATES = "com.sanchez.extra_coordinates";
    public static final String EXTRA_PLACE_TITLE = "com.sanchez.extra_place_title";
    public static final String EXTRA_PLACE_ID = "com.sanchez.extra_place_id";
    public static final String EXTRA_CALCULATE_DISTANCES = "com.sanchez.extra_calculate_distances";

    @Bind(R.id.toolbar_market_list_activity)
    Toolbar mToolbar;

    public abstract class OnGetLocationFinishedListener {
        public abstract void onFinished(String result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);
        ButterKnife.bind(this);

        // get coordinates from MainFragment intent to pass to MarketListFragment
        double[] coordinates = getIntent().getDoubleArrayExtra(EXTRA_COORDINATES);
        String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        String placeTitle = getIntent().getStringExtra(EXTRA_PLACE_TITLE);

        // flag whether to rely on distances returned from USDA API
        boolean needToCalculateDistances = true;

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (null != placeTitle) {
            getSupportActionBar().setTitle(placeTitle);
        } else {
            // user clicked "use current location button", meaning we need to reverse geocode
            getLocationName(coordinates, new OnGetLocationFinishedListener() {
                @Override
                public void onFinished(String result) {
                    getSupportActionBar().setTitle(result);
                }
            });
            needToCalculateDistances = false;
        }

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(getResources().getColor(R.color.primary_dark));
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentById(R.id.container_market_list_activity);

        if (listFragment == null) {
            listFragment = MarketListFragment.newInstance(coordinates, placeId, needToCalculateDistances);
            fm.beginTransaction()
                    .add(R.id.container_market_list_activity, listFragment)
                    .commit();
        }
    }

    public void getLocationName(final double[] coords, final OnGetLocationFinishedListener listener) {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(getBaseContext(), Locale.ENGLISH);
                List<Address> results = null;
                try {
                    results = coder.getFromLocation(coords[0], coords[1], 1);
                } catch (IOException e) {
                    Log.e("FarmersMarketFinder", "Error getting location from coordinates");
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
                if (result != null && listener != null) {
                    listener.onFinished(result);
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
