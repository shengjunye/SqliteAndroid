package com.example.prcticasqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MovementDetailsActivity extends AppCompatActivity {

    private ArticleDataSource db;
    private long idArticle;

    private adapterArticleListFilter scArticle;

    private static String[] from = new String[]{ArticleDataSource.MOVEMENT_CODEARTICLE,ArticleDataSource.MOVEMENT_DATE,ArticleDataSource.MOVEMENT_QUANTITY,ArticleDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.lblCode,R.id.lblDate,R.id.lblQuantity,R.id.lblType};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);
        setTitle("Article activity");

        db = new ArticleDataSource(this);
        loadMovements();

        idArticle = this.getIntent().getExtras().getLong("id");



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void loadMovements() {
        // Demanem els articles disponibles
        Cursor cursorArticles = db.movementArticle(idArticle);

        // Now create a simple cursor adapter and set it to display
        scArticle = new adapterArticleListFilter(this, R.layout.movement_row_details, cursorArticles, from, to, 1);

        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setAdapter(scArticle);



    }


}

class adapterMovementList extends android.widget.SimpleCursorAdapter {
    private Context context;

    private static final String colorStockUnaviable = "#d78290";
    private static final String colorStockAvailable = "#d7d7d7";

    public adapterMovementList(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
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


                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(positionf);

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
                //context.showAlertDialogButtonClicked(view, "Modificar stock", stock);
            }
        });

        //Treure estoc
        img = (ImageView) view.findViewById(R.id.btnSubstractArticles);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"restar estoc");
                //context.showAlertDialogButtonClicked(view, "Modificar stock", stock);
            }
        });

        return view;
    }

}

