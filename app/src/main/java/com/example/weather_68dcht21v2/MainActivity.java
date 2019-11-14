package com.example.weather_68dcht21v2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText edtSearch;
    Button btnSearch, btnThayDoiNgay;
    TextView txtThanhPho, txtQuocGia, txtNhietDo,txtNgayGioCapNhat, txtTrangThai, txtDoAm, txtMay, txtGio, txtSatusAir, txtStatusUV, txtValueAir, txtValueUV;
    ImageView imgTrangThai, imgFace;
    String City = "HaNoi";
    String Id_location = "353412";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        GetCurrentWeatherData(City);
        GetAirAndPollen(Id_location);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtSearch.getText().toString();
                if (city.equals("")) {
                    GetCurrentWeatherData(City);
                    GetAirAndPollen(Id_location);
                }else {
                    City = city;
                    GetCurrentWeatherData(City);
                    if (city.toLowerCase().equals("hanoi")) {
                        Id_location = "353412";
                    }else if (city.toLowerCase().equals("danang")){
                        Id_location = "352954";
                    }else if (city.toLowerCase().equals("hochiminh")) {
                        Id_location = "353981";
                    }
                    GetAirAndPollen(Id_location);
                }

            }
        });
        btnThayDoiNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtSearch.getText().toString();
                Intent intent = new Intent(MainActivity.this, ThoiTietActivity.class);
                intent.putExtra("key", city);
                startActivity(intent);
            }
        });
    }

    public void GetCurrentWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&appid=4cfa017f7ae8871934251cb53b5f651b";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String ngay = jsonObject.getString("dt");
                            String name = jsonObject.getString("name");
                            txtThanhPho.setText("Tên thành phố: " + name);
                            long l = Long.valueOf(ngay);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM-dd-yyyy HH:mm" );
                            String Ngay = simpleDateFormat.format(date);
                            txtNgayGioCapNhat.setText(Ngay);
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            String trangthai = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            Picasso.get().load("http://openweathermap.org/img/wn/"+icon+".png").into(imgTrangThai);
                            txtTrangThai.setText(trangthai);
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String nhietdo = jsonObjectMain.getString("temp");
                            String doam = jsonObjectMain.getString("humidity");

                            Double nd = Double.valueOf(nhietdo);
                            String NhietDo = String.valueOf(nd.intValue());
                            txtNhietDo.setText(NhietDo+"°C");
                            txtDoAm.setText(doam+"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            txtGio.setText(wind+"m/s");

                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                            String may = jsonObjectCloud.getString("all");
                            txtMay.setText(may+"%");

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String quocgia = jsonObjectSys.getString("country");
                            txtQuocGia.setText("Quốc gia: "+quocgia);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    private void GetAirAndPollen(String id_location) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://dataservice.accuweather.com/forecasts/v1/daily/1day/"+id_location+"?apikey=FfmdXCWXZvWrFTIPrkofSas8glCXLEJE&language=vi&details=true&metric=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayDaliForecasts = jsonObject.getJSONArray("DailyForecasts");
                    JSONObject jsonObjectDaliForecasts = jsonArrayDaliForecasts.getJSONObject(0);

                    JSONArray jsonArrayAirAndPollen = jsonObjectDaliForecasts.getJSONArray("AirAndPollen");

                    JSONObject jsonObjectAirQuality =jsonArrayAirAndPollen.getJSONObject(0);
                    int ValueAir = Integer.parseInt(jsonObjectAirQuality.getString("Value"));
                    String StatusAir = jsonObjectAirQuality.getString("Category");

                    txtValueAir.setText(ValueAir + " US AQI");
                    txtSatusAir.setText(StatusAir);
                    if ((ValueAir >= 0) && (ValueAir < 50)) {
                        imgFace.setImageResource(R.drawable.face_green);
                    }else if (ValueAir >= 50 && ValueAir < 100) {
                        imgFace.setImageResource(R.drawable.face_yellow);
                    }else if (ValueAir >= 100 && ValueAir < 130) {
                        imgFace.setImageResource(R.drawable.face_orange);
                    } else  if(ValueAir >= 130) {
                        imgFace.setImageResource(R.drawable.face_red);
                    }

                    JSONObject jsonObjectUVIndex = jsonArrayAirAndPollen.getJSONObject(5);
                    String ValueUV = jsonObjectUVIndex.getString("Value");
                    String StatusUV = jsonObjectUVIndex.getString("Category");

                    txtValueUV.setText("Chỉ số UV của " +ValueUV+ " trên 10");
                    txtStatusUV.setText("Mức độ:"+StatusUV);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        edtSearch = findViewById(R.id.edittextSearch);

        btnSearch = findViewById(R.id.buttonTimKiem);
        btnThayDoiNgay = findViewById(R.id.buttonThayDoiNgay);

        txtThanhPho = findViewById(R.id.textviewTenThanhPho);
        txtQuocGia = findViewById(R.id.textviewQuocGia);
        txtNhietDo = findViewById(R.id.textviewNhietDo);
        txtTrangThai= findViewById(R.id.textviewTrangThai);
        txtMay = findViewById(R.id.textviewMay);
        txtGio = findViewById(R.id.textviewGio);
        txtDoAm = findViewById(R.id.textviewDoAm);
        txtNgayGioCapNhat = findViewById(R.id.textviewNgayThang);

        txtStatusUV = findViewById(R.id.textviewSatusUV);
        txtSatusAir = findViewById(R.id.textviewStatusAir);
        txtValueAir = findViewById(R.id.textviewValueAIR);
        txtValueUV = findViewById(R.id.textviewValueUV);
        imgFace = findViewById(R.id.imageCamXuc);



        imgTrangThai = findViewById(R.id.imageviewTrangThai);
    }
}
