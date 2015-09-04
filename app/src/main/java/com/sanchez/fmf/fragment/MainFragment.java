package com.sanchez.fmf.fragment;


import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.PlaceAutocompleteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    public String TAG = MainFragment.class.getSimpleName();

    @Bind(R.id.root_main_fragment)
    View mRootView;
    @Bind(R.id.search_autocomplete)
    AutoCompleteTextView mSearchAutocomplete;
    @Bind(R.id.clear_icon)
    View mClearSearch;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_NORTH_AMERICA = new LatLngBounds(new LatLng(18.000000,
            -64.000000), new LatLng(67.000000, -165.000000));

    private GoogleApiClient googleApiClient;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient
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
                android.R.layout.simple_list_item_1, googleApiClient, BOUNDS_NORTH_AMERICA, null);
        mSearchAutocomplete.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchAutocomplete.setAdapter(mAutocompleteAdapter);
        mSearchAutocomplete.setDropDownVerticalOffset(8); // just below search box
        mSearchAutocomplete.setDropDownBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.pure_white)));
        mSearchAutocomplete.setDropDownAnchor(R.id.card_search);
        mSearchAutocomplete.setThreshold(3); // waits for 3 chars before autocomplete kicks in
        mSearchAutocomplete.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
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

    private void removeFocusFromAll() {
        mRootView.requestFocus();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getActivity(), "Google Places API unavailable right now. Error code:" +
                connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "Geocoder error", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            if(result != null) {
                Toast.makeText(getActivity(), "Coords: lat=" + result.get(0) + " lon=" + result.get(1),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_LONG).show();
            }
        }
    }
}
