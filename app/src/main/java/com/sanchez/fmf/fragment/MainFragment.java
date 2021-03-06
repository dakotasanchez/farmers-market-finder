package com.sanchez.fmf.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sanchez.fmf.MainActivity;
import com.sanchez.fmf.MarketDetailActivity;
import com.sanchez.fmf.MarketListActivity;
import com.sanchez.fmf.R;
import com.sanchez.fmf.adapter.FavoriteListAdapter;
import com.sanchez.fmf.adapter.PlaceAutocompleteAdapter;
import com.sanchez.fmf.application.FMFApplication;
import com.sanchez.fmf.event.FavoriteClickEvent;
import com.sanchez.fmf.event.FavoriteRemoveEvent;
import com.sanchez.fmf.event.PermissionResultEvent;
import com.sanchez.fmf.util.LocationUtil;
import com.sanchez.fmf.util.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public String TAG = MainFragment.class.getSimpleName();

    @BindView(R.id.search_autocomplete)
    AutoCompleteTextView mSearchAutocomplete;
    @BindView(R.id.search_button)
    View mSearchButton;
    @BindView(R.id.clear_icon)
    View mClearSearch;
    @BindView(R.id.use_location_button)
    Button mUseLocationButton;
    @BindView(R.id.distance_constraint)
    EditText mDistanceConstraint;
    @BindView(R.id.market_favorites_list)
    RecyclerView mFavoritesList;
    @BindView(R.id.no_favorites_text)
    View mNoFavorites;

    // parent of all visible UI in main fragment
    View contentView;

    final public static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_NORTH_AMERICA = new LatLngBounds(new LatLng(18.000000,
            -64.000000), new LatLng(67.000000, -165.000000));

    private GoogleApiClient mGoogleApiClient = null;
    private GeoDataClient mGeoDataClient = null;
    private PlaceAutocompleteAdapter mAutocompleteAdapter = null;

    private String mSelectedPlace = null;
    private String mSelectedPlaceId = null;

    private boolean mGettingPermission = false;

    private Snackbar mFetchingSnackbar;
    private Runnable delayedShowFetching = this::showFetching;
    private Runnable delayedCancelShowFetching = this::cancelShowFetching;

    public abstract class OnGetCoordinatesFromLocationListener {
        public abstract void onFinished(ArrayList<Double> results);
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGeoDataClient = Places.getGeoDataClient(getActivity());
        // register client for Google APIs
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
//                .build();
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
    public void onResume() {
        super.onResume();
        if(!mGettingPermission) {
            cancelShowFetching();
        }
        updateFavoritesList(null);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        contentView = getActivity().findViewById(android.R.id.content);

        // linear RecyclerView
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getContext());
        mFavoritesList.setLayoutManager(linearLM);
        mFavoritesList.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        mFavoritesList.setAdapter(new FavoriteListAdapter(new LinkedHashMap<>()));

        // customize place autocomplete adapter
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("US")
                .build();

        mAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, BOUNDS_NORTH_AMERICA, filter);
        mSearchAutocomplete.setAdapter(mAutocompleteAdapter);

        // customize autocomplete
        mSearchAutocomplete.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchAutocomplete.setDropDownVerticalOffset(8); // just below search box
        mSearchAutocomplete.setDropDownBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.pure_white)));
        mSearchAutocomplete.setDropDownAnchor(R.id.card_search);

        // remember place ID of selected dropdown item
        mSearchAutocomplete.setOnItemClickListener(mAutocompleteClickListener);

        // search when search IME option pressed
        mSearchAutocomplete.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });

        // show search 'clear' icon when text is present
        mSearchAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (mClearSearch.getVisibility() == View.GONE) {
                        mClearSearch.setVisibility(View.VISIBLE);
                    }
                } else {
                    mClearSearch.setVisibility(View.GONE);
                }
            }
        });

        mSearchButton.setOnClickListener((v) -> {
            search();
        });

        mClearSearch.setOnClickListener((v) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mSearchAutocomplete.setText("", false);
            } else {
                mSearchAutocomplete.setText("");
            }
        });

        // set backgroundTint programmatically as xml method is undefined...
        int primaryColor = getResources().getColor(R.color.primary);
        ColorStateList cSL = new ColorStateList(new int[][]{new int[0]}, new int[]{primaryColor});
        ((AppCompatButton)mUseLocationButton).setSupportBackgroundTintList(cSL);
        ((AppCompatImageButton)mSearchButton).setSupportBackgroundTintList(cSL);

        mUseLocationButton.setOnClickListener((v) -> getLocationWrapper());

        // no keyboard popup on launch
        removeFocusFromAll();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void search() {
        ViewUtils.hideKeyboard(getActivity());
        if (mSearchAutocomplete.getText().toString().isEmpty() || mDistanceConstraint.getText().toString().isEmpty()) {
            Snackbar.make(contentView, "Invalid input", Snackbar.LENGTH_LONG).show();
            cancelShowFetching();
            return;
        }

        mSearchAutocomplete.setEnabled(false);
        mUseLocationButton.setEnabled(false);

        String searchText = mSearchAutocomplete.getText().toString();
        String[] tokens = searchText.split(",");
        mSelectedPlace = tokens[0];

        getCoordinatesFromLocation(searchText, new OnGetCoordinatesFromLocationListener() {
            @Override
            public void onFinished(ArrayList<Double> results) {
                //TODO: checkout the coords to make sure they're valid
                launchMarketList(new double[] { results.get(0), results.get(1) }, false, mSelectedPlace);
            }
        });

        mSearchAutocomplete.dismissDropDown();
        contentView.postDelayed(delayedShowFetching, 300);
        // As a fail safe if something errors out
        contentView.postDelayed(delayedCancelShowFetching, 7000);
    }

    // wrap permission requester around location fetching (required for location access in API 23+)
    private void getLocationWrapper() {
        mSearchAutocomplete.setEnabled(false);
        mUseLocationButton.setEnabled(false);
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("Access coarse location");
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("Access fine location");
        }
        if (permissionsList.size() > 0) {
            mGettingPermission = true;
            if (permissionsNeeded.size() > 0) {
                showPermissionRationale(getString(R.string.location_permission_rationale));
                return;
            }
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MainFragment.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        getLocationAndLaunchList();
    }

    private void showPermissionRationale(String message) {
        RationaleDialogFragment frag = RationaleDialogFragment.newInstance(message);
        frag.show(getActivity().getSupportFragmentManager(), "dialog2");
    }

    private boolean addPermission(List<String> permissionList, String permission) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);
            if (shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    private void getLocationAndLaunchList() {
        boolean locEnabled = new LocationUtil().getLocation(getContext(),
                new LocationUtil.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        getActivity().runOnUiThread(() -> {
                            if (location == null) {
                                Snackbar.make(contentView, R.string.location_error,
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                double[] coords = {location.getLatitude(), location.getLongitude()};
                                launchMarketList(coords, true, null);
                            }
                        });
                    }
                });
        if (locEnabled) {
            contentView.postDelayed(delayedShowFetching, 300);
            // As a fail safe if something errors out
            contentView.postDelayed(delayedCancelShowFetching, 7000);
        } else {
            final Snackbar s = Snackbar.make(contentView,
                    R.string.enable_location_prompt,
                    Snackbar.LENGTH_INDEFINITE);
            s.setAction(R.string.enable, (view) -> {
                s.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            s.setActionTextColor(getResources().getColor(R.color.pure_white));
            s.show();
        }
    }

    private void showFetching() {
        mFetchingSnackbar = Snackbar.make(contentView, R.string.fetching_location, Snackbar.LENGTH_INDEFINITE);
        mFetchingSnackbar.show();
    }

    private void cancelShowFetching() {
        contentView.removeCallbacks(delayedShowFetching);
        if(mFetchingSnackbar != null) {
            mFetchingSnackbar.dismiss();
        }
        mSearchAutocomplete.setEnabled(true);
        mUseLocationButton.setEnabled(true);
    }

    private void updateFavoritesList(LinkedHashMap<String, String> favorites) {
        // check to see if we got a parameter
        if(null == favorites) {
            favorites = FMFApplication
                    .getGlobalPreferences()
                    .getFavoriteMarkets();
        }

        // see if the persistent data lookup returned anything
        if (null == favorites) {
            mNoFavorites.setVisibility(View.VISIBLE);
        } else {
            ((FavoriteListAdapter)mFavoritesList.getAdapter()).replaceData(favorites);
            if(favorites.size() > 0) {
                mNoFavorites.setVisibility(View.GONE);
            } else {
                mNoFavorites.setVisibility(View.VISIBLE);
            }
        }
    }

    private void launchMarketList(double[] coords, boolean usedDeviceCoordinates, String placeTitle) {
        mGettingPermission = false;
        // start market list activity with coordinates from search
        Intent i = new Intent(getActivity(), MarketListActivity.class);
        i.putExtra(MarketListActivity.EXTRA_COORDINATES, coords);
        i.putExtra(MarketListActivity.EXTRA_PLACE_TITLE, placeTitle);
        i.putExtra(MarketListActivity.EXTRA_PLACE_ID, mSelectedPlaceId);
        i.putExtra(MarketListActivity.EXTRA_USED_DEVICE_COORDINATES, usedDeviceCoordinates);
        i.putExtra(MarketListActivity.EXTRA_DISTANCE_CONSTRAINT, mDistanceConstraint.getText().toString());
        startActivity(i);
        contentView.removeCallbacks(delayedShowFetching);
        contentView.removeCallbacks(delayedCancelShowFetching);
        if(mFetchingSnackbar != null) {
            mFetchingSnackbar.dismiss();
        }
    }

    private void removeFocusFromAll() {
        contentView.requestFocus();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // retrieve the place ID of the selected item from the Adapter.
            AutocompletePrediction item = mAutocompleteAdapter.getItem(position);
            mSelectedPlaceId = String.valueOf(item.getPlaceId());

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }

    public void getCoordinatesFromLocation(String location,
                                           final OnGetCoordinatesFromLocationListener listener) {
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... arg0) {
                ArrayList<Double> returnList = new ArrayList<Double>();
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        returnList.add(address.getLatitude());
                        returnList.add(address.getLongitude());
                        return returnList;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return null;
                }
                return returnList;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> results) {
                if(null == results) {
                    Snackbar.make(contentView, "Network error", Snackbar.LENGTH_LONG).show();
                    cancelShowFetching();
                } else if (results.size() > 0) {
                    listener.onFinished(results);
                } else {
                    Snackbar.make(contentView, "Invalid input", Snackbar.LENGTH_LONG).show();
                    cancelShowFetching();
                }
            }
        }.execute();
    }

    public static class RationaleDialogFragment extends AppCompatDialogFragment {

        public static final String EXTRA_MESSAGE = "com.sanchez.fmf.extra_message";

        public static  RationaleDialogFragment newInstance(String message) {
            RationaleDialogFragment frag = new RationaleDialogFragment();
            Bundle args = new Bundle();
            args.putString(EXTRA_MESSAGE, message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle onSaveInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(EXTRA_MESSAGE))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[] {
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION},
                                    MainFragment.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EventBus.getDefault().post(new PermissionResultEvent(false));
                        }
                    })
                    .create();
        }
    }

    public void onEvent(FavoriteClickEvent event) {
        Intent i = new Intent(getActivity(), MarketDetailActivity.class);
        i.putExtra(MarketDetailActivity.EXTRA_MARKET_ID, event.getId());
        i.putExtra(MarketDetailActivity.EXTRA_MARKET_NAME, event.getName());
        startActivity(i);
    }

    public void onEvent(FavoriteRemoveEvent event) {
        LinkedHashMap<String, String> favoritesMap =
            FMFApplication.getGlobalPreferences().getFavoriteMarkets();
        favoritesMap.remove(event.getId());

        FMFApplication.getGlobalPreferences().setFavoriteMarkets(favoritesMap);

        updateFavoritesList(favoritesMap);
    }

    public void onEvent(PermissionResultEvent event) {
        if (event.getGranted()) {
            getLocationAndLaunchList();
        } else {
            mSearchAutocomplete.setEnabled(true);
            mUseLocationButton.setEnabled(true);
            Snackbar.make(contentView, "Location permission denied", Snackbar.LENGTH_LONG).show();
        }
    }
}
