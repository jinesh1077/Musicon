package com.jin10.musicon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TagActivity extends AppCompatActivity {

    private int width,height;
    private Button tagButton;
    private EditText tagArtist,tagSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tagButton = (Button)findViewById(R.id.tagButton);
        tagArtist = (EditText)findViewById(R.id.tagArtist);
        tagSong = (EditText)findViewById(R.id.tagSong);

        String song,artist;
        song=getIntent().getStringExtra("song");
        artist=getIntent().getStringExtra("artist");

        tagArtist.setText(artist);
        tagSong.setText(song);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.7),(int)(height*0.3));

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TagActivity.this,LyricsActivity.class);
                i.putExtra("song",tagSong.getText().toString());
                i.putExtra("artist",tagArtist.getText().toString());

                setResult(45,i);
                setResult(RESULT_OK,i);
                finish();
            }
        });

    }
}
