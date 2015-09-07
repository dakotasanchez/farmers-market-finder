package com.sanchez.fmf.fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.MarketListAdapter;
import com.sanchez.fmf.model.MarketListItemModel;
import com.sanchez.fmf.model.MarketListModel;
import com.sanchez.fmf.service.MarketService;
import com.sanchez.fmf.service.RestClient;
import com.sanchez.fmf.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MarketListFragment extends Fragment  implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MarketListFragment.class.getSimpleName();

    @Bind(R.id.market_list)
    RecyclerView mMarketList;
    @Bind(R.id.fragment_default_suggestion)
    View mSuggestion;
    @Bind(R.id.progress_bar)
    View mProgressBar;
    @Bind(R.id.test_image)
    ImageView mImageView;

    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleApiClient mGoogleApiClient = null;
    private RecyclerView.Adapter mAdapter;

    // service for USDA API
    private MarketService mMarketService;

    private double[] mCoordinates = new double[2];
    private String mPlaceId = null;

    // grab coordinates sent from MainFragment intent
    public static MarketListFragment newInstance(double[] coords, String placeId) {
        MarketListFragment fragment = new MarketListFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MarketListActivity.EXTRA_COORDINATES, coords);
        args.putString(MarketListActivity.EXTRA_PLACE_ID, placeId);
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
            mPlaceId = getArguments().getString(MarketListActivity.EXTRA_PLACE_ID);
        }

        // register client for Google APIs
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .build();

        if(mPlaceId != null) {
            // change width & height params in future
            //getPlacePhotoAsync(mPlaceId, 600, 400);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_list, container, false);
        ButterKnife.bind(this, v);

        // linear RecyclerView
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getContext());
        mMarketList.setLayoutManager(linearLM);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                Log.e(TAG, error.getMessage());
            }
        });
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
        mAdapter = new MarketListAdapter(new ArrayList<>(markets));
        mMarketList.setAdapter(mAdapter);

        int shortAnimation = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        ViewUtils.crossfadeTwoViews(mMarketList, mProgressBar, shortAnimation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }
}
