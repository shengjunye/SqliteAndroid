package com.example.prcticasqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ArticleActivity extends Activity {
    private long codeArticle;
    private ArticleDataSource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

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

        // Boton eliminar
        Button  btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteArticle();
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
        codeArticle = this.getIntent().getExtras().getLong("id");

        if (codeArticle != -1) {
            // Si estem modificant carreguem les dades en pantalla
            loadData();
        }
        else {
            // Si estem creant amaguem el checkbox de finalitzada i el botó d'eliminar
            CheckBox chk = (CheckBox) findViewById(R.id.chkDeleteArticle);
            chk.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        // Això es podria fer amb un classe pero...
        Cursor datos = db.articleFind(codeArticle);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtCodi);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLE_CODEARTICLE)));

        tv = (TextView) findViewById(R.id.edtDescripcion);
        tv.setText(datos.getString(datos.getColumnIndex(ArticleDataSource.ARTICLE_DESCRIPTION)));

        tv = (TextView) findViewById(R.id.edtPreu);
        tv.setText(String.valueOf(datos.getFloat(datos.getColumnIndex(ArticleDataSource.ARTICLE_PRICE))));

        tv = (TextView) findViewById(R.id.edtEstoc);
        tv.setText(String.valueOf(datos.getInt(datos.getColumnIndex(ArticleDataSource.ARTICLE_STOCK))));

        //Sempre la tindrem desenmarcada
        CheckBox chk;
        chk = (CheckBox) findViewById(R.id.chkDeleteArticle);
        chk.setChecked(false);

    }

    private void acceptChanges() {
        // Validem les dades
        TextView tv;

        // Codi ha d'estar informat
        tv = (TextView) findViewById(R.id.edtCodi);
        String codi = tv.getText().toString();

        if (codi.trim().equals("") || codi.length() != 7) {
            myDialogs.showToast(this,"Has d'introduir un codi de 7 digits");
            return;
        }
        long codiL = 0;
        try {
            codiL = Long.parseLong(codi);
        } catch (Exception e){
            myDialogs.showToast(this, "El codi ha de ser només de numeros");
        }

        // Descripció no pot ser nul.la
        tv = (TextView) findViewById(R.id.edtDescripcion);
        String descripcio = tv.getText().toString();
        if (descripcio.trim().equals("")) {
            myDialogs.showToast(this, "No pots deixar un producte sense descripció");
            return;
        }

        tv = (TextView) findViewById(R.id.edtPreu);
        float preu = 0;
        try {
            preu = Float.parseFloat(tv.getText().toString().replaceAll(",","."));
        }
        catch (Exception e) {
            myDialogs.showToast(this,"El preu ha de ser un valor numeric");
            return;
        }
        if ((preu < 0)) {
            myDialogs.showToast(this, "Introdueix un preu vàlid");
            return;
        }

        tv = (TextView) findViewById(R.id.edtEstoc);
        int estoc;
        try {
            estoc = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"L'estoc ha de ser un valor numeric enter");
            return;
        }

        // Mirem si estem creant o estem guardant
        if (codeArticle == -1) {
            if (estoc < 0) {
                //Quan creem, el estoc no pot ser negatiu
                myDialogs.showToast(this,"El estoc no pot ser un numero negatiu");
                return;
            } else {
                codeArticle = db.articleAdd(codiL,descripcio,preu,estoc);
            }
        }
        else {
            db.articleUpdate(codiL,descripcio,preu,estoc);

            //Potser falta modificar per filtrar estoc i no estoc
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", codeArticle);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelChanges() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", codeArticle);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void deleteArticle() {

        final CheckBox chk;
        chk = (CheckBox) findViewById(R.id.chkDeleteArticle);

        // Si hem apretat al checkbox
        if (chk.isChecked()) {
            // Pedimos confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("¿Desitja eliminar el article?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.articleDelete(codeArticle);

                    Intent mIntent = new Intent();
                    mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                    setResult(RESULT_OK, mIntent);

                    finish();
                }
            });

            builder.setNegativeButton("No", null);

            builder.show();
        } else {
            myDialogs.showToast(this,"Accepti el checkbox per poder eliminar el article");
            return;
        }

    }
}
