package com.sanchez.fmf;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sanchez.fmf.application.FMFApplication;
import com.sanchez.fmf.util.MarketUtils;
import com.sanchez.fmf.util.ViewUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MarketDetailActivity extends AppCompatActivity {

    public static final String TAG = MarketDetailActivity.class.getSimpleName();

    public static final String EXTRA_MARKET_ID = "com.sanchez.market_id";
    public static final String EXTRA_MARKET_NAME = "com.sanchez.market_name";

    private static final String BASE_URL = "http://search.ams.usda.gov/farmersmarkets/Row.aspx?ID=";

    @Bind(R.id.toolbar_market_detail)
    Toolbar mToolbar;
    @Bind(R.id.progress_bar)
    View mProgressBar;
    @Bind(R.id.market_detail_webview)
    WebView mWebView;
    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;

    private static int MED_ANIM_TIME;

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

        MED_ANIM_TIME = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        String marketId = getIntent().getStringExtra(EXTRA_MARKET_ID);
        String marketName = getIntent().getStringExtra(EXTRA_MARKET_NAME);
        getSupportActionBar().setTitle(marketName);

        int marketColor = getResources().getColor(MarketUtils.getRandomMarketColor());
        ColorStateList cSL = new ColorStateList(new int[][]{new int[0]}, new int[]{marketColor});
        mFavoriteFab.setBackgroundTintList(cSL);

        mFavoriteFab.setOnClickListener((v) -> {
            // update favorite markets list
            LinkedHashMap<String, String> favorites = FMFApplication
                    .getGlobalPreferences()
                    .getFavoriteMarkets();
            if(null != favorites) {
                favorites.put(marketId, marketName);
            } else {
                favorites = new LinkedHashMap<>();
            }
            FMFApplication.getGlobalPreferences().setFavoriteMarkets(favorites);
        });

        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ViewUtils.crossfadeTwoViews(mWebView, mProgressBar, MED_ANIM_TIME);
            }
        });
        mWebView.loadUrl(BASE_URL + marketId);
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
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
