package com.example.prcticasqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MovementDetailsActivity extends AppCompatActivity {
    private static Context context;
    private static int dia,mes,any;
    private ArticleDataSource db;
    private long idArticle;
    final String USA_FORMAT = "yyyy/MM/dd";
    final Calendar c = Calendar.getInstance();

    private adapterMovementArticle scArticle;

    private static String[] from = new String[]{ArticleDataSource.MOVEMENT_CODEARTICLE,ArticleDataSource.MOVEMENT_DATE,ArticleDataSource.MOVEMENT_QUANTITY,ArticleDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.lblCode,R.id.lblDate,R.id.lblQuantity,R.id.lblType};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);
        setTitle("Article activity");
        context = MovementDetailsActivity.this;
        db = new ArticleDataSource(this);
        idArticle = this.getIntent().getExtras().getLong("id");
        final TextView dataF;
        dataF = findViewById(R.id.edtDateFind);
        loadMovements();

        ImageView img = (ImageView) findViewById(R.id.btnCalendariFind);
        final TextView tv = (TextView) findViewById(R.id.edtDateInicio);

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calendarDialog(dataF);
                getArticlesByDay(dataF);
            }
        });

        img = findViewById(R.id.btnRefresh);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursorRefresh = db.movementArticle(idArticle);
                dataF.setText("");
                scArticle.changeCursor(cursorRefresh);
                scArticle.notifyDataSetChanged();

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void getArticlesByDay(TextView dataF){

        Cursor cursorFind = db.movementFindDate(idArticle,dataF.getText().toString());
        scArticle.changeCursor(cursorFind);
        scArticle.notifyDataSetChanged();

    }

    private void loadMovements() {
        db.movementArticle(idArticle);
        // Demanem els articles disponibles
        Cursor cursorArticles = db.movementArticle(idArticle);

        // Now create a simple cursor adapter and set it to display
        scArticle = new adapterMovementArticle(this, R.layout.movement_row_details, cursorArticles, from, to, 1);
        ListView list = (ListView) findViewById(R.id.idLlistaArticleMovement);
        list.setAdapter(scArticle);

    }


    public void calendarDialog(final TextView edt) {
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        any = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String diaUsa = year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(USA_FORMAT);
                    Date d = sdf.parse(diaUsa);
                    sdf.applyPattern(USA_FORMAT);
                    diaUsa = sdf.format(d);
                } catch (Exception e) {
                    myDialogs.showToast(context,"Error en canviar el tipus de data");
                    return;
                }
                edt.setText(diaUsa);

            }
        },any,mes,dia);
        datePickerDialog.show();
    }
}

class adapterMovementArticle extends android.widget.SimpleCursorAdapter {

    private static final String colorStockUnaviable = "#fc5d5d";
    private static final String colorStockAvailable = "#5dfc80";

    public adapterMovementArticle(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR

        Cursor element = (Cursor) getItem(position);
        final String type = element.getString(
                element.getColumnIndexOrThrow(ArticleDataSource.MOVEMENT_TYPE)
        );


        // Pintem el fons de la view segons hi ha estock o no
        if (!type.equalsIgnoreCase("Entrada")) {
            view.setBackgroundColor(Color.parseColor(colorStockUnaviable));
        }
        else {
            view.setBackgroundColor(Color.parseColor(colorStockAvailable));
        }

        return view;
    }

}
