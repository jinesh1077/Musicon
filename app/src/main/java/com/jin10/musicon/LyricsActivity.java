package com.jin10.musicon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LyricsActivity extends AppCompatActivity {



    private class MyTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String ... urls)
        {
            try
            {
                URL url = new URL(urls[0]);
                URLConnection uc = url.openConnection();
                //String j = (String) uc.getContent();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                return a.toString();
            }
            catch (Exception e)
            {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            //Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            Disp(result);
        }
    }




    private WebView webView;
    private String artist = "Taylor Swift";
    private String songname = "Everything has changed";
    private String base="http://www.metrolyrics.com/printlyric/";
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        Intent i = getIntent();
        artist = i.getStringExtra("artist");
        songname = i.getStringExtra("song");

        artist = artist.replaceAll(" ","-");
        songname = songname.replaceAll(" ","-");
        artist = artist.toLowerCase();
        songname = songname.toLowerCase();

        url = base + songname + "-lyrics-" + artist + ".html";

        webView = (WebView) findViewById(R.id.webview);
        MyTask taskLoad = new MyTask();
        taskLoad.execute(url);

  /*      webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);


        webView.loadUrl(url);
*/
    }




    public void Disp(String s){
        //Toast.makeText(this, s+"", Toast.LENGTH_SHORT).show();

        if(s.equals(url)){
            Toast.makeText(this, "Lyrics Not Found", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(this, "Display", Toast.LENGTH_SHORT).show();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setFocusable(true);
            webView.setFocusableInTouchMode(true);
            webView.loadUrl(url);
        }
    }


}
