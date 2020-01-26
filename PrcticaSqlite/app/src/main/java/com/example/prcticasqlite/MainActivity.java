package com.example.prcticasqlite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static int ACTIVITY_ARTICLE_ADD = 1;
    private static int ACTIVITY_ARTICLE_UPDATE = 2;

    private ArticleDataSource db;
    private long idActual;

    private adapterArticleListFilter scArticle;
    private filterKind filterActual;

    private static String[] from = new String[]{ArticleDataSource.ARTICLE_CODEARTICLE,ArticleDataSource.ARTICLE_DESCRIPTION,ArticleDataSource.ARTICLE_PRICE,ArticleDataSource.ARTICLE_STOCK};
    private static int[] to = new int[]{R.id.lblCode,R.id.lblDescription,R.id.lblPrice,R.id.lblStock};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Pràctica SqLite");

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new ArticleDataSource(this);
        loadArticles();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnAdd:
                addArticle();
                return true;
            case R.id.mnuTot:
                filterTot();
                return true;
            case R.id.mnuAmbStock:
                filterAmbStock();
                return true;
            case R.id.mnuSenseStock:
                filterSenseStock();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterTot() {
        // Demanem totes les tasques
        Cursor cursorArticles = db.articleList();
        filterActual = filterKind.FILTER_ALL;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scArticle.changeCursor(cursorArticles);
        scArticle.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setSelection(0);
    }

    private void filterAmbStock(){
        // Demanem els articles amb la següent descripció
        Cursor cursorArticles = db.articleAmbEstoc();
        filterActual = filterKind.FILTER_STOCKAVAILABLE;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scArticle.changeCursor(cursorArticles);
        scArticle.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setSelection(0);
    }
    private void filterSenseStock(){
        // Demanem els articles amb la següent descripció
        Cursor cursorArticles = db.articleSenseEstoc();
        filterActual = filterKind.FILTER_STOCKUNAVAILABLE;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scArticle.changeCursor(cursorArticles);
        scArticle.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setSelection(0);
    }

    private void filterSearch(){
        // Demanem els articles amb la següent descripció

        TextView tv = (TextView) findViewById(R.id.edtDescripcion);
        String descripcio = tv.getText().toString();

        Cursor cursorArticles = db.articleSenseEstoc();
        filterActual = filterKind.FILTER_STOCKUNAVAILABLE;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scArticle.changeCursor(cursorArticles);
        scArticle.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setSelection(0);
    }
    private void loadArticles() {

        // Demanem els articles disponibles
        Cursor cursorArticles = db.articleAmbEstoc();
        filterActual = filterKind.FILTER_ALL;

        // Now create a simple cursor adapter and set it to display
        scArticle = new adapterArticleListFilter(this, R.layout.row_details, cursorArticles, from, to, 1);

        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setAdapter(scArticle);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        // modifiquem el id
                        updateArticle(id);
                    }
                }
        );

    }
    private void refreshArticles() {

        Cursor cursorArticles = null;

        // Demanem els articles depenen del filtre que s'estigui aplicant
        switch (filterActual) {
            case FILTER_ALL:
                cursorArticles = db.articleList();
                break;
            case FILTER_STOCKAVAILABLE:
                cursorArticles = db.articleAmbEstoc();
                break;
            case FILTER_STOCKUNAVAILABLE:
                cursorArticles = db.articleSenseEstoc();
                break;
        }

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scArticle.changeCursor(cursorArticles);
        scArticle.notifyDataSetChanged();
    }
    private void addArticle(){

        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, ArticleActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ARTICLE_ADD);
    }

    private void updateArticle(long id) {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        idActual = id;

        Intent i = new Intent(this, ArticleActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ARTICLE_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_ARTICLE_ADD) {
            if (resultCode == RESULT_OK) {
                refreshArticles();
            }
        }

        if (requestCode == ACTIVITY_ARTICLE_UPDATE) {
            if (resultCode == RESULT_OK) {
                refreshArticles();
            }
        }

    }


}

class adapterArticleListFilter extends android.widget.SimpleCursorAdapter {
    private static final String colorStockUnaviable = "#d78290";
    private static final String colorStockAvailable = "#d7d7d7";

    public adapterArticleListFilter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor element = (Cursor) getItem(position);
        int stock = element.getInt(
                element.getColumnIndexOrThrow(ArticleDataSource.ARTICLE_STOCK)
        );

        // Pintem el fons de la view segons hi ha estock o no
        if (stock <= 0) {
            view.setBackgroundColor(Color.parseColor(colorStockUnaviable));
        }
        else {
            view.setBackgroundColor(Color.parseColor(colorStockAvailable));
        }

        return view;
    }
}

