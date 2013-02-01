package com.voici.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;
import com.voici.R;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 24.01.13
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{


    private EditTextPreference server;
    private EditTextPreference key;
    private EditTextPreference score;
    private SharedPreferences prefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        server = (EditTextPreference) getPreferenceScreen().findPreference("key_server");
        key = (EditTextPreference) getPreferenceScreen().findPreference("key_key");
        score = (EditTextPreference) getPreferenceScreen().findPreference("key_score");
    }


    @Override
    protected void onResume() {
        super.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        server.setSummary(prefs.getString("key_server", ""));
        key.setSummary(prefs.getString("key_key", ""));
        score.setSummary(prefs.getString("key_score", ""));
        score.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                 return numberCheck(o);
            }
        });
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private boolean numberCheck(Object newValue) {
        try{
            Integer value = new Integer(newValue.toString());
            boolean result = value > 0 && value <= 100;
            if (!result)
                Toast.makeText(SettingsActivity.this, "Score value must be number between 0 and 100" , Toast.LENGTH_SHORT).show();
            return result;
        } catch (NumberFormatException e){
            Toast.makeText(SettingsActivity.this, "Score value must be number between 0 and 100" , Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String pref_key) {
        if (pref_key.equals("key_server")){
            server.setSummary(prefs.getString("key_server", ""));
        }else if (pref_key.equals("key_key")){
            key.setSummary(prefs.getString("key_key", ""));
        }else if (pref_key.equals("key_score")){
            score.setSummary(prefs.getString("key_score", ""));
        }
    }
}