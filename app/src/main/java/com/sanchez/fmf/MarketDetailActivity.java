package com.sanchez.fmf;

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

import com.sanchez.fmf.event.GetMarketDetailFailEvent;
import com.sanchez.fmf.event.GetMarketDetailSuccessEvent;
import com.sanchez.fmf.event.RetryGetMarketDetailEvent;
import com.sanchez.fmf.fragment.MarketDetailFragment;
import com.sanchez.fmf.model.MarketDetailResponseModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketDetailActivity extends AppCompatActivity {

    public static final String TAG = MarketDetailActivity.class.getSimpleName();

    public static final String EXTRA_MARKET_ID = "com.sanchez.market_id";
    public static final String EXTRA_MARKET_NAME = "com.sanchez.market_name";

    @Bind(R.id.toolbar_market_detail_activity)
    Toolbar mToolbar;

    private String mMarketId;

    // service for API
    private MarketService mMarketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        mMarketId = getIntent().getStringExtra(EXTRA_MARKET_ID);
        String marketName = getIntent().getStringExtra(EXTRA_MARKET_NAME);
        getSupportActionBar().setTitle(marketName);

        RestClient client = new RestClient();
        mMarketService = client.getMarketService();
        getMarketDetails(mMarketId);

        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = MarketDetailFragment.newInstance(mMarketId, marketName);
        fm.beginTransaction()
                .add(R.id.container_market_detail_activity, listFragment, MarketDetailFragment.TAG)
                .commit();
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

    private void getMarketDetails(String id) {

        mMarketService.getMarket(id, new Callback<MarketDetailResponseModel>() {
            @Override
            public void success(MarketDetailResponseModel marketDetailResponseModel, Response response) {
                EventBus.getDefault().postSticky(new GetMarketDetailSuccessEvent(marketDetailResponseModel));
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.getDefault().postSticky(new GetMarketDetailFailEvent());
                Log.e(TAG, error.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market_detail, menu);
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

    public void onEvent(RetryGetMarketDetailEvent event) { getMarketDetails(mMarketId); }
}
