package com.hend.backpack.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hend.backpack.R;
import com.hend.backpack.adapters.LandmarkRecyclerViewAdapter;
import com.hend.backpack.apis.RestClient;
import com.hend.backpack.data.LandmarkColumns;
import com.hend.backpack.data.LandmarkDatabase;
import com.hend.backpack.data.LandmarkProvider;
import com.hend.backpack.models.Landmark;
import com.hend.backpack.services.GeoFenceTransitionsIntentService;
import com.hend.backpack.utils.Constants;
import com.hend.backpack.utils.GeofenceErrorMessages;
import com.hend.backpack.utils.Utility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * An activity representing a list of Landmarks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LandmarkDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class LandmarkListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, LandmarkDetailFragment.Callback, LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @NonNull
    @BindView(R.id.landmark_list)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerview_landmark_empty)
    TextView recyclerviewLandmarkEmpty;
    AdView mAdView;
    GoogleMap map;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    List<Landmark> landmarksList;
    LandmarkRecyclerViewAdapter adapter;
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    //    private GridLayoutManager gridLayout;
    final String LOG_TAG = LandmarkListActivity.class.getSimpleName();
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final String ACTION_DATA_UPDATED="com.hend.backpack.ACTION_DATA_UPDATED";
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 1;
    final static int LANDMARK_LOADER = 0;
    private static final String[] LANDMARK_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            LandmarkDatabase.LANDMARKS + "." + LandmarkColumns.ID,
            LandmarkColumns.LANDMARK_ID,
            LandmarkColumns.LANDMARK_NAME_EN,
            LandmarkColumns.LANDMARK_NAME_AR,
            LandmarkColumns.LANDMARK_DESCRIPTION_EN,
            LandmarkColumns.LANDMARK_DESCRIPTION_AR,
            LandmarkColumns.LANDMARK_IMAGE_URL,
            LandmarkColumns.LATITUDE,
            LandmarkColumns.LONGITUDE,
            LandmarkColumns.LANDMARK_RADIUS,
            LandmarkColumns.FLAG_STREET_VIEW
    };

    // These indices are tied to LANDMARKS_COLUMNS.  If LANDMARKS_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_LANDMARK_ID = 1;
    static final int COL_LANDMARK_NAME_EN = 2;
    static final int COL_LANDMARK_NAME_AR = 3;
    static final int COL_LANDMARK_DESCRIPTION_EN = 4;
    static final int COL_LANDMARK_DESCRIPTION_AR = 5;
    static final int COL_LANDMARK_IMAGE_URL = 6;
    static final int COL_LATITUDE = 7;
    static final int COL_LONGITUDE = 8;
    static final int COL_LANDMARK_RADIUS = 9;
    static final int COL_FLAG_STREET_VIEW = 10;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANDMARK_STATUS_OK, LANDMARK_STATUS_SERVER_DOWN, LANDMARK_STATUS_SERVER_INVALID, LANDMARK_STATUS_UNKNOWN, LANDMARK_STATUS_NETWORK_ERROR})
    public @interface LandmarksStatus {
    }

    public static final int LANDMARK_STATUS_OK = 0;
    public static final int LANDMARK_STATUS_SERVER_DOWN = 1;
    public static final int LANDMARK_STATUS_SERVER_INVALID = 2;
    public static final int LANDMARK_STATUS_UNKNOWN = 3;
    public static final int LANDMARK_STATUS_NETWORK_ERROR = 5;
    @LandmarksStatus
    int status;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        if (!canAccessLocation()) {
//            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
//        }
        mGeofenceList = new ArrayList<>();
        // Get the geofences used. Geofence data is hard coded in this sample.
//        populateGeofenceList();


        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        assert recyclerView != null;
        landmarksList = new ArrayList<>();
        setupRecyclerView();

        if (landmarksList.isEmpty()) {
            getLandmarks();
        }
        getLoaderManager().initLoader(LANDMARK_LOADER, null, this);

        if (findViewById(R.id.landmark_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        if (mTwoPane) {
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    void updateMap(GoogleMap googleMap, Landmark landmark) {
        LatLng landmarkLocation = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        CameraPosition camera = CameraPosition.builder()
                .target(landmarkLocation)
                .zoom(15)
                .build();
        MarkerOptions marker = new MarkerOptions().position(landmarkLocation).title(landmark.getName_en());
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
        //Clicking on the marker will show navigation option
        googleMap.addMarker(marker);
        googleMap.addCircle(new CircleOptions()
                .center(landmarkLocation)
                .radius(500)
                .strokeColor(ContextCompat.getColor(this, R.color.colorAccent))
                .fillColor(Color.argb(64, 25, 196, 216)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (canAccessLocation()) {
                    addGeofencesHandler();
                } else {
                    Log.d(LOG_TAG, "Cannot add geo fences");
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_landmarkslist, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        }
        return true;
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, new LandmarkRecyclerViewAdapter.LandmarkAdapterOnClickHandler() {
            @Override
            public void onClick(Landmark landmark, LandmarkRecyclerViewAdapter.LandmarkAdapterViewHolder vh) {
                if (!mTwoPane) {
                    Intent intent = new Intent(LandmarkListActivity.this, LandmarkDetailActivity.class);
                    intent.putExtra(Constants.LANDMARK, landmark);
                    startActivity(intent);
                } else {
                    //TODO: Send data to the fragment
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Constants.LANDMARK,
                            landmark);
                    LandmarkDetailFragment fragment = new LandmarkDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.landmark_detail_container, fragment)
                            .commit();
                    updateMap(map, landmark);
                    Toast.makeText(LandmarkListActivity.this, landmark.getName_en(), Toast.LENGTH_SHORT).show();
                }
            }
        }, recyclerviewLandmarkEmpty, landmarksList);
        recyclerView.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLandmarks() {
        RestClient.get().requestLandmarks(new Callback<List<Landmark>>() {
            @Override
            public void success(List<Landmark> landmarks, Response response) {
                if (landmarks != null && landmarks.size() > 0) {
                    landmarksList.clear();
                    landmarksList = landmarks;
//                    for (Landmark landmark : landmarks) {
//                        Log.d(LOG_TAG, landmark.getName_en());
//                    }
                    updateDatabase(LandmarkListActivity.this, landmarksList);

                    addLandmarksToGeoPoints(landmarksList);
                    populateGeofenceList();
                    if (!canAccessLocation()) {
                        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
                    } else {
                        if (mGoogleApiClient.isConnected()) {
                            addGeofencesHandler();
                        }
                    }
                }
                updateWidgets();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, error.getKind().name());
                Log.e(LOG_TAG, error.getMessage());
                switch (error.getKind()) {
                    case HTTP:
                        // TODO get message from getResponse()'s body or HTTP status 404 Web page not available
                        Log.e(LOG_TAG, error.getResponse().getReason());
                        Log.e(LOG_TAG, "" + error.getResponse().getStatus());
                        // Invalid
                        status = LANDMARK_STATUS_SERVER_DOWN;
                        updateEmptyView();
                        break;
                    case NETWORK:
                        Log.e(LOG_TAG, error.getCause().getMessage());
                        status = LANDMARK_STATUS_NETWORK_ERROR;
                        updateEmptyView();
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.internetConnection, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getLandmarks();
                                    }
                                });
                        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                        snackbar.show();
                        break;
                    case CONVERSION:
                        Log.e(LOG_TAG, "Invalid json");
                        //Server Invalid
                        status = LANDMARK_STATUS_SERVER_INVALID;
                        updateEmptyView();
                        break;
                    case UNEXPECTED:
//                        status = LANDMARK_STATUS_UNKNOWN;
//                        updateEmptyView();
                        throw error;

                    default:
                        throw new AssertionError("Unknown error kind: " + error.getKind());
                        //Server is down
                }

//                Log.d(LOG_TAG,error.getBody().toString());
            }
        });
    }

    private void updateWidgets()
    {
        Context context = getApplicationContext();
        Intent dataUpdatedIntent= new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
    public void updateDatabase(Context context, List<Landmark> landmarks) {
        getContentResolver().delete(LandmarkProvider.Landmarks.CONTENT_URI, null, null);
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(landmarks.size());

//        Log.d(LOG_TAG, "Data is inserted");
        for (Landmark landmark : landmarks) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    LandmarkProvider.Landmarks.CONTENT_URI);
            builder.withValue(LandmarkColumns.LANDMARK_ID, landmark.getId());
            builder.withValue(LandmarkColumns.LANDMARK_NAME_EN, landmark.getName_en());
            builder.withValue(LandmarkColumns.LANDMARK_NAME_AR, landmark.getName_ar());
            builder.withValue(LandmarkColumns.LANDMARK_DESCRIPTION_EN, landmark.getDescription_en());
            builder.withValue(LandmarkColumns.LANDMARK_DESCRIPTION_AR, landmark.getDescription_ar());
            builder.withValue(LandmarkColumns.LANDMARK_IMAGE_URL, landmark.getImage_url());
            builder.withValue(LandmarkColumns.LANDMARK_RADIUS, landmark.getRadius());
            builder.withValue(LandmarkColumns.LATITUDE, landmark.getLatitude());
            builder.withValue(LandmarkColumns.LONGITUDE, landmark.getLongitude());
            builder.withValue(LandmarkColumns.FLAG_STREET_VIEW, landmark.isStreet_view());
            batchOperations.add(builder.build());
        }

        try {
            getContentResolver().applyBatch(LandmarkProvider.AUTHORITY, batchOperations);
            //TODO: get called from Loader
//            getLandmarksFromDatabase();
            Log.d(LOG_TAG, "Data is inserted");
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("Insert Error", "Error applying batch insert", e);
        }


    }

    void addLandmarksToGeoPoints(List<Landmark> landmarks) {
        for (Landmark landmark : landmarks) {
            Constants.CAIRO_LANDMARKS.put(landmark.getName_en(), landmark);
        }
    }

    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {

            if (null != recyclerviewLandmarkEmpty) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_landmarks_list;
                switch (status) {
                    case LANDMARK_STATUS_SERVER_DOWN:
                        message = R.string.empty_landmarks_list_server_down;
                        break;
                    case LANDMARK_STATUS_SERVER_INVALID:
                        message = R.string.empty_landmarks_list_server_error;
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(LandmarkListActivity.this)) {
                            message = R.string.empty_landmarks_list_no_network;
                        }
                }
                recyclerviewLandmarkEmpty.setText(message);
            }
        }
    }

