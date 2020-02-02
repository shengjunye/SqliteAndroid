package com.example.prcticasqlite;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static int ACTIVITY_ARTICLE_ADD = 1;
    private static int ACTIVITY_ARTICLE_UPDATE = 2;

    private static Calendar calendar = Calendar.getInstance();
    private static String dates;

    private ArticleDataSource db;

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
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnAdd:
                addArticle();
                return true;
            case R.id.btnMovementsList:
                movementListAll();
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

    private void movementListAll(){
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        Intent i = new Intent(this, MovementList.class);
        bundle.putLong("id",5);

        bundle.putLong("type",1);
        i.putExtras(bundle);
        this.startActivityForResult(i, 1);

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

        Intent i = new Intent(this, ArticleActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ARTICLE_ADD);
    }

    private void updateArticle(long id) {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

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
    private MainActivity context;

    private static final int ACTIVITY_STOCK_ADD = 1;
    private static final int ACTIVITY_STOCK_QUIT = 2;

    private static final String colorStockUnaviable = "#d78290";
    private static final String colorStockAvailable = "#d7d7d7";

    public adapterArticleListFilter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int positionf = position;
        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor element = (Cursor) getItem(position);
        final int stock = element.getInt(
                element.getColumnIndexOrThrow(ArticleDataSource.ARTICLE_STOCK)
        );
        final int idArticle = element.getInt(element.getColumnIndexOrThrow(ArticleDataSource.ARTICLE_ID));


        // Pintem el fons de la view segons hi ha estock o no
        if (stock <= 0) {
            view.setBackgroundColor(Color.parseColor(colorStockUnaviable));
        }
        else {
            view.setBackgroundColor(Color.parseColor(colorStockAvailable));
        }

        //historial
        ImageView img = (ImageView) view.findViewById(R.id.btnHistorial);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogs.showToast(context,"Dialog historial");


                View row = (View) v.getParent();

                ListView lv = (ListView) row.getParent();

                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                Intent myIntent = new Intent(context, MovementDetailsActivity.class);
                myIntent.putExtra("id", linia.getString(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLE_ID)));
                myIntent.putExtra("code", linia.getString(linia.getColumnIndexOrThrow(ArticleDataSource.ARTICLE_CODEARTICLE)));
                context.startActivity(myIntent);

            }
        });

        //Afegir estoc
        img = (ImageView) view.findViewById(R.id.btnAddArticles);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"afegir estoc");
                Movement(true, idArticle);
            }
        });

        //Treure estoc
        img = (ImageView) view.findViewById(R.id.btnSubstractArticles);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"restar estoc");
                Movement(false, idArticle);
            }
        });

        return view;
    }

    private void Movement(boolean afegir,int idArticle){

        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        Intent i = new Intent(context, StockActivity.class);
        bundle.putLong("id",idArticle);

        if (afegir) {
            bundle.putLong("type",1);
            i.putExtras(bundle);
            context.startActivityForResult(i, ACTIVITY_STOCK_ADD);
        } else {
            bundle.putLong("type",-1);
            i.putExtras(bundle);
            context.startActivityForResult(i, ACTIVITY_STOCK_QUIT);
        }

    }


}

