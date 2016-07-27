package com.tanjinc.mediademo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mPlayBtn;
    private Button mStopBtn;
    private ListView mListView;
    private MusicFileAdapter mAdapter;

    private String mMusicPath = "/sdcard/12/天空之城.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPlayBtn = (Button) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(this);

        mStopBtn = (Button) findViewById(R.id.stop);
        mStopBtn.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.music_list_view);

        mAdapter = new MusicFileAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicPath = mAdapter.getMusicDataArrayList().get(position).path;
                playMusic(mMusicPath);
            }
        });
    }

    private void playMusic(String musicPath) {
        Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
        intent.setData(Uri.parse(musicPath));
        startService(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                mMusicPath = mAdapter.getMusicDataArrayList().get(0).path;
                playMusic(mMusicPath);
                break;
            case R.id.stop:
                if (MusicPlayerService.isServiceRunning) {
                    stopService(new Intent(MainActivity.this, MusicPlayerService.class));
                }
                break;
        }
    }
}
