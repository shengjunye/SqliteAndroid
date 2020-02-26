package com.example.prcticasqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ArticleDataSource {

    public static final String table_ARTICLE = "articlelist";
    public static final String ARTICLE_ID = "_id";
    public static final String ARTICLE_CODEARTICLE = "codiarticle";
    public static final String ARTICLE_DESCRIPTION = "description";
    public static final String ARTICLE_PRICE = "price";
    public static final String ARTICLE_STOCK = "stock";

    //
    public static final String table_MOVEMENT = "movement";
    public static final String MOVEMENT_ID = "_id";
    public static final String MOVEMENT_CODEARTICLE = "codiarticle";
    public static final String MOVEMENT_IDARTICLE = "idarticle";
    public static final String MOVEMENT_DATE = "date";
    public static final String MOVEMENT_QUANTITY = "quantity";
    public static final String MOVEMENT_TYPE = "type";

    private articleHelper dbHelper;
    private SQLiteDatabase dbW, dbR;

    //Constructor
    public ArticleDataSource(Context ctx){
        //Comunicació directe amb la base de dades
        dbHelper = new articleHelper(ctx);

        //obrim 2 databases per llegir i escriure
        open();
    }

    //Destructor
    protected void finalize(){
        //Tanquem els databases
        dbR.close();
        dbW.close();
    }
    private void open(){
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    // ******************
    // Funcions que retornen cursors de todoList
    // ******************
    public Cursor articleList(){
        //Retornem totes les llistes
        return dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
                null, null, null, null, ARTICLE_ID);
    }

    public Cursor articleAmbEstoc(){
        //Retornem una llista amb la descripció indicada
        return dbR.rawQuery("SELECT * FROM " + table_ARTICLE + " WHERE " + ARTICLE_STOCK + " > 0 ORDER BY " + ARTICLE_ID, null);
        /*return dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
            ARTICLE_STOCK + ">?",new String[]{String.valueOf(0)}, null, null,ARTICLE_ID);*/
    }

    public Cursor articleSenseEstoc(){
        //Retornem una llista amb la descripció indicada
        return dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
                ARTICLE_STOCK + "<?", new String[]{String.valueOf(1)}, null, null,ARTICLE_ID);
    }
    public Cursor article(long id){
        //Retornem una llista amb la descripció indicada
        return dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
                ARTICLE_ID + "=?", new String[]{String.valueOf(id)}, null, null,null);
    }
    public boolean articleFind(String code){
        //Retornem l'element per veure si ja hi existeix a la llista
        boolean existeix = false;
        if (dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
                ARTICLE_CODEARTICLE + "=?", new String[]{code}, null, null,null).getCount() != 0){
            existeix = true;
        }
        return existeix;
    }

    public Cursor articleDescripcio(String description) {
        //Retornem una llista amb la descripció indicada
        return dbR.query(table_ARTICLE, new String[]{ARTICLE_ID,ARTICLE_CODEARTICLE,ARTICLE_DESCRIPTION,ARTICLE_PRICE,ARTICLE_STOCK},
                ARTICLE_DESCRIPTION + "LIKE?", new String[]{String.valueOf(description)}, null, null,null);
    }
    // ******************
    // Funciones de manipualación de datos taula articles
    // ******************
    public long articleAdd(String code, String description, float price, int stock) {
        // Afegim el article nou i retornem el id creat per si el necessiten
        ContentValues values = new ContentValues();
        values.put(ARTICLE_CODEARTICLE, code);
        values.put(ARTICLE_DESCRIPTION, description);
        values.put(ARTICLE_PRICE, price);
        values.put(ARTICLE_STOCK, stock);

        return dbW.insert(table_ARTICLE,null,values);
    }

    public void articleUpdate(long id, String description, float price, int stock) {

        ContentValues values = new ContentValues();
        values.put(ARTICLE_DESCRIPTION, description);
        values.put(ARTICLE_PRICE, price);
        values.put(ARTICLE_STOCK, stock);

        dbW.update(table_ARTICLE,values,ARTICLE_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public void articleStockUpdate(long id, int stock, int quantity) {
        ContentValues values = new ContentValues();

        values.put(ARTICLE_STOCK, stock + quantity);
        dbW.update(table_ARTICLE,values,ARTICLE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void articleDelete(long id){
        //Eliminem el article amb el codi indicat
        dbW.delete(table_ARTICLE,ARTICLE_ID + " = ?", new String[]{String.valueOf(id)});
    }
    // ******************
    // Funciones que retornen dades de moviments
    // ******************
    public Cursor movementList(){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " ORDER BY " + MOVEMENT_ID, null);
/*
        return dbR.query(table_MOVEMENT, new String[]{MOVEMENT_ID,MOVEMENT_DATE,MOVEMENT_IDARTICLE,MOVEMENT_QUANTITY,MOVEMENT_TYPE,MOVEMENT_CODEARTICLE},
                null, null, null, null, MOVEMENT_ID);

 */
    }
    public Cursor movementArticle(long id){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " WHERE " + MOVEMENT_IDARTICLE + " = " + id, null);

    }

    public Cursor movementBetweenDates(String DateInitial, String DateFinal){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " WHERE date BETWEEN '" + DateInitial + "' AND '" +
                DateFinal + "' ORDER BY "+ MOVEMENT_ID + " ASC",null);
    }

    public Cursor movementInitialDate(String DateInitial){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " WHERE date >= '" + DateInitial + "' ORDER BY " + MOVEMENT_ID +" ASC",null);
    }
    public Cursor movementFinalDate(String DateFinal){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " WHERE date <= '" + DateFinal + "' ORDER BY "+ MOVEMENT_ID + " ASC",null);
    }
    public Cursor movementFindDate(long id, String Date){
        //Retornem tota la llista d'aquell element
        return dbR.rawQuery("SELECT * FROM " + table_MOVEMENT + " WHERE date = '" + Date + "' " +
                "AND "+ MOVEMENT_IDARTICLE + " = " + id + " ORDER BY _id ASC",null);
    }

    // ******************
    // Funciones de manipualación de datos taula moviment
    // ******************

    public long movementAdd(long idarticle, String codeArticle, String date, int quantity, String type) {

        // Afegim el article nou i retornem el id creat per si el necessiten
        ContentValues values = new ContentValues();
        values.put(MOVEMENT_CODEARTICLE, codeArticle);
        values.put(MOVEMENT_DATE, date);
        values.put(MOVEMENT_QUANTITY, quantity);
        values.put(MOVEMENT_TYPE, type);
        values.put(MOVEMENT_IDARTICLE, idarticle);


        return dbW.insert(table_MOVEMENT,null,values);
    }

}
