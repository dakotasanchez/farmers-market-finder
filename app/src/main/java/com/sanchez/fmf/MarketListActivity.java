package com.sanchez.fmf;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.sanchez.fmf.fragment.MarketListFragment;

import butterknife.ButterKnife;

public class MarketListActivity extends AppCompatActivity {

    public static final String EXTRA_COORDINATES = "com.sanchez.extra_coordinates";
    public static final String EXTRA_PLACE_TITLE = "com.sanchez.extra_place_title";
    public static final String EXTRA_PLACE_ID = "com.sanchez.extra_place_id";
    public static final String EXTRA_CALCULATE_DISTANCES = "com.sanchez.extra_calculate_distances";

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
        boolean needToCalculateDistances = (null != placeTitle);

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentById(R.id.container_market_list_activity);

        if (listFragment == null) {
            listFragment = MarketListFragment.newInstance(coordinates, placeTitle, placeId, needToCalculateDistances);
            fm.beginTransaction()
                    .add(R.id.container_market_list_activity, listFragment)
                    .commit();
        }
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
