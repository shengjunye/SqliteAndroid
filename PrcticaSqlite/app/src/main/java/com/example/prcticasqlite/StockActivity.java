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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StockActivity extends Activity {
    private Context context;
    private long idArticle;
    private String metode;
    private int stock;
    private ArticleDataSource db;
    final String USA_FORMAT = "yyyy/MM/dd";
    final Calendar c = Calendar.getInstance();

    private static int dia,mes,any;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_details);
        setTitle("Activity per afegir o treure estock");

        db = new ArticleDataSource(this);
        context = StockActivity.this;
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
        metode = this.getIntent().getExtras().getString("type");
        stock = this.getIntent().getExtras().getInt("stock");
        loadData();

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

    private void loadData() {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        Cursor datos = db.article(idArticle);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodiMovement);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLE_CODEARTICLE)));

        //No es pot modificar el codi
        this.disableTextView(tv);

        final TextView tvC = (TextView) findViewById(R.id.edtDate);
        disableTextView(tvC);
        Button img = (Button) findViewById(R.id.btnCalendari);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendarDialog(tvC);
            }
        });

        tv = (TextView) findViewById(R.id.edtQuantitat);
        tv.setText("");

        tv = (TextView) findViewById(R.id.edtType);
        disableTextView(tv);
        tv.setText(metode);

    }

    private void acceptChanges() {
        // Validem les dades
        TextView tv;

        // Codi ha d'estar informat
        tv = (TextView) findViewById(R.id.edtCodiMovement);
        String codi = tv.getText().toString();

        tv = (TextView) findViewById(R.id.edtDate);
        String diaUsa = tv.getText().toString();
        if (diaUsa.isEmpty()) {
            diaUsa = (String.valueOf(c.get(Calendar.YEAR))+"/"+String.valueOf(c.get(Calendar.MONTH))+"/"+String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
        }

        tv = (TextView) findViewById(R.id.edtQuantitat);
        int quantitat;
        try {
            quantitat = Integer.valueOf(tv.getText().toString());
            if (metode.equals("Sortida")) {
                quantitat = -quantitat;
            }
        }
        catch (Exception e) {
            myDialogs.showToast(this,"La quantitat introduida ha de ser un valor numeric enter");
            return;
        }

        tv = (TextView) findViewById(R.id.edtType);
        String type = tv.getText().toString();


        db.movementAdd(idArticle,codi,diaUsa,quantitat,type);

        db.articleStockUpdate(idArticle,stock,quantitat);

        myDialogs.showToast(this,"Moviment Afegit");
        //Potser falta modificar per filtrar estoc i no estoc

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
