package com.tanjinc.mediademo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;


/**
 * Created by tanjincheng on 16/7/27.
 */
public class MusicPlayerService extends Service {

    public static boolean isServiceRunning;

    public static final String MSG_ACTION_UPDATE_VISUALIZER = "com.tanjinc.mediademo.update";

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Uri mUri;
    private Visualizer mVisualizer;
    private MyVisualizerView mVisualizerView;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    /**
     * 初始化频谱
     */
    private void setupVisualizer()
    {
        if (mVisualizer != null) {
            return;
        }
        // 以MediaPlayer的AudioSessionId创建Visualizer
        // 相当于设置Visualizer负责显示该MediaPlayer的音频数据
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        //专业的说这就是采样，该采样值一般为2的指数倍，如64,128,256,512,1024。
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 为mVisualizer设置监听器
        /*
         * Visualizer.setDataCaptureListener(OnDataCaptureListener listener, int rate, boolean waveform, boolean fft
         *
         *      listener，表监听函数，匿名内部类实现该接口，该接口需要实现两个函数
                rate， 表示采样的周期，即隔多久采样一次，联系前文就是隔多久采样128个数据
                iswave，是波形信号
                isfft，是FFT信号，表示是获取波形信号还是频域信号

         */
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener()
                {
                    //这个回调应该采集的是快速傅里叶变换有关的数据
                    @Override
                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] fft, int samplingRate)
                    {

                    }
                    //这个回调应该采集的是波形数据
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] waveform, int samplingRate)
                    {
                        Intent intent = new Intent();
                        intent.putExtra("byte", waveform);
                        intent.setAction(MSG_ACTION_UPDATE_VISUALIZER);
                        sendBroadcast(intent);
                    }
                }, Visualizer.getMaxCaptureRate() /4, true, false);
        mVisualizer.setEnabled(true);
    }

    public void play(Uri uri) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(),uri);
            mMediaPlayer.prepare();

            setupVisualizer();

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

    public void stop() {
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
        play(intent.getData());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        stop();
        mVisualizer.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
