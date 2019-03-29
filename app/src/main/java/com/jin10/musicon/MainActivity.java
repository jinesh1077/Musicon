package com.jin10.musicon;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;;
import com.jin10.musicon.MyService.musicBinder;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class MainActivity extends AppCompatActivity  {



        MyService musicSrv;
    boolean isBound = false;
    private Button detailButton,songMax,songMin;
    private ImageButton shuffleBtn,repeatBtn;
    public ArrayList<Song> songList;
    int songPos;
    private Intent playIntent;
    private boolean start = true, shuffle = false, repeat = false;
    private ImageButton playBtn;
    private String imgLoc;
    private File folder;
    private Bitmap bitmap;
    private ImageView songCover;
    private MyDbHandler dbHandler;
    private String[] lyr=new String[2];
    private LinearLayout lay;
    private SeekBar seekBar;
    private int mediaPos,mediaMax;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                return;
            }
        }
        lyr[0]="";
        lyr[1]="";
        detailButton = (Button) findViewById(R.id.songDetail);
        songMax = (Button) findViewById(R.id.songMax);
        songMin = (Button) findViewById(R.id.sondMin);
        playBtn = (ImageButton) findViewById(R.id.playButton);
        shuffleBtn = (ImageButton)findViewById(R.id.shuffleButton);
        repeatBtn = (ImageButton)findViewById(R.id.repeatButton);
        dbHandler = new MyDbHandler(this,null,null,1);
        lay= (LinearLayout)findViewById(R.id.BtnLay);
        seekBar=(SeekBar)findViewById(R.id.seekBar);

        songList = new ArrayList<Song>();

        getSongList();


        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.get_title().compareTo(b.get_title());
            }
        });


        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        songPos = sharedPref.getInt("posn", 0);

        //Toast.makeText(MainActivity.this,"abc "+songPos,Toast.LENGTH_SHORT).show();
        final ArrayList<Song> songList2=songList;
        //Toast.makeText(MainActivity.this,"abc "+songList,Toast.LENGTH_LONG).show();
        final Intent intent = new Intent(MainActivity.this, SongListActivity.class);
        intent.putExtra("list", songList);

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(intent, 234);
            }
        });




        detailButton.setText(songList.get(songPos).get_title()+"\n"+songList.get(songPos).get_artist()+"\n"+songList.get(songPos).get_album());

        folder = getExternalFilesDir("");

        File f= new File(folder, "coverOfSong");
        if (!f.exists())
            if (!f.mkdir()) {
                //Toast.makeText(this, folder+" can't be created.", Toast.LENGTH_LONG).show();

            } else {
                //Toast.makeText(this, folder+" can be created.", Toast.LENGTH_LONG).show();
            }
        else {
         //   Toast.makeText(this, folder + " already exits.", Toast.LENGTH_LONG).show();
        }
        String imgSelect = folder + "/coverOfSong/" +detailButton.getText().toString() + ".jpg";
        imgLoc = folder+"/coverOfSong/";

        bitmap = BitmapFactory.decodeFile(imgSelect);


        songCover = (ImageView) findViewById(R.id.cover);
        if(bitmap!=null){
        songCover.setImageBitmap(bitmap);
        }else {
            //Toast.makeText(this, songList.get(songPos).get_cover() + "", Toast.LENGTH_LONG).show();
            if(songList.get(songPos).get_cover()!=""){
                bitmap = BitmapFactory.decodeFile(songList.get(songPos).get_cover());
                songCover.setImageBitmap(bitmap);
            }
        }


        //lay.setBackground(songCover.getDrawable());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Art) {
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,44);
            return true;
        }

        if (id == R.id.Tag) {
            Intent iTag = new Intent(MainActivity.this,TagActivity.class);
            String songName="",aritstName="";

            iTag.putExtra("song",songName);
            iTag.putExtra("artist",aritstName);
            startActivityForResult(iTag,45);
            return true;
        }
        if (id == R.id.lyrics) {
            Intent ilyrics = new Intent(MainActivity.this,LyricsActivity.class);
            String s2=detailButton.getText().toString();
            lyr=dbHandler.getVal(s2);
            ilyrics.putExtra("song",lyr[0]);
            ilyrics.putExtra("artist",lyr[1]);
            startActivity(ilyrics);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ServiceConnection musicCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicBinder binder = (musicBinder) iBinder;
            musicSrv = binder.getService();
            musicSrv.makeList(songList);
            musicSrv.makeSong(songPos);
            musicSrv.setSongDetail(detailButton, playBtn,songCover,imgLoc,lay,seekBar,songMax,songMin);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    private void getSongList() {

        ContentResolver musicRes = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = musicRes.query(musicUri, null, null, null, null);


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


    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MyService.class);
            bindService(playIntent, musicCon, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void playButton(View view) {
        //musicSrv.makeSong(songPos);
        if (start) {
            musicSrv.playSong();
            //musicSrv.startSong();
            start = false;
        } else {

            musicSrv.pauseSong();
        }
        // Toast.makeText(MainActivity.this,"make "+songPos,Toast.LENGTH_SHORT).show();

    }

    public void nextButton(View view) {
        start = false;
        musicSrv.nextSong();


    }

    public void prevButton(View view) {

        start = false;
        musicSrv.prevSong();

    }

    public void shuffleButton(View view) {
        if (shuffle) {
            shuffle = false;
            shuffleBtn.setImageResource(R.drawable.shuffle);
        }
        else {
            shuffle = true;
            shuffleBtn.setImageResource(R.drawable.shuffletrue);
        }
        musicSrv.setShuffle(shuffle);

    }

    public void repeatButton(View view) {
        if (repeat) {
            repeat = false;
            repeatBtn.setImageResource(R.drawable.repeat);
        }
        else {
            repeat = true;
            repeatBtn.setImageResource(R.drawable.repeattrue);
        }
        musicSrv.setRepeat(repeat);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {

                songPos = data.getIntExtra("index", 0);
                //Toast.makeText(this, "make " + songPos, Toast.LENGTH_SHORT).show();
                detailButton.setText(songList.get(songPos).get_title());
                musicSrv.makeSong(songPos);
                musicSrv.playSong();

            }

            if (requestCode ==45){
                String str = detailButton.getText().toString();
                String _songName = data.getStringExtra("song");
                String _artistName = data.getStringExtra("artist");
                dbHandler.addData(str,_songName,_artistName);
                //Toast.makeText(this, str+" "+_songName+" "+_artistName, Toast.LENGTH_LONG).show();

                String[] s1 = new String[2];
                s1[0]="";s1[1]="";
                s1 = dbHandler.getVal(str);
                //Toast.makeText(this, s1[0]+" "+s1[1], Toast.LENGTH_LONG).show();

            }

        }
        if (requestCode == 44) {

            String stemp = getPath(data.getData());
            //String stemp = data.getData().getPath();
            File sFile = new File(stemp+"");
            String dtemp = detailButton.getText().toString();
            String dFile = folder +"/coverOfSong/"+dtemp+".jpg";
            Toast.makeText(this, dFile+"", Toast.LENGTH_LONG).show();

            File destFile = new File(dFile+"");


            try {
                copyFile(sFile, destFile);
            } catch (IOException e) {
                Toast.makeText(this, "not created", Toast.LENGTH_LONG).show();
            }
            bitmap = BitmapFactory.decodeFile(dFile);
            songCover = (ImageView) findViewById(R.id.cover);
            songCover.setImageBitmap(bitmap);

        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {


        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();

        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
        Toast.makeText(this, "created 2", Toast.LENGTH_SHORT).show();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }








}







