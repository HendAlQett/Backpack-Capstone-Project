package com.hend.backpack.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.hend.backpack.R;

/**
 * Created by hend on 8/13/16.
 */
public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        //TODO: Check for the internet connection -- blank screen if not internet
        StreetViewPanoramaFragment streetViewPanoramaFragment=(StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);// new StreetViewPanoramaFragment();
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        LatLng latLng = new LatLng(30.0286342,31.2619385);
        streetViewPanorama.setPosition(latLng);
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder().bearing(180).build();
        streetViewPanorama.animateTo(camera,1000); //Duration

    }
}
