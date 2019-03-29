package com.jin10.musicon;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MyService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener , SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener {




        private int songPosn;
    private ArrayList<Song> song;
    private MediaPlayer player;
    private Button detailButton,songMax,songMin;
    private boolean pause = false, flag = false, repeat = false, shuffle = false;
    private ImageButton playBtn, shuffleBtn, repeatBtn;
    private Random random;
    private Context context;
    private ImageView songCover;
    private String imgDest;
    private LinearLayout lay;
    private SeekBar seekBar;
    private int mediaPos,mediaMax;
    private Handler handler;


    private final IBinder binder = new musicBinder();

    public void onCreate() {
        super.onCreate();


        player = new MediaPlayer();
        random = new Random();
        handler= new Handler();
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

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Toast.makeText(this, i+"", Toast.LENGTH_LONG).show();



    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(moveSeekBarThread);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(moveSeekBarThread);

    }

    public class musicBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();

        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (player.getCurrentPosition() > 0) {
            mediaPlayer.reset();
            if (repeat)
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
    public void onPrepared(final MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPos=mediaPlayer.getCurrentPosition();
        mediaMax=mediaPlayer.getDuration();
        seekBar.setMax(mediaMax);
        seekBar.setProgress(mediaPos);

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){

                    player.seekTo(i);
                    seekBar.setProgress(i);



                }

                long millis=player.getCurrentPosition();
                int seconds= (int) ((millis/1000)%60);

                int minutes= (int) (((millis-seconds)/1000)/60);
                if(seconds<10){
                    songMin.setText(minutes+":0"+seconds);

                }else{
                    songMin.setText(minutes+":"+seconds);

                }

                millis=player.getDuration();
                seconds= (int) ((millis/1000)%60);

                minutes= (int) (((millis-seconds)/1000)/60);
                if(seconds<10){
                    songMax.setText(minutes+":0"+seconds);

                }else{
                    songMax.setText(minutes+":"+seconds);

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



            }
        });
        //Toast.makeText(this, mediaMax+"", Toast.LENGTH_LONG).show();

    }

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if(player.isPlaying()){

                int mediaPos_new = player.getCurrentPosition();
                int mediaMax_new = player.getDuration();
                seekBar.setMax(mediaMax_new);
                seekBar.setProgress(mediaPos_new);

                long millis=player.getCurrentPosition();
                int seconds= (int) ((millis/1000)%60);

                int minutes= (int) (((millis-seconds)/1000)/60);
                if(seconds<10){
                    songMin.setText(minutes+":0"+seconds);

                }else{
                    songMin.setText(minutes+":"+seconds);

                }

                millis=player.getDuration();
                seconds= (int) ((millis/1000)%60);

                minutes= (int) (((millis-seconds)/1000)/60);
                if(seconds<10){
                    songMax.setText(minutes+":0"+seconds);

                }else{
                    songMax.setText(minutes+":"+seconds);

                }

                handler.postDelayed(this, 100); //Looping the thread after 0.1 second
            }

        }
    };


    public void playSong() {
        player.reset();
        detailButton.setText(song.get(songPosn).get_title()+"\n"+song.get(songPosn).get_artist()+"\n"+song.get(songPosn).get_album());
        long current = song.get(songPosn).getId();
        playBtn.setImageResource(R.drawable.pause);

        SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("posn", songPosn);
        editor.apply();

        String imgSelect = imgDest + song.get(songPosn).get_title() + ".jpg";

        Bitmap bitmap = BitmapFactory.decodeFile(imgSelect);
        if (bitmap != null) {
            songCover.setImageBitmap(bitmap);
        }else{
            if(song.get(songPosn).get_cover()!=null){
                bitmap = BitmapFactory.decodeFile(song.get(songPosn).get_cover());
                songCover.setImageBitmap(bitmap);
            }else
            songCover.setImageResource(R.drawable.unk);
        }



        //lay.setBackground(songCover.getDrawable());



        Uri track = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,current);

        try{

            player.setDataSource(getApplicationContext(), track);



        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        /*
*/
        seekFunction();

    }

    private void seekFunction() {

    }

    public void startSong(){
        player.prepareAsync();
    }

    public void setSongDetail(Button btn, ImageButton btn2, ImageView imgView,
                              String _str,LinearLayout _lay,SeekBar _seekBar,
                              Button _songMax,Button _songMin){
        detailButton = btn;
        playBtn = btn2;
        songCover = imgView;
        imgDest = _str;
        lay = _lay;
        seekBar=_seekBar;
        songMax=_songMax;
        songMin=_songMin;




    }

    public void setAll(Context c){
        context = c;
    }

    public void pauseSong(){


        if(player.isPlaying()){
            player.pause();
            playBtn.setImageResource(R.drawable.play);

        }
        else {

            player.start();
            mediaPos=player.getCurrentPosition();
            mediaMax=player.getDuration();
            seekBar.setMax(mediaMax);
            seekBar.setProgress(mediaPos);

            handler.removeCallbacks(moveSeekBarThread);
            handler.postDelayed(moveSeekBarThread, 100);

            playBtn.setImageResource(R.drawable.pause);


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
