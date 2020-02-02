package com.example.prcticasqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class MovementList extends AppCompatActivity {
    private static Context context;
    private long idArticle;
    private int metode;
    private ArticleDataSource db;

    private adapterArticleListFilter scMoviment;
    private static Calendar calendar = Calendar.getInstance();
    private static String dates;

    private static String[] from = new String[]{ArticleDataSource.MOVEMENT_CODEARTICLE,ArticleDataSource.MOVEMENT_DATE,ArticleDataSource.MOVEMENT_QUANTITY,ArticleDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.lblCode,R.id.lblDate,R.id.lblQuantity,R.id.lblType};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_list);
        MovementList.context = getApplicationContext();

        //Afegir estoc
        ImageView img = (ImageView) findViewById(R.id.btnCalendarInitial);
        final TextView tv = (TextView) findViewById(R.id.edtDateInicio);

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"Data inicial");
                Dialog(context, tv);
            }
        });
        img = (ImageView) findViewById(R.id.btnCalendariFinal);
        TextView tv2 = (TextView) findViewById(R.id.edtDateFinal);

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"Data final");
                Dialog(context, tv);
            }
        });


    }

    public static void Dialog(Context contex, final TextView edt){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(contex, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int years, int months, int days) {

                if (months < 10 && days < 10) {
                    dates = "0" + days + "/" + "0" + (months + 1) + "/" + years;
                } else if (months < 10) {
                    dates = days + "/" + "0" + (months + 1) + "/" + years;
                } else if (days < 10) {
                    dates = "0" + days + "/" + (months + 1) + "/" + years;
                } else {
                    dates = days + "/" + (months + 1) + "/" + years;
                }

                edt.setText(dates);

            }
        },year,month,day);
        datePickerDialog.show();

    }


    private void movementListAll(){
        // Demanem els articles disponibles
        Cursor cursorArticles = db.articleAmbEstoc();

        // Now create a simple cursor adapter and set it to display
        scMoviment = new adapterArticleListFilter(this, R.layout.movement_row_details, cursorArticles, from, to, 1);

        ListView list = (ListView) findViewById(R.id.idLlista);
        list.setAdapter(scMoviment);

    }

}

class adapterMovementLists extends android.widget.SimpleCursorAdapter {
    private Context context;

    private static final String colorStockUnaviable = "#d78290";
    private static final String colorStockAvailable = "#d7d7d7";

    public adapterMovementLists(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
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
        TextView tv = (TextView)

        return view;
    }

}


