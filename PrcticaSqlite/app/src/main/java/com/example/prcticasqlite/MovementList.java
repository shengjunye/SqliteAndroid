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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MovementList extends AppCompatActivity {
    private static Context context;
    final String USA_FORMAT = "yyyy/MM/dd";
    final Calendar c = Calendar.getInstance();
    private ArticleDataSource db;

    private adapterAllMovementLists scMoviment;
    private int dia,mes,any;

    private static String[] from = new String[]{ArticleDataSource.MOVEMENT_CODEARTICLE,ArticleDataSource.MOVEMENT_DATE,ArticleDataSource.MOVEMENT_QUANTITY,ArticleDataSource.MOVEMENT_TYPE};
    private static int[] to = new int[]{R.id.lblCode,R.id.lblDate,R.id.lblQuantity,R.id.lblType};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_list);
        MovementList.context = getApplicationContext();
        setTitle("Moviments de tots els articles");
        db = new ArticleDataSource(this);

        movementListAll();

        final TextView dataI,dataF;
        dataI = findViewById(R.id.edtDateInicio);
        dataF = findViewById(R.id.edtDateFinal);

        //Afegir estoc
        ImageView img = (ImageView) findViewById(R.id.btnCalendarInitial);
        //final TextView tv = (TextView) findViewById(R.id.edtDateInicio);

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"Data inicial");
                calendarDialog(dataI);
                filterDate(dataI, dataF);
            }
        });
        img = findViewById(R.id.btnResfreshInitial);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataI.setText("");
                filterDate(dataI,dataF);
            }
        });
        img = (ImageView) findViewById(R.id.btnCalendariFinal);
        //TextView tv2 = (TextView) findViewById(R.id.edtDateFinal);

        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialogs.showToast(context,"Data final");
                calendarDialog(dataF);
                filterDate(dataI, dataF);

            }
        });
        img = findViewById(R.id.btnRefreshFinal);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataF.setText("");
                filterDate(dataI,dataF);
            }
        });

    }

    private void filterDate(TextView edt1,TextView edt2){
        if (edt1.getText().length() > 1 && edt2.getText().length() > 1){
            Cursor cursorDates = db.movementBetweenDates(edt1.getText().toString(),edt2.getText().toString());
            scMoviment.changeCursor(cursorDates);
            scMoviment.notifyDataSetChanged();
        } else if (edt1.getText().length() > 1){
            Cursor cursorDates = db.movementInitialDate(edt1.getText().toString());
            scMoviment.changeCursor(cursorDates);
            scMoviment.notifyDataSetChanged();
        } else {
            Cursor cursorDates = db.movementFinalDate(edt2.getText().toString());
            scMoviment.changeCursor(cursorDates);
            scMoviment.notifyDataSetChanged();
        }
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
                    return;
                }
                edt.setText(diaUsa);

            }
        },any,mes,dia);
        datePickerDialog.show();
    }


    private void movementListAll(){
        // Demanem els articles disponibles

        Cursor cursorArticles = db.movementList();

        // Now create a simple cursor adapter and set it to display
        scMoviment = new adapterAllMovementLists(this, R.layout.movement_row_details, cursorArticles, from, to, 1);

        ListView list = (ListView) findViewById(R.id.idLlistaMovements);
        list.setAdapter(scMoviment);

    }

}

class adapterAllMovementLists extends android.widget.SimpleCursorAdapter {

    private static final String colorStockUnaviable = "#fc5d5d";
    private static final String colorStockAvailable = "#5dfc80";

    public adapterAllMovementLists(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int positionf = position;
        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor element = (Cursor) getItem(position);
        final String metode = element.getString(
                element.getColumnIndexOrThrow(ArticleDataSource.MOVEMENT_TYPE)
        );


        // Pintem el fons de la view segons hi ha estock o no
        if (metode.equalsIgnoreCase("Sortida")) {
            view.setBackgroundColor(Color.parseColor(colorStockUnaviable));
        } else {
            view.setBackgroundColor(Color.parseColor(colorStockAvailable));
        }


        return view;
    }
}


