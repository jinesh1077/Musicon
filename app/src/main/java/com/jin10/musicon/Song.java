package com.jin10.musicon;


public class Song {

    private int _id;
    private String _title;
    private String _artist;


    public Song(int songId,String songTitle,String songArtist) {
        _id = songId;
        _artist = songArtist;
        _title = songTitle;

    }

    public int getId(){
        return _id;
    }

    public String get_title(){
        return _title;
    }

    public String get_artist(){
        return _artist;
    }

}
