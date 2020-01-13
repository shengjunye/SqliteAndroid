package com.example.prcticasqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class articleHelper extends SQLiteOpenHelper {

    //database version
    private static final int database_VERSION = 1;

    //database name
    private static final String database_NAME = "articleDatabase";

    public articleHelper (Context context){
        super(context,database_NAME,null,database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTICLELIST =
                "CREATE TABLE articlelist ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "codiarticle TEXT, " +
                        "description TEXT," +
                        "price FLOAT," +
                        "stock INTEGER)";

        db.execSQL(CREATE_ARTICLELIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing
    }
}
