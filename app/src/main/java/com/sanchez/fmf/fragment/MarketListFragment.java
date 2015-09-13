package com.sanchez.fmf.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sanchez.fmf.MarketDetailActivity;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.MarketListAdapter;
import com.sanchez.fmf.event.MarketClickEvent;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.model.MarketListModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;
import com.sanchez.fmf.util.MarketUtils;
import com.sanchez.fmf.util.ViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketListFragment extends Fragment  implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MarketListFragment.class.getSimpleName();

    @Bind(R.id.toolbar_market_list_fragment)
    Toolbar mToolbar;
    @Bind(R.id.market_list)
    RecyclerView mMarketList;
    @Bind(R.id.fragment_no_markets)
    View mNoMarkets;
    @Bind(R.id.layout_try_again)
    ViewGroup mTryAgain;
    @Bind(R.id.try_again_button)
    Button mTryAgainButton;
    @Bind(R.id.market_backdrop)
    ImageView mMarketBackDrop;
    @Bind(R.id.map_view_fab)
    FloatingActionButton mMapFab;
    @Bind(R.id.progress_bar)
    View mProgressBar;

    private static int MED_ANIM_TIME;

    public abstract class OnGetLocationFinishedListener {
        public abstract void onFinished(String result);
    }

    //private static final int GOOGLE_API_CLIENT_ID = 0;

    //private GoogleApiClient mGoogleApiClient = null;
    private MarketListAdapter mAdapter;

    // service for USDA API
    private MarketService mMarketService;

    private double[] mCoordinates = new double[2];
    private String mPlaceTitle = null;
    private String mPlaceId = null;
    private boolean mUsedDeviceCoordinates = false;

    // grab coordinates sent from MainFragment intent
    public static MarketListFragment newInstance(double[] coords,
                                                 String placeTitle,
                                                 String placeId,
                                                 boolean usedDeviceCoordinates) {
        MarketListFragment fragment = new MarketListFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MarketListActivity.EXTRA_COORDINATES, coords);
        args.putString(MarketListActivity.EXTRA_PLACE_TITLE, placeTitle);
        args.putString(MarketListActivity.EXTRA_PLACE_ID, placeId);
        args.putBoolean(MarketListActivity.EXTRA_USED_DEVICE_COORDINATES, usedDeviceCoordinates);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestClient client = new RestClient();
        mMarketService = client.getMarketService();

        if (getArguments() != null) {
            mCoordinates = getArguments().getDoubleArray(MarketListActivity.EXTRA_COORDINATES);
            mPlaceTitle = getArguments().getString(MarketListActivity.EXTRA_PLACE_TITLE);
            mPlaceId = getArguments().getString(MarketListActivity.EXTRA_PLACE_ID);
            mUsedDeviceCoordinates = getArguments().getBoolean(MarketListActivity.EXTRA_USED_DEVICE_COORDINATES);
        }

        MED_ANIM_TIME = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        // register client for Google APIs
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
//                .build();
//
//        if(mPlaceId != null) {
//            // change width & height params in future
//            getPlacePhotoAsync(mPlaceId, 600, 400);
//        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_list, container, false);
        ButterKnife.bind(this, v);

        AppCompatActivity a = (AppCompatActivity)getActivity();
        a.setSupportActionBar(mToolbar);
        a.getSupportActionBar().setTitle("");
        a.getSupportActionBar().setDisplayShowHomeEnabled(true);
        a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!mUsedDeviceCoordinates) {
            a.getSupportActionBar().setTitle(mPlaceTitle);
        } else {
            // user clicked "use current location button", meaning we need to reverse geocode
            getLocationName(mCoordinates, new OnGetLocationFinishedListener() {
                @Override
                public void onFinished(String result) {
                    a.getSupportActionBar().setTitle(result);
                }
            });
        }

        // set backgroundTint programmatically as xml method is undefined...
        int pureWhite = getResources().getColor(R.color.pure_white);
        ColorStateList cSL2 = new ColorStateList(new int[][]{new int[0]}, new int[]{pureWhite});
        ((AppCompatButton)mTryAgainButton).setSupportBackgroundTintList(cSL2);

        int marketColor = getResources().getColor(MarketUtils.getRandomMarketColor());
        ColorStateList cSL = new ColorStateList(new int[][]{new int[0]}, new int[]{marketColor});
        mMapFab.setBackgroundTintList(cSL);


        // linear RecyclerView
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getContext());
        mMarketList.setLayoutManager(linearLM);
        // dummy adapter so Android doesn't complain
        mMarketList.setAdapter(new MarketListAdapter(new ArrayList<>(), false));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTryAgainButton.setOnClickListener((v) -> {
            ViewUtils.crossfadeTwoViews(mProgressBar, mTryAgain, MED_ANIM_TIME);
            retrieveMarkets();
        });

        retrieveMarkets();

        loadBackdrop();
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
                showMarkets(marketListModel.getMarkets());
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(getView(), "Failed to get markets!", Snackbar.LENGTH_LONG).show();
                ViewUtils.crossfadeTwoViews(mTryAgain, mProgressBar, MED_ANIM_TIME);
                Log.e(TAG, error.toString());
            }
        });
    }

    private void loadBackdrop() {
        Glide.with(getActivity()).load(MarketUtils.getRandomMarketDrawable())
                .centerCrop()
                .into(mMarketBackDrop);
    }

    // TODO: check to see if this photo API is usable in future
    // current state: most place IDs don't return any photos....
