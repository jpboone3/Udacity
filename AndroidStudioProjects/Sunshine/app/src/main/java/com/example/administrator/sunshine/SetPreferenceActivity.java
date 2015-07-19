package com.example.administrator.sunshine;

import com.example.administrator.sunshine.MainActivityFragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;


public class SetPreferenceActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{


//implements SharedPreferences.OnSharedPreferenceChangeListener, OnItemClickListener


    SharedPreferences.OnSharedPreferenceChangeListener prefListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//if (prefListener != null)
        Log.d("preference", "listerner is active");


        preferences.registerOnSharedPreferenceChangeListener(this);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("pref_location"))
        {
            //String location = sharedPreferences.getString(key, "00000");
            //FetchWeatherTask task = new FetchWeatherTask();
            //task.execute(location);
        }

        Log.d("XXXXpreference", "Listener per preferenze changed. key: " + key);

        Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(), "preference", Toast.LENGTH_LONG);
    }

    @Override
    public void onResume() {
        super.onResume();

        // PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

        Log.d("preference", "Listener per preferenze registrato.");
    }

    @Override
    public void onPause() {
        super.onPause();

        // PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        // getSActivity().getDefaultSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        Log.d("preference", "Listener per preferenze de-registrato.");
    }


}