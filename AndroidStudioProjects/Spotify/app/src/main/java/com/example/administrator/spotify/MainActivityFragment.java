package com.example.administrator.spotify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final int MAX_MARTISTS_TO_LIST = 40;
    private ArrayAdapter mArtistadApter = null;
    private ArrayList<StreamerArtist> martists = new ArrayList<StreamerArtist>();
    private EditText mArtist_name;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> ar = new ArrayList();
        ar.add("No Artist loaded");

        mArtist_name = (EditText) rootView.findViewById(R.id.artist_name);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences != null) {

            String name = preferences.getString("pref_artist", null);
            if (name != null)
                mArtist_name.setText(name);
        }

        // prevent the soft keyboard from showing at startup
        mArtist_name.setInputType(0);

        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        mArtist_name.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String name = preferences.getString("pref_artist", null);
                if (mArtist_name.equals(name) == false) {
                    SharedPreferences.Editor prefEditor = preferences.edit();
                    prefEditor.putString("pref_artist", mArtist_name.getText().toString()); //**syntax error on tokens**
                    prefEditor.putInt("pref_artist_selected", -1); //**syntax error on tokens**
                    prefEditor.commit();
                }

                updateMartists();
                return false;
            }
        });

        // instantiate our ArtistAdapter class
        mArtistadApter = new StreamerAdapter(getActivity(), R.layout.list_item_artist, martists);

        ListView mListView = (ListView) rootView.findViewById(R.id.listview_artist);

        mListView.setAdapter(mArtistadApter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StreamerArtist sa = (StreamerArtist) parent.getItemAtPosition(position);
                String text = sa.getName();


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putInt("pref_artist_selected", position); //**syntax error on tokens**
                prefEditor.putInt("pref_track_selected", -1); //**syntax error on tokens**
                prefEditor.commit();


                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("artist", text);

                startActivity(i);
            }
        });

        return rootView;
    }

    //@Override
    public void onBackPressed() {
        updateMartists();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMartists();
    }

    /**
     * method to invoke the building of the artist list
     */

    protected void updateMartists() {

        ArtisTlisttaSk task = new ArtisTlisttaSk();
        String mTemp = mArtist_name.getText().toString();
        if (mTemp != null && mTemp.length() > 0) {
            task.execute(mTemp);
        } else {
            mArtist_name.setInputType(InputType.TYPE_CLASS_TEXT);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                // menu.add(...) --> how to get the menu instance?
                mArtist_name.setInputType(InputType.TYPE_CLASS_TEXT);
                mArtist_name.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(mArtist_name, 0);
                }                //task.execute("94043");
                //updateMartists();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ArtisTlisttaSk extends AsyncTask<String, String, List> {

        static final String myLOGFILTER = "ArtisTlisttaSk";

        //static final String myLOGFILTER = ArtisTlisttaSk.getClass().getSimpleName();
        public ArtisTlisttaSk() {
        }

        protected List doInBackground(String... params) {
            String q = params[0].trim();

            List<StreamerArtist> sl = new ArrayList<StreamerArtist>();

            SpotifyApi api = new SpotifyApi();
            if (api == null)
                return null;
            SpotifyService spotify = api.getService();
            if (spotify == null)
                return null;

            ArtistsPager results = spotify.searchArtists(q);
            List listOfMartists = results.artists.items;

            for (int i = 0; i < listOfMartists.size(); i++) {
                StreamerArtist sa = new StreamerArtist();
                Artist a = (Artist) listOfMartists.get(i);
                sa.setName(a.name);
                sa.setSelected(false);

                if (a.images != null) {
                    // find the image that is 54 in height
                    String url = null;
                    for (int j = 0; j < a.images.size(); j++) {
                        if (a.images.get(j).height <= 64)
                            url = a.images.get(j).url;
                    }
                    if (url != null) {
                        //sa.thumbnail = null;
                        try {
                            URL aURL = new URL(url);
                            URLConnection conn =
                                    conn = aURL.openConnection();
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            sa.setThumbnail(BitmapFactory.decodeStream(bis));
                            bis.close();
                            is.close();
                        } catch (IOException e) {
                            // TO DO
                            Log.e("ArtistAdapter", "Error getting bitmap", e);
                        }

                    }

                }
                sl.add(sa);

                // limit the list to MAX_MARTISTS_TO_LIST
                if (i >= MAX_MARTISTS_TO_LIST)
                    break;
            }


            return sl;
        }

        @Override
        protected void onPostExecute(List l) {
            mArtistadApter.clear();
            mArtist_name.setInputType(InputType.TYPE_CLASS_TEXT);
            //Log.d("lengthg ", "length: " + s.length);
            if (l == null || l.size() == 0) {
                Toast.makeText(getActivity(), "No martists were found to display, refine your search.", Toast.LENGTH_LONG).show();
                return;
            }


            int mArtistSelected = -1;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (preferences != null) {
                mArtistSelected = preferences.getInt("pref_artist_selected", -1);
            }

            if (mArtistSelected >= 0) {
                StreamerArtist sa = (StreamerArtist) l.get(mArtistSelected);
                sa.setSelected(true);
            }

            //mArtistadApter.addAll(s);
            mArtistadApter.addAll(l);
            if (l.size() >= MAX_MARTISTS_TO_LIST)
                Toast.makeText(getActivity(), "Only showing first " + MAX_MARTISTS_TO_LIST + " martists that match the search.", Toast.LENGTH_LONG).show();
            //super.onPostExecute(l);
        }
    }
}
