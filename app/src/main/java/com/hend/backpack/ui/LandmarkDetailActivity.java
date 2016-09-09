package com.hend.backpack.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hend.backpack.R;
import com.hend.backpack.models.Landmark;
import com.hend.backpack.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Landmark detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LandmarkListActivity}.
 */
public class LandmarkDetailActivity extends AppCompatActivity implements OnMapReadyCallback, LandmarkDetailFragment.Callback {

    AdView mAdView;
    LatLng landmarkLocation;
    Landmark landmark;
    @BindView(R.id.ivLandmark)
    ImageView ivLandmark;
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
            landmark = getIntent().getParcelableExtra(Constants.LANDMARK);
            double lat = landmark.getLatitude();
            double lng = landmark.getLongitude();
            landmarkLocation = new LatLng(lat, lng);
            Bundle arguments = new Bundle();
            arguments.putParcelable(Constants.LANDMARK,
                    landmark);
            arguments.putBoolean(LandmarkDetailFragment.DETAIL_TRANSITION_ANIMATION, true);
            LandmarkDetailFragment fragment = new LandmarkDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.landmark_detail_container, fragment)
                    .commit();


        Glide.with(this).load(landmark.getImage_url())
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivLandmark);
        ivLandmark.setContentDescription(landmark.getDescription_en());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, LandmarkListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onStreetViewClicked(Landmark landmark) {
        Intent streetViewIntent = new Intent(this, StreetViewActivity.class);
        streetViewIntent.putExtra(Constants.LANDMARK, landmark);
        startActivity(streetViewIntent);
    }
}
