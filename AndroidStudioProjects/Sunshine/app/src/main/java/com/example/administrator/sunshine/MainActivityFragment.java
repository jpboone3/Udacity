package com.example.administrator.sunshine;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 *
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ShareActionProvider mShareActionProvider;

    private ArrayAdapter mForecastAdapter = null;
    //private Context context = null;

    public MainActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container);
        ArrayList<String> ar = new ArrayList();
        ar.add("Retrieving weather data");

        mForecastAdapter  = new ArrayAdapter(getActivity(),
                R.layout.listitem_forecast, R.id.list_item_forecast_textview, ar);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(mForecastAdapter );
        //context = lv.getContext();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //parent,getItemAtPosition(position);
                String text = parent.getItemAtPosition(position).toString();

                //Toast.makeText(getActivity(),text, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), DerailActivity .class);
                i.putExtra("weather", text);
                //i.putExtra(Intent.EXTRA_TEXT, text);


                startActivity(i);

            }
        });

        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.forcast_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

    }
    protected void updateWeather()
    {
        String location = "94043";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences != null)
            location = preferences.getString("pref_location", "94043");

        boolean metric = preferences.getBoolean("pref_metric", true);
        FetchWeatherTask task = new FetchWeatherTask();
        if (metric)
            task.execute("Y" + location);
        else
            task.execute("N" + location);
    }
    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                // menu.add(...) --> how to get the menu instance?

                //FetchWeatherTask task = new FetchWeatherTask();
                //task.execute("94043");
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class FetchWeatherTask extends AsyncTask<String, String, String []>
    {

        static final String myLOGFILTER = "FetchWeatherTask";
        //static final String myLOGFILTER = FetchWeatherTask.getClass().getSimpleName();
        public FetchWeatherTask() {
        }
        /*
                //@Override
                protected void onPreExecute()
                {
                    super.onPreExecute();
                }
                //@Override
                protected void onPostExecute(String result)
                {
                    super.onPostExecute(result);

                }
        */
        //@Override
        //protected String doInBackground(String... args) {}
        //protected String doInBackground(Object[] params) {
        protected String [] doInBackground(String... params)
        {
            String [] days = new String[7];
            String zip = params[0];

            boolean metric = false;

            if (zip.charAt(0) == 'Y')
            metric = true;
            zip = zip.substring(1);
            //String zip = "90266";
            //static final String myLOGFILTER = FetchWeatherTask.getClass().getSimpleName();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                Log.d("FetchWeatherTask", "ZIP: " + zip);

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", zip)
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7");
                Log.d("FetchWeatherTask", builder.build().toString());
                URL url = new URL(builder.build().toString());


                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + zip + "&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                //Log.d("FetchWeatherTask", "connect");
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

//Log.d("FetchWeatherTask", "inputstream");
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

//Log.d("FetchWeatherTask", buffer.toString());
                if (buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                try
                {

                    JSONObject jsonObj = new JSONObject(buffer.toString());

                    JSONArray arr = jsonObj.getJSONArray("list");
                    for (int i=0; i<arr.length(); i++)
                    {

                        JSONObject jsonObj2 = new JSONObject(arr.get(i).toString());
                        JSONObject jsonObj3 = jsonObj2.getJSONObject("temp");


                        JSONArray arr2 = jsonObj2.getJSONArray("weather");
                        JSONObject jsonObj4 = new JSONObject(arr2.get(0).toString());


                        double min = jsonObj3.getDouble("min");
                        double max = jsonObj3.getDouble("max");
                        long dt = jsonObj2.getLong("dt");

                        String main  = jsonObj4.getString("main");
                        String minmax = getReadableDateString(dt) + " - " + main + " - " + formatHighLows(metric, max, min);
                        days[i] = minmax;
//Log.d("fetch", "minmax: " + minmax);
                    }
                }
                catch (JSONException e)
                {
                    Log.e(myLOGFILTER, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.

                    return null;
                }


                //forecastJsonStr = buffer.toString();
            } catch (IOException e)
            {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally
            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return days;
        }

        @Override
        protected void onPostExecute(String[]  s)
        {
            mForecastAdapter.clear();
            if (s == null)
            {
                mForecastAdapter.add("Unable to acquire weather data");
                return;
            }
            mForecastAdapter.addAll(s);
/*
            for (int i = 0;i < s.length;i++)
            {
                if (s[i] != null)
                    mForecastAdapter.add(s[i]);
            }
*/
            //mForecastAdapter.add(s);
//Log.d("myFilter", "Post Execute")
            super.onPostExecute(s);
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time)
        {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat =
                    new SimpleDateFormat("EEE, MMM dd");
            return shortenedDateFormat.format(time * 1000);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(boolean metric, double high, double low)
        {
            // For presentation, assume the user doesn't care about tenths of a degree.
            if (metric == false)
            {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
         throws JSONException {

         // These are the names of the JSON objects that need to be extracted.
         final String OWM_LIST = "list";
         final String OWM_WEATHER = "weather";
         final String OWM_TEMPERATURE = "temp";
         final String OWM_MAX = "max";
         final String OWM_MIN = "min";
         final String OWM_DESCRIPTION = "main";

         JSONObject forecastJson = new JSONObject(forecastJsonStr);
         JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

         // OWM returns daily forecasts based upon the local time of the city that is being
         // asked for, which means that we need to know the GMT offset to translate this data
         // properly.

         // Since this data is also sent in-order and the first day is always the
         // current day, we're going to take advantage of that to get a nice
         // normalized UTC date for all of our weather.

         Time dayTime = new Time();
         dayTime.setToNow();

         // we start at the day returned by local time. Otherwise this is a mess.
         int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

         // now we work exclusively in UTC
         dayTime = new Time();

         String[] resultStrs = new String[numDays];
         for(int i = 0; i < weatherArray.length(); i++)
         {
         // For now, using the format "Day, description, hi/low"
         String day;
         String description;
         String highAndLow;

         // Get the JSON object representing the day
         JSONObject dayForecast = weatherArray.getJSONObject(i);

         // The date/time is returned as a long.  We need to convert that
         // into something human-readable, since most people won't read "1400356800" as
         // "this saturday".
         long dateTime;
         // Cheating to convert this to UTC time, which is what we want anyhow
         dateTime = dayTime.setJulianDay(julianStartDay+i);
         day = getReadableDateString(dateTime);

         // description is in a child array called "weather", which is 1 element long.
         JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
         description = weatherObject.getString(OWM_DESCRIPTION);

         // Temperatures are in a child object called "temp".  Try not to name variables
         // "temp" when working with temperature.  It confuses everybody.
         JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
         double high = temperatureObject.getDouble(OWM_MAX);
         double low = temperatureObject.getDouble(OWM_MIN);

         highAndLow = formatHighLows(metric, high, low);
         resultStrs[i] = day + " - " + description + " - " + highAndLow;
         }

         for (String s : resultStrs) {
         //Log.v(LOG_TAG, "Forecast entry: " + s);
         Log.v("myTag", "Forecast entry: " + s);
         }
         return resultStrs;

         }
         */
    }

}
