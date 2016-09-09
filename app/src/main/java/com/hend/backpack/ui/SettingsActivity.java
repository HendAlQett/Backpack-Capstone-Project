package com.hend.backpack.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.hend.backpack.R;

/**
 * Created by hend on 8/16/16.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

//    protected final static int PLACE_PICKER_REQUEST = 9090;
//    private ImageView mAttribution;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

    }

    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
       // else if (key.equals(getString(R.string.pref_location_key))) {
//            @SunshineSyncAdapter.LocationStatus int status = Utility.getLocationStatus(this);
//            switch (status) {
//                case SunshineSyncAdapter.LOCATION_STATUS_OK:
//                    preference.setSummary(stringValue);
//                    break;
//                case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
//                    preference.setSummary(getString(R.string.pref_location_unknown_description, value.toString()));
//                    break;
//                case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
//                    preference.setSummary(getString(R.string.pref_location_error_description, value.toString()));
//                    break;
//                default:
//                    // Note --- if the server is down we still assume the value
//                    // is valid
//                    preference.setSummary(stringValue);
//            }
        //}
        else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        setPreferenceSummary(preference, value);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(getString(R.string.pref_location_key))) {
//            // we've changed the location
//            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.remove(getString(R.string.pref_location_latitude));
//            editor.remove(getString(R.string.pref_location_longitude));
//            editor.commit();
//            // Remove attributions for our any PlacePicker locations.
//            if (mAttribution != null) {
//                mAttribution.setVisibility(View.GONE);
//            }
//            Utility.resetLocationStatus(this);
//            SunshineSyncAdapter.syncImmediately(this);
//        } else if (key.equals(getString(R.string.pref_units_key))) {
//            // units have changed. update lists of weather entries accordingly
//            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
//        } else if (key.equals(getString(R.string.pref_art_pack_key))) {
//            // art pack have changed. update lists of weather entries accordingly
//            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
//        } else if (key.equals(getString(R.string.pref_art_pack_key))) {
//            // art pack have changed. update lists of weather entries accordingly
//            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
//        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        //It will check the if the MainActivity in the stack
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

}
