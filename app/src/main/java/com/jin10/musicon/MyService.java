package com.jin10.musicon;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

public class MyService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private int songPosn;
    private ArrayList<Song> song;
    private MediaPlayer player;
    private boolean pause = false,flag=false,repeat=false,shuffle=false;
    private Button detailButton,playBtn,shuffleBtn,repeatBtn;
    private Random random;
    private Context context;
    private ImageView songCover;
    private String imgDest;
    private LinearLayout lay;



    private final IBinder binder = new musicBinder();

    public void onCreate(){
        super.onCreate();


        player = new MediaPlayer();
        random = new Random();

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class musicBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }



    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(player.getCurrentPosition()>0){
            mediaPlayer.reset();
            if(repeat)
                playSong();
            else
                nextSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }




    public void playSong(){
        player.reset();
        detailButton.setText(song.get(songPosn).get_title());
        long current = song.get(songPosn).getId();
        playBtn.setBackgroundResource(R.drawable.pause);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("posn",songPosn);
        editor.apply();

        String imgSelect = imgDest +detailButton.getText().toString() + ".jpg";

        Bitmap bitmap = BitmapFactory.decodeFile(imgSelect);
        songCover.setImageBitmap(bitmap);
        //lay.setBackground(songCover.getDrawable());

        Uri track = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,current);

        try{
            player.setDataSource(getApplicationContext(), track);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();


    }

    public void startSong(){
        player.prepareAsync();
    }

    public void setSongDetail(Button btn, Button btn2, ImageView imgView,String _str,LinearLayout _lay){
        detailButton = btn;
        playBtn = btn2;
        songCover = imgView;
        imgDest = _str;
        lay = _lay;
    }

    public void setAll(Context c){
        context = c;
    }

    public void pauseSong(){


        if(player.isPlaying()){
            player.pause();
            playBtn.setBackgroundResource(R.drawable.play);

        }
        else {

            player.start();
            playBtn.setBackgroundResource(R.drawable.pause);


        }

    }

    public void nextSong(){

        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=random.nextInt(song.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=song.size()) songPosn=0;
        }
        playSong();


    }

    public void prevSong(){
        songPosn--;
        if(songPosn<0)
            songPosn = song.size()-1;
        playSong();

    }

    public void setShuffle(boolean sfl){
        shuffle = sfl;
    }

    public void setRepeat(boolean rpt){
        repeat = rpt;
    }



    public void makeSong(int _posn){
        songPosn = _posn;
        //Toast.makeText(this,"make "+songPosn,Toast.LENGTH_SHORT).show();

    }

    public void makeList(ArrayList<Song> _list){
        song = _list;
    }


}
