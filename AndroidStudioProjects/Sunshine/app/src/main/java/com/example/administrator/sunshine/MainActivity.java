package com.example.administrator.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends Activity {

    CheckBox prefCheckBox;
    TextView prefEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //prefCheckBox = (CheckBox)findViewById(R.id.prefCheckBox);
        //prefEditText = (TextView)findViewById(R.id.prefEditText);

        //loadPref();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //Log.d("onOptionsItemSelected", "yes");



        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        if (id == R.id.action_location)
        {
            /*
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String location = sharedPrefs.getString(
+                getString(R.string.pref_location_key),
+                getString(R.string.pref_location_default));
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(location);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
*/
            //String location = "90266";
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            String location = sharedPrefs.getString("pref_location", "94043");
            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location).build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                //Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

  /*
   * To make it simple, always re-load Preference setting.
   */

        //loadPref();
    }


}
