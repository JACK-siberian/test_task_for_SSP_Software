package com.jack.ImageGallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.jack.ImageGallery.MyUtil.Util;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
// TODO rework to fragments

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.setting_lp_animation_list)))
                updateListAnimationSummary();
        else if ( key.equals(getString(R.string.setting_et_slideshow_interval)))
                updateSlideshowIntervalSummary();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateListAnimationSummary();
        updateSlideshowIntervalSummary();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences( getApplicationContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateListAnimationSummary() {
        ListPreference listAnimation =
                (ListPreference) findPreference(getString( R.string.setting_lp_animation_list));
        listAnimation.setSummary(
                getString(R.string.setting_lp_animation_list_summary)
                        + " "
                        + listAnimation.getEntry());
    }
    private void updateSlideshowIntervalSummary() {
        EditTextPreference slideshowInterval =
                (EditTextPreference) findPreference(getString( R.string.setting_et_slideshow_interval));

        String intervalValue = slideshowInterval.getText();

        if ( !Util.isDigit(intervalValue) || Integer.parseInt(intervalValue) < 1) {
            intervalValue = getString(R.string.setting_et_slideshow_interval_default);
            Toast.makeText( this, " Please, enter count of sec ( min 1 )", Toast.LENGTH_LONG).show();
            slideshowInterval.setText( intervalValue);
        }
        slideshowInterval.setSummary(
                getString( R.string.setting_et_slideshow_interval_summary)
                        + " "
                        + intervalValue);
    }
}