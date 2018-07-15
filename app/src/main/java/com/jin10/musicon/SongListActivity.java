package com.jin10.musicon;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SongListActivity extends AppCompatActivity {

    private ArrayList<Song> songList;
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songList = new ArrayList<Song>();
        songView=(ListView)findViewById(R.id.songView);
        getSongList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.get_title().compareTo(b.get_title());
            }
        });


        SongAdapter adapter = new SongAdapter(SongListActivity.this,songList);
        songView.setAdapter(adapter);


    }


    private void getSongList(){

        ContentResolver musicRes = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = musicRes.query(musicUri,null,null,null,null);

        if(cursor!=null&&cursor.moveToFirst()){

            int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);


            do{

                int _Id = cursor.getInt(songId);
                String _Title = cursor.getString(songTitle);
                String _Artist = cursor.getString(songArtist);
                songList.add(new Song(_Id, _Title, _Artist));
            }while (cursor.moveToNext());

        }

    }

    public void songSelected(View view){
        Intent i =  new Intent(SongListActivity.this,MainActivity.class);
        i.putExtra("index",Integer.parseInt(view.getTag().toString()));
       // i.putExtra("value",true);
        setResult(234,i);
        setResult(RESULT_OK, i);
        finish();
    }



}
