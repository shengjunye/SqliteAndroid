package com.example.prcticasqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class StockActivity extends Activity {
    private long idArticle;
    private int metode;
    private ArticleDataSource db;

    private static Calendar calendar = Calendar.getInstance();
    private static String dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_details);
        setTitle("Activity per afegir o treure estock");

        db = new ArticleDataSource(this);

        // Botones de aceptar y cancelar
        // Boton ok
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                acceptChanges();
            }
        });


        // Boton cancelar
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idArticle = this.getIntent().getExtras().getLong("id");
        metode = this.getIntent().getExtras().getInt("type");

        if (metode == -1) {
            // Restem al estoc
            loadData(metode);
        }
        else {
            loadData(metode);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData(int metode) {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        Cursor datos = db.article(idArticle);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodiMovement);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLE_CODEARTICLE)));

        //No es pot modificar el codi
        this.disableTextView(tv);

        tv = (TextView) findViewById(R.id.edtDate);
        tv.setText("-");

        tv = (TextView) findViewById(R.id.edtQuantitat);
        tv.setText("0");

        tv = (TextView) findViewById(R.id.edtType);
        disableTextView(tv);

        if (metode == -1) {
            tv.setText("Sortida");
        } else {
            tv.setText("Entrada");
        }


    }

    private void acceptChanges() {
        // Validem les dades
        TextView tv;

        // Codi ha d'estar informat
        tv = (TextView) findViewById(R.id.edtCodiMovement);
        String codi = tv.getText().toString();

        tv = (TextView) findViewById(R.id.edtDate);
        String date;

        try {
            date = "dd/MM/yyyy";
        }
        catch (Exception e) {
            myDialogs.showToast(this,"L'estoc ha de ser un valor numeric enter");
            return;
        }

        tv = (TextView) findViewById(R.id.edtQuantitat);
        int quantitat;
        try {
            quantitat = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"L'estoc ha de ser un valor numeric enter");
            return;
        }

        tv = (TextView) findViewById(R.id.edtType);
        String type = tv.getText().toString();


        db.movementAdd(idArticle,codi,date,quantitat,type);
        //Potser falta modificar per filtrar estoc i no estoc


        Intent mIntent = new Intent();
        mIntent.putExtra("id", idArticle);
        setResult(RESULT_OK, mIntent);

        myDialogs.showToast(this,"Moviment Afegit");
        finish();
    }

    private void cancelChanges() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idArticle);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void disableTextView(TextView editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
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
}
