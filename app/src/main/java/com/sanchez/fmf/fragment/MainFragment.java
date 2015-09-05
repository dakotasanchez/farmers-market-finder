package com.sanchez.fmf.fragment;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.PlaceAutocompleteAdapter;
import com.sanchez.fmf.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    public String TAG = MainFragment.class.getSimpleName();

    @Bind(R.id.root_main_fragment)
    View mRootView;
    @Bind(R.id.card_search)
    View mSearchCard;
    @Bind(R.id.search_autocomplete)
    AutoCompleteTextView mSearchAutocomplete;
    @Bind(R.id.clear_icon)
    View mClearSearch;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_NORTH_AMERICA = new LatLngBounds(new LatLng(18.000000,
            -64.000000), new LatLng(67.000000, -165.000000));

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;
    private String mSelectedPlaceId;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),
                android.R.layout.simple_list_item_1, mGoogleApiClient, BOUNDS_NORTH_AMERICA, null);
        mSearchAutocomplete.setAdapter(mAutocompleteAdapter);

        mSearchAutocomplete.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchAutocomplete.setDropDownVerticalOffset(8); // just below search box
        mSearchAutocomplete.setDropDownBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.pure_white)));
        mSearchAutocomplete.setDropDownAnchor(R.id.card_search);

        mSearchAutocomplete.setOnItemClickListener(mAutocompleteClickListener);
        mSearchAutocomplete.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                new GetCoordinatesFromLocation().execute(mSearchAutocomplete.getText().toString());
                Log.e(TAG, "current place id = " + mSelectedPlaceId);
                ViewUtils.hideKeyboard(getActivity());
                mSearchAutocomplete.dismissDropDown();
                return true;
            }
            return false;
        });
        mSearchAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    if(mClearSearch.getVisibility() == View.GONE) {
                        mClearSearch.setVisibility(View.VISIBLE);
                    }
                } else {
                    mClearSearch.setVisibility(View.GONE);
                }
            }
        });

        mSearchCard.setOnClickListener((v) -> {
            mSearchAutocomplete.requestFocus();
            ViewUtils.showKeyboard(getActivity(), mSearchAutocomplete);
        });

        mClearSearch.setOnClickListener((v) -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mSearchAutocomplete.setText("", false);
            } else {
                mSearchAutocomplete.setText("");
            }
        });

        removeFocusFromAll();

        return rootView;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            PlaceAutocompleteAdapter.PlaceAutocomplete item = mAutocompleteAdapter.getItem(position);
            mSelectedPlaceId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//
//
//            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    // to display attributions and extra place info
//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            // Get the Place object from the buffer.
//            final Place place = places.get(0);
//
//            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));
//
//            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }
//
//            Log.i(TAG, "Place details received: " + place.getName());
//
//            places.release();
//        }
//    };
//
//    private Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
//        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//    }

    private void removeFocusFromAll() {
        mRootView.requestFocus();
    }

    private void launchMarketList(ArrayList<Double> coords) {
        Intent i = new Intent(getActivity(), MarketListActivity.class);
        i.putExtra(MarketListActivity.EXTRA_COORDINATES,
                new double[] { coords.get(0), coords.get(1) } );
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Snackbar.make(mRootView, "Google Places API unavailable right now. Error code:" +
                connectionResult.getErrorCode(), Snackbar.LENGTH_LONG).show();
    }

    private class GetCoordinatesFromLocation extends AsyncTask<String, Void, ArrayList<Double>> {

        @Override
        protected ArrayList<Double> doInBackground(String... location) {
            ArrayList<Double> returnList = new ArrayList<Double>();
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(location[0], 1);
                if(addresses.size() > 0) {
                    Address address = addresses.get(0);
                    returnList.add(address.getLatitude());
                    returnList.add(address.getLongitude());
                    return returnList;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Snackbar.make(mRootView, "Geocoder error", Snackbar.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            if(result != null) {
                //Snackbar.make(mRootView, "Coords: lat=" + result.get(0) + " lon=" + result.get(1),
                //        Snackbar.LENGTH_LONG).show();
                //TODO: checkout the coords to make sure they're valid
                launchMarketList(result);
            } else {
                Snackbar.make(mRootView, "Invalid input", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