//    private void getPlacePhotoAsync(String placeId, int width, int height) {
//        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
//                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
//
//                    @Override
//                    public void onResult(PlacePhotoMetadataResult photos) {
//                        if (!photos.getStatus().isSuccess()) {
//                            return;
//                        }
//
//                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                        if (photoMetadataBuffer.getCount() > 0) {
//                            // Display the first bitmap in an ImageView in the size of the view
//                            photoMetadataBuffer.get(0)
//                                    .getScaledPhoto(mGoogleApiClient, width, height)
//                                    .setResultCallback(mDisplayPhotoResultCallback);
//                        }
//                        photoMetadataBuffer.release();
//                    }
//                });
//    }
//
//    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
//            = new ResultCallback<PlacePhotoResult>() {
//        @Override
//        public void onResult(PlacePhotoResult placePhotoResult) {
//            if (!placePhotoResult.getStatus().isSuccess()) {
//                return;
//            }
//            mImageView.setImageBitmap(placePhotoResult.getBitmap());
//        }
//    };

    private void showMarkets(List<MarketListItemModel> markets) {

        View incomingView;

        if(markets.size() > 0) {
            mAdapter = new MarketListAdapter(new ArrayList<>(markets), mUsedDeviceCoordinates);
            mMarketList.setAdapter(mAdapter);
            incomingView = mMarketList;
        } else {
            incomingView = mNoMarkets;
        }

        ViewUtils.crossfadeTwoViews(incomingView, mProgressBar, MED_ANIM_TIME);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }

    // get a location name based on latitude and longitude (reverse geocoding)
    public void getLocationName(final double[] coords, final OnGetLocationFinishedListener listener) {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... arg0) {
                Geocoder coder = new Geocoder(getActivity(), Locale.ENGLISH);
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

    // use clicked on a market card
    public void onEvent(MarketClickEvent event) {
        int position = mMarketList.getChildAdapterPosition(event.getMarket());
        MarketListItemModel market = mAdapter.getDataSet().get(position);

        Intent i = new Intent(getActivity(), MarketDetailActivity.class);
        i.putExtra(MarketDetailActivity.EXTRA_MARKET_ID, market.getId());
        i.putExtra(MarketDetailActivity.EXTRA_MARKET_NAME,
                MarketUtils.getNameFromMarketString(market.getName()));
        startActivity(i);
    }
}
