package com.hend.backpack.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.hend.backpack.R;
import com.hend.backpack.adapters.LandmarkRecyclerViewAdapter;
import com.hend.backpack.apis.RestClient;
import com.hend.backpack.data.LandmarkColumns;
import com.hend.backpack.data.LandmarkProvider;
import com.hend.backpack.models.Landmark;
import com.hend.backpack.services.GeoFenceTransitionsIntentService;
import com.hend.backpack.utils.Constants;
import com.hend.backpack.utils.GeofenceErrorMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class LandmarkListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    List<Landmark> landmarksList;
    LandmarkRecyclerViewAdapter adapter;
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    @NonNull
    RecyclerView recyclerView;
    //    private GridLayoutManager gridLayout;
    final String LOG_TAG = LandmarkListActivity.class.getSimpleName();
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 1;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (!canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }

        mGeofenceList = new ArrayList<Geofence>();
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        recyclerView = (RecyclerView) findViewById(R.id.landmark_list);
        assert recyclerView != null;
        landmarksList = new ArrayList<>();
        setupRecyclerView();
        getLandmarksFromDatabase();
        if (landmarksList.isEmpty()) {
            Log.d(LOG_TAG, "Downloading");
            getLandmarks();
        }

        if (findViewById(R.id.landmark_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    private void setupRecyclerView() {
//        gridLayout = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(gridLayout);
        adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
        recyclerView.setAdapter(adapter);
    }

    private void getLandmarks() {
        RestClient.get().requestLandmarks(new Callback<List<Landmark>>() {
            @Override
            public void success(List<Landmark> landmarks, Response response) {
                landmarksList.clear();
                landmarksList = landmarks;
                adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                for (Landmark landmark : landmarks) {
                    Log.d(LOG_TAG, landmark.getName_en());
                }
                updateDatabase(LandmarkListActivity.this, landmarksList);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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
            builder.withValue(LandmarkColumns.LONGITUDE, landmark.getLatitude());
            builder.withValue(LandmarkColumns.FLAG_STREET_VIEW, landmark.isStreet_view());
            batchOperations.add(builder.build());
        }

        try {
            getContentResolver().applyBatch(LandmarkProvider.AUTHORITY, batchOperations);
            Log.d(LOG_TAG, "Data is inserted");
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("Insert Error", "Error applying batch insert", e);
        }


    }

    void getLandmarksFromDatabase() {
        landmarksList.clear();
        Cursor cursor = getContentResolver().query(LandmarkProvider.Landmarks.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() != 0 && cursor != null) {
            Log.d(LOG_TAG, "Getting from Provider");
            int landmarkId, radius;
            boolean streetView;
            Double latitude, longitude;
            String nameEn, nameAr, descriptionEn, descriptionAr, imageUrl;

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                landmarkId = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.LANDMARK_ID));
                nameEn = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_NAME_EN));
                nameAr = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_NAME_AR));
                descriptionEn = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_DESCRIPTION_EN));
                descriptionAr = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_DESCRIPTION_AR));
                imageUrl = cursor.getString(cursor.getColumnIndex(LandmarkColumns.LANDMARK_IMAGE_URL));
                latitude = cursor.getDouble(cursor.getColumnIndex(LandmarkColumns.LATITUDE));
                longitude = cursor.getDouble(cursor.getColumnIndex(LandmarkColumns.LONGITUDE));

                radius = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.LANDMARK_RADIUS));

                streetView = cursor.getInt(cursor.getColumnIndex(LandmarkColumns.FLAG_STREET_VIEW)) > 0 ? true : false;


                Landmark landmark = new Landmark(landmarkId, nameEn, nameAr, descriptionEn, descriptionAr, imageUrl, latitude, longitude, radius, streetView);
                landmarksList.add(landmark);
            }

            cursor.close();
        }
        if (landmarksList.size() > 0) {
            adapter = new LandmarkRecyclerViewAdapter(LandmarkListActivity.this, landmarksList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


    }


    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofencesButtonHandler(View view) {
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
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
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
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //requestPermissions();
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
            Log.d(LOG_TAG, securityException.getMessage());
        }

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
        }
    }

    private void test_sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
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
