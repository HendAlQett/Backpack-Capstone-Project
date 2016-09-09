package com.hend.backpack.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.hend.backpack.R;
import com.hend.backpack.models.Landmark;
import com.hend.backpack.utils.Constants;
import com.hend.backpack.utils.Utility;

/**
 * Created by hend on 8/13/16.
 */
public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {
    Landmark landmark;
//    LatLng location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        if (Utility.isNetworkAvailable(this)) {
            landmark = getIntent().getParcelableExtra(Constants.LANDMARK);
            StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);// new StreetViewPanoramaFragment();
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        } else {
            Toast.makeText(this, R.string.internet_check, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        LatLng latLng = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        streetViewPanorama.setPosition(latLng);
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder().bearing(180).build();
        streetViewPanorama.animateTo(camera, 1000); //Duration

    }
}
