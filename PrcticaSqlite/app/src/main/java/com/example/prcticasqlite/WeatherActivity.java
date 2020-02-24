package com.example.prcticasqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String apiKey = "3f52d2d11c829a437994a8092fed04ea";
    private static final String apiWeb = "http://api.openweathermap.org/data/2.5/weather?q=";
    private JSONObject tempsCiutat = null;
    private static EditText edt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setTitle("El temps");

        edt = findViewById(R.id.lblWeatherSearch);

        Button btn = findViewById(R.id.btnSearch);
        btn.setOnClickListener(this);

        setVisible(false);

    }


    private void buscar() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        client.get( apiWeb + edt.getText() + "&APPID=" + apiKey, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);

                try {
                    tempsCiutat = new JSONObject(str);
                    TextView tv = findViewById(R.id.lblWeatherCity);
                    tv.setText(edt.getText());

                    tv = findViewById(R.id.lblWeatherDescrip);
                    String st = tempsCiutat.getJSONArray("weather").getJSONObject(0).getString("main");
                    tv.setText(st);

                    tv = findViewById(R.id.lblWeatherTemp);
                    double num = tempsCiutat.getJSONObject("main").getDouble("temp");
                    tv.setText(String.valueOf(Math.round(num - 273.15)*100.0/100.0) + "º");

                    tv = findViewById(R.id.lblWeatherMinTemp);
                    num = tempsCiutat.getJSONObject("main").getDouble("temp_min");
                    tv.setText(String.valueOf(Math.round(num - 273.15)*100.0/100.0) + "º");

                    tv = findViewById(R.id.lblWeatherMaxTmp);
                    num = tempsCiutat.getJSONObject("main").getDouble("temp_max");
                    tv.setText(String.valueOf(Math.round(num - 273.15)*100.0/100.0) + "º");

                    st = tempsCiutat.getJSONArray("weather").getJSONObject(0).getString("icon");
                    URL url = new URL("https://openweathermap.org/img/wn/"+ st +"@2x.png");
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ImageView img = findViewById(R.id.imageWeather);
                    img.setImageBitmap(bmp);

                    setVisible(true);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String str = new String(error.getMessage().toString());
                String valor = str;

                Toast.makeText(getApplicationContext(), valor,Toast.LENGTH_LONG).show();
                return;
            }

        });

    }

    public void setVisible(boolean visible) {
        if (visible) {
            TextView tv = findViewById(R.id.labelTemperatura);
            tv.setVisibility(View.VISIBLE);

            tv = findViewById(R.id.labelTemperaturaMax);
            tv.setVisibility(View.VISIBLE);

            tv = findViewById(R.id.labelTemperaturaMin);
            tv.setVisibility(View.VISIBLE);

            ImageView img = findViewById(R.id.imageWeather);
            img.setVisibility(View.VISIBLE);

        } else {
            TextView tv = findViewById(R.id.labelTemperatura);
            tv.setVisibility(View.GONE);

            tv = findViewById(R.id.labelTemperaturaMax);
            tv.setVisibility(View.GONE);

            tv = findViewById(R.id.labelTemperaturaMin);
            tv.setVisibility(View.GONE);

            ImageView img = findViewById(R.id.imageWeather);
            img.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSearch: buscar(); break;
        }
    }


}