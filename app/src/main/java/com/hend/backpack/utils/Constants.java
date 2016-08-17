package com.hend.backpack.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by hend on 8/17/16.
 */
public class Constants {
    public Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 10;

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();

    static {
//        // San Francisco International Airport.
//        BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
//
//        // Googleplex.
//        BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611, -122.0840577));

        // Test
        BAY_AREA_LANDMARKS.put("Home", new LatLng(30.003389,  31.316339));
    }
}