//    void getLandmarksFromDatabase() {
//        landmarksList.clear();
//        Cursor cursor = getContentResolver().query(LandmarkProvider.Landmarks.CONTENT_URI,
//                null, null, null, null);
//        if (cursor.getCount() != 0 && cursor != null) {
//            Log.d(LOG_TAG, "Getting from Provider");
//            int landmarkId, radius;
//            boolean streetView;
//            Double latitude, longitude;
//            String nameEn, nameAr, descriptionEn, descriptionAr, imageUrl;
//
//            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//
//                landmarkId = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.LANDMARK_ID));
//                nameEn = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_NAME_EN));
//                nameAr = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_NAME_AR));
//                descriptionEn = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_DESCRIPTION_EN));
//                descriptionAr = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_DESCRIPTION_AR));
//                imageUrl = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_IMAGE_URL));
//                latitude = cursor.getDouble(cursor.getColumnIndex(LandmarkColumns.LATITUDE));
//                longitude = cursor.getDouble(cursor.getColumnIndex(LandmarkColumns.LONGITUDE));
//                radius = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.LANDMARK_RADIUS));
//                streetView = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.FLAG_STREET_VIEW)) > 0 ? true : false;
//
//                Landmark landmark = new Landmark(landmarkId, nameEn, nameAr, descriptionEn, descriptionAr, imageUrl, latitude, longitude, radius, streetView);
//                landmarksList.add(landmark);
//            }
//
//            cursor.close();
//        }
//        if (landmarksList.size() > 0) {
////            adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
////            recyclerView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        }
//
//
//    }


    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    public void addGeofencesButtonHandler(View view) {
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    // The GeofenceRequest object.
//                    getGeofencingRequest(),
//                    // A pending intent that that is reused when calling removeGeofences(). This
//                    // pending intent is used to generate an intent when a matched geofence
//                    // transition is observed.
//                    getGeofencePendingIntent()
//            ).setResultCallback(this); // Result processed in onResult().
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//        }
//    }


    void addGeofencesHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    public void populateGeofenceList() {

        for (Map.Entry<String, Landmark> entry : Constants.CAIRO_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // TODO: from service, Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().getLatitude(),
                            entry.getValue().getLongitude(),
                            entry.getValue().getRadius()
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
      }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.d(LOG_TAG,errorMessage);
        }
    }

    @Override
    public void onStreetViewClicked(Landmark landmark) {
        Intent streetViewIntent = new Intent(this, StreetViewActivity.class);
        streetViewIntent.putExtra(Constants.STREET_VIEW, landmark);
        startActivity(streetViewIntent);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
//        Uri landmarkUri = LandmarkProvider.Landmarks.CONTENT_URI
        return new CursorLoader(this, LandmarkProvider.Landmarks.CONTENT_URI, LANDMARK_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        Log.d(LOG_TAG, "onLoadReset");
        adapter.swapCursor(null);
    }


//    public class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final List<DummyContent.DummyItem> mValues;
//
//        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
//            mValues = items;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.landmark_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(LandmarkDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                        LandmarkDetailFragment fragment = new LandmarkDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.landmark_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, LandmarkDetailActivity.class);
//                        intent.putExtra(LandmarkDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView mIdView;
//            public final TextView mContentView;
//            public DummyContent.DummyItem mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
//                mContentView = (TextView) view.findViewById(R.id.content);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
//        }
//    }
}
