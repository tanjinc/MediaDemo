package com.tanjinc.mediademo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;


/**
 * Created by tanjincheng on 16/7/27.
 */
public class MusicPlayerService extends Service {

    public static boolean isServiceRunning;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Uri mUri;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }


    private void play(Uri uri) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(),uri);
            mMediaPlayer.prepare();

            int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    private OnAudioFocusChangeListener mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            if(mMediaPlayer != null) {

            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        mUri = intent.getData();
        play(mUri);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
