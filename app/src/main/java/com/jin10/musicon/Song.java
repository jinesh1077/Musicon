package com.jin10.musicon;


public class Song {

    private int _id;
    private String _title;
    private String _artist;
    private String _album;
    private String _cover;


    public Song(int songId,String songTitle,String songArtist,String songAlbum,String songCover) {
        _id = songId;
        _artist = songArtist;
        _title = songTitle;
        _album = songAlbum;
        _cover = songCover;

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

    public String get_album(){
        return _album;
    }

    public String get_cover(){
        return _cover;
    }

}
