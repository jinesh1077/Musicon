package com.jin10.musicon;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SongListActivity extends AppCompatActivity {

    public ArrayList<Song> songList;
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        //Toast.makeText(this,"abcsdd ",Toast.LENGTH_LONG).show();

        //songList = new ArrayList<Song>();



        songList = (ArrayList<Song>) getIntent().getSerializableExtra("list");
        //Toast.makeText(this,"abcsdd "+songList,Toast.LENGTH_LONG).show();

        songView=(ListView)findViewById(R.id.songView);
       /* getSongList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.get_title().compareTo(b.get_title());
            }
        });
*/

        SongAdapter adapter = new SongAdapter(SongListActivity.this,songList);
        songView.setAdapter(adapter);


    }


    private void getSongList(){

        ContentResolver musicRes = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = musicRes.query(musicUri,null,null,null,null);

        if (cursor != null && cursor.moveToFirst() ) {

            int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumID = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {

                int _Id = cursor.getInt(songId);
                String _Title = cursor.getString(songTitle);
                String _Artist = cursor.getString(songArtist);
                String _Album = cursor.getString(songAlbum);
                String _albumId = cursor.getString(albumID);

                Cursor cursor2 = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID+ "=?",
                        new String[] {String.valueOf(_albumId)},
                        null);
                String _cover="";
                if (cursor2.moveToFirst()) {
                    _cover = cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    // do whatever you need to do
                }


                String _cover2="noth";
                songList.add(new Song(_Id, _Title, _Artist,_Album,_cover));
            } while (cursor.moveToNext());

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
