package com.tanjinc.mediademo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tanjincheng on 16/8/7.
 */
public class MusicPlayerActivity extends AppCompatActivity {

    @BindView(R.id.music_title)
    TextView mMusicTitle;
    @BindView(R.id.visualzer_view)
    MyVisualizerView mVisualzerView;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MusicPlayerService.MSG_ACTION_UPDATE_VISUALIZER:
                    if (mVisualzerView != null) {
                        mVisualzerView.updateVisualizer(intent.getExtras().getByteArray("byte"));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        mVisualzerView = (MyVisualizerView) findViewById(R.id.visualzer_view);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.MSG_ACTION_UPDATE_VISUALIZER);
        registerReceiver(mBroadcastReceiver, intentFilter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.setData(getIntent().getData());
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
