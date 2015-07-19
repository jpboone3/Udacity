package com.example.administrator.spotify;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AudioActivity extends Activity
        implements MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl {

    private static final String TAG = "AudioActivity";

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private String audioFile;
    private String trackName;
    private String albumName;
    private String songImageUrl;
    private ImageView mSongImage;
    private boolean mMmediaCleaned = false;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio);

        songImageUrl = this.getIntent().getStringExtra("song_url");
        audioFile = this.getIntent().getStringExtra("track_url");
        trackName = this.getIntent().getStringExtra("track_name");
        albumName = this.getIntent().getStringExtra("album_name");

        ((TextView) findViewById(R.id.now_playing_album)).setText(albumName);

        ((TextView) findViewById(R.id.now_playing_text)).setText(trackName);

        mSongImage = (ImageView) findViewById(R.id.song_image);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        mediaController = new MediaController(this);

        AudioActivity.SongimagetaSk task = new AudioActivity.SongimagetaSk();
        if (songImageUrl != null && songImageUrl.length() > 0) {

            //Toast.makeText(getActivity(), "Seaching Spotify", Toast.LENGTH_LONG).show();
            task.execute(songImageUrl);
        }

        try {
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            // TO DO  logging for io exception
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMmediaCleaned == false) {
            pause();
            mMmediaCleaned = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMmediaCleaned) {
            start();
            mMmediaCleaned = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_audio_view));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

    public class SongimagetaSk extends AsyncTask<String, String, Bitmap> {

        public SongimagetaSk() {
        }

        protected Bitmap doInBackground(String... params) {

            String mUrl = params[0]; //.trim().replaceAll(" ", "%20");
            Bitmap mBitmap = null;
            try {
                URL aURL = new URL(mUrl);
                URLConnection conn =
                        aURL.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                mBitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                // TO DO logging of the bitmap load error
            }

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {

            // set the song image
            if (bm != null && mSongImage != null)
                mSongImage.setImageBitmap(bm);
            super.onPostExecute(bm);
        }

    }
}