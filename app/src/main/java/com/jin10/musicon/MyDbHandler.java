package com.jin10.musicon;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SongDatabase.db";
    private static final String TABLE_NAME = "TagTable";
    private static final int DATABASE_VERSION = 1;
    private static final String COL_ID = "_id" ;
    private static final String COL_NAME = "_name" ;
    private static final String COL_Song = "_song" ;
    private static final String COL_Artist = "_artist" ;
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COL_NAME+ " TEXT, "+COL_Song+ " Text, " + COL_Artist + " TEXT);";
    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
    private Context c;

    public MyDbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        c = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void addData(String n,String s,String a){

        delData(n);

        ContentValues values = new ContentValues();
        values.put(COL_NAME,n);
        values.put(COL_Song,s);
        values.put(COL_Artist,a);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public void delData(String n){
        String[] values = {n};
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,COL_NAME + " = ?",values);
        db.close();
    }

    public String[] getVal(String n){

        SQLiteDatabase db = getWritableDatabase();
        String[] str = new String[2];
        str[0]="";
        str[1]="";
        String s="";
        String query = "SELECT * FROM " + TABLE_NAME ;

        Cursor cursor =db.rawQuery(query,null);


        cursor.moveToFirst();
        try{
        while (!cursor.isAfterLast())
        {
            String check = cursor.getString(cursor.getColumnIndex("_name"));
            if(check!=null&&check.equals(n)){
                //Toast.makeText(c, "ok", Toast.LENGTH_LONG).show();
                str[0] =  cursor.getString(cursor.getColumnIndex("_song"));
                str[1] =  cursor.getString(cursor.getColumnIndex("_artist"));
                //s=s+"\n";

            }
            cursor.moveToNext();
        }}
        catch (Exception e){

        }
        db.close();
        return str;
    }


}
