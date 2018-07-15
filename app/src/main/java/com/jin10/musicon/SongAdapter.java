package com.jin10.musicon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    ArrayList<Song> _song;
    LayoutInflater _inflator;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        _song = songs;
        _inflator = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return _song.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout layout = (LinearLayout)_inflator.inflate(R.layout.list_design,viewGroup,false);

        TextView _title = (TextView)layout.findViewById(R.id.songTitle);
        TextView _artist = (TextView)layout.findViewById(R.id.songArtist);

        Song songPosition = _song.get(i);

        _title.setText(songPosition.get_title());
        _artist.setText(songPosition.get_artist());

        layout.setTag(i);

        return layout;
    }
}
