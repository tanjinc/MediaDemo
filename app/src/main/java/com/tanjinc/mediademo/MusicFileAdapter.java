package com.tanjinc.mediademo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by tanjincheng on 16/7/27.
 */
public class MusicFileAdapter extends BaseAdapter {

    private ArrayList<MusicData> mMusicDataArrayList;
    private Context mContext;
    private MusicProviderAsyncTask mMusicProviderAsyncTask;
    private LayoutInflater mInflater;


    MusicFileAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMusicDataArrayList = new ArrayList<>();
        mMusicProviderAsyncTask = new MusicProviderAsyncTask();
        mMusicProviderAsyncTask.execute();
    }

    public ArrayList<MusicData> getMusicDataArrayList() {
        return mMusicDataArrayList;
    }
    @Override
    public int getCount() {
        return mMusicDataArrayList != null ? mMusicDataArrayList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mMusicDataArrayList != null ? mMusicDataArrayList.get(position): null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_view_item, null);

            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.music_title);
            viewHolder.path = (TextView) convertView.findViewById(R.id.music_path);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(mMusicDataArrayList.get(position).title);
        viewHolder.path.setText(mMusicDataArrayList.get(position).path);
        return convertView;
    }

    class MusicProviderAsyncTask extends AsyncTask<MusicData, MusicData, ArrayList<MusicData>> {

        @Override
        protected ArrayList<MusicData> doInBackground(MusicData... params) {
            ContentResolver contentResolver = mContext.getContentResolver();

            mMusicDataArrayList.clear();
            String[] projection = new String[]{
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA
            };

            Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null,MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            if (cursor != null) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {

                    MusicData musicData = new MusicData();
                    musicData.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    musicData.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    mMusicDataArrayList.add(musicData);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return mMusicDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicData> musicDatas) {
            notifyDataSetChanged();
            super.onPostExecute(musicDatas);
        }
    }

    class MusicData {
        String title;
        String path;
    }

    static class ViewHolder {
        TextView title;
        TextView path;
    }
}
