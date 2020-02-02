package com.example.prcticasqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class articleHelper extends SQLiteOpenHelper {

    //database version
    private static final int database_VERSION = 2;

    //database name
    private static final String database_NAME = "articleDatabase";

    //Table names
    private static final String TABLE_ARTICLE = "articlelist";
    private static final String TABLE_MOVEMENT = "movement";

    public articleHelper (Context context){
        super(context,database_NAME,null,database_VERSION);
    }

    private String CREATE_TABLE_ARTICLELIST =
            "CREATE TABLE "+ TABLE_ARTICLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "codiarticle TEXT NOT NULL, " +
                    "description TEXT NOT NULL," +
                    "price FLOAT NOT NULL," +
                    "stock INTEGER NOT NULL)";

    private String CREATE_TABLE_MOVEMENT =
            "CREATE TABLE " + TABLE_MOVEMENT + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "codiarticle TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL, " +
                    "type CHAR NOT NULL," +
                    "idarticle INTEGER NOT NULL, " +
                    "FOREIGN KEY (idarticle) REFERENCES "+ TABLE_ARTICLE +"(_id)" +
                    "ON DELETE CASCADE)";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_ARTICLELIST);
        db.execSQL(CREATE_TABLE_MOVEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            // on upgrade older tables
            db.execSQL(TABLE_MOVEMENT);
        }

    }
}
