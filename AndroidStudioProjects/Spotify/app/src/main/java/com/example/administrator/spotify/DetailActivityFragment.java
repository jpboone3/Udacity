package com.example.administrator.spotify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String mAlbum = null;
    private ArrayAdapter mArtistadApter = null;
    private ArrayList<StreamerArtist> artists = new ArrayList<StreamerArtist>();


    private MediaPlayer mediaPlayer;
    private MediaController mcontroller;
    private Handler handler = new Handler();

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ArrayList<String> ar = new ArrayList();
        ar.add("No Artist Tracks loaded");


        Bundle extras = getActivity().getIntent().getExtras();

        mAlbum = extras.getString("artist");

        // set the title/subtitle on the action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Top 10 Tracks");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(mAlbum);

        // instantiate our ArtistAdapter class
        mArtistadApter = new StreamerAdapter(getActivity(), R.layout.list_item_artist, artists);

        ListView lv = (ListView) rootView.findViewById(R.id.listview_artist);
        //lv.requestFocus();

        lv.setAdapter(mArtistadApter);

        //context = lv.getContext();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //parent,getItemAtPosition(position);
                StreamerArtist sa = (StreamerArtist) parent.getItemAtPosition(position);
                String text = sa.getName();


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putInt("pref_track_selected", position); //**syntax error on tokens**
                prefEditor.commit();


                Intent i = new Intent(getActivity(), AudioActivity.class);
                i.putExtra("album_name", mAlbum);
                i.putExtra("track_name", text);
                i.putExtra("track_url", sa.getPreviewUrl());
                i.putExtra("song_url", sa.getSongImageUtl());

                startActivity(i);

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTracks();
    }

    protected void updateTracks() {

        //boolean metric = preferences.getBoolean("pref_metric", true);
        DetailActivityFragment.TracklisttaSk task = new DetailActivityFragment.TracklisttaSk();
        //if (metric)
        if (mAlbum != null && mAlbum.length() > 0) {

            task.execute(mAlbum);
        }
    }

    public class TracklisttaSk extends AsyncTask<String, String, List> {

        static final String myLOGFILTER = "ArtisTlisttaSk";

        //static final String myLOGFILTER = ArtisTlisttaSk.getClass().getSimpleName();
        public TracklisttaSk() {
        }

        //protected String doInBackground(String... args) {}
        //protected String doInBackground(Object[] params) {
        protected List doInBackground(String... params) {

            String mQuery = params[0]; //.trim().replaceAll(" ", "%20");

            List<StreamerArtist> mSlist = new ArrayList<StreamerArtist>();

            SpotifyApi mApi = new SpotifyApi();
            if (mApi == null) {
                return null;
            }
            SpotifyService mService = mApi.getService();
            if (mService == null) {
                return null;
            }

            TracksPager mTm = mService.searchTracks(mQuery);
            List listOfArtists = mTm.tracks.items;

            for (int i = 0; i < listOfArtists.size(); i++) {

                StreamerArtist sa = new StreamerArtist();
                Track a = (Track) listOfArtists.get(i);
                sa.setName(a.name);
                //sa.popularity  a.popularity.intValue;
                sa.setPopularity(a.popularity);
                sa.setPreviewUrl(a.preview_url);
                sa.setSelected(false);

                if (a.album.images != null) {
                    // find the image that is 54 in height
                    String url = null;
                    int mLargest = 0;
                    for (int j = 0; j < a.album.images.size(); j++) {
                        if (a.album.images.get(j).height <= 64)
                            url = a.album.images.get(j).url;

                        if (mLargest < a.album.images.get(j).height) {
                            mLargest = a.album.images.get(j).height;
                            sa.setSongImageUtl(a.album.images.get(j).url);
                        }
                    }
                    if (url != null) {
                        try {
                            URL aURL = new URL(url);
                            URLConnection conn =
                                    aURL.openConnection();
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

                mSlist.add(sa);

            }
            // Need to return the top ten tracks based on popularity
            // First bubble sort popularity in reverse order
            Collections.sort(mSlist);

            // only want to retturn the top 10 tracks
            while (mSlist.size() > 10) {
                mSlist.remove(10);
            }

            return mSlist;
        }

        //@Override
        protected void onPostExecute(List l) {
            mArtistadApter.clear();
            if (l == null || l.size() == 0) {
                Toast.makeText(getActivity(), "No artists were found to display, please refine your search.", Toast.LENGTH_LONG).show();
                return;
            }
            int mTrackSelected = -1;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (preferences != null) {
                mTrackSelected = preferences.getInt("pref_track_selected", -1);
            }
            if (mTrackSelected >= 0) {
                StreamerArtist sa = (StreamerArtist) l.get(mTrackSelected);
                sa.setSelected(true);
            }
            mArtistadApter.addAll(l);
            super.onPostExecute(l);
        }

    }
}
