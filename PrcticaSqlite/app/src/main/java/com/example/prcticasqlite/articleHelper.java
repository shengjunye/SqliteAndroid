package com.example.prcticasqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class articleHelper extends SQLiteOpenHelper {

    //database version
    private static final int database_VERSION = 1;

    //database name
    private static final String database_NAME = "articleDatabase";

    //Table names
    private static final String TABLE_ARTICLE = "articlelist";
    private static final String TABLE_MOVEMENT = "movement";

    public articleHelper (Context context){
        super(context,database_NAME,null,database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ARTICLELIST =
                "CREATE TABLE "+ TABLE_ARTICLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "codiarticle_PK TEXT NOT NULL, " +
                    "description TEXT NOT NULL," +
                    "price FLOAT NOT NULL," +
                    "stock INTEGER NOT NULL)";

        String CREATE_TABLE_MOVEMENT =
                "CREATE TABLE " + TABLE_MOVEMENT + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "codiarticle_FK TEXT NOT NULL," +
                        "date TEXT NOT NULL," +
                        "quantity INTEGER NOT NULL, " +
                        "type CHAR NOT NULL," +
                        " FOREIGN KEY (codiarticle_FK) REFERENCES "+ TABLE_ARTICLE +"(codiarticle_PK))";

        db.execSQL(CREATE_TABLE_ARTICLELIST);
        db.execSQL(CREATE_TABLE_MOVEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVEMENT);

        //create new tables
        onCreate(db);
    }
}
