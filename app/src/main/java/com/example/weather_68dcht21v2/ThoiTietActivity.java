package com.example.weather_68dcht21v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ThoiTietActivity extends AppCompatActivity {
    String ThanhPho = "";
    Toolbar toolbar;
    TextView txtThanhPho;
    ListView lvThoiTiet;
    ThoiTietAdapter thoiTietAdapter;
    ArrayList<Thoitiet> arrayList_ThoiTiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoi_tiet);
        AnhXa();
        Intent intent = getIntent();
        String city = intent.getStringExtra("key");
        if (city.equals("")) {
            ThanhPho = "HaNoi";
            Get5DaysData(ThanhPho);
        }else {
            ThanhPho = city;
            Get5DaysData(ThanhPho);
        }

    }

    private void AnhXa() {
        toolbar = findViewById(R.id.toolbarDuBaoThoiTiet);
        txtThanhPho = findViewById(R.id.textviewCity);
        lvThoiTiet = findViewById(R.id.listviewDuBaoThoiTiet);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Dự báo thời tiết");

        arrayList_ThoiTiet = new ArrayList<Thoitiet>();
        thoiTietAdapter = new ThoiTietAdapter(ThoiTietActivity.this, arrayList_ThoiTiet);
        lvThoiTiet.setAdapter(thoiTietAdapter);
    }


    private void Get5DaysData(String data) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?q="+data+"&units=metric&appid=4cfa017f7ae8871934251cb53b5f651b";
        RequestQueue requestQueue = Volley.newRequestQueue(ThoiTietActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String thanhpho = jsonObjectCity.getString("name");
                            Toast.makeText(ThoiTietActivity.this, thanhpho , Toast.LENGTH_SHORT).show();
                            txtThanhPho.setText(thanhpho);
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String ngay = jsonObjectList.getString("dt");
                                String gio = jsonObjectList.getString("dt");
                                long l = Long.valueOf(ngay);
                                Date date = new Date(l*1000L);
                                long g = Long.valueOf(gio);
                                Date date1 = new Date(g*1000L);

                                SimpleDateFormat simpleDateFormatNgay = new SimpleDateFormat("EEEE, MM/dd/yyyy HH:mm" );
                                String Ngay = simpleDateFormatNgay.format(date);

                                SimpleDateFormat simpleDateFormatGio = new SimpleDateFormat("HH:mm" );
                                String Gio = simpleDateFormatGio.format(date1);

                                JSONObject jsonObjectNhietDo = jsonObjectList.getJSONObject("main");
                                String NhietDoCaoNhat = jsonObjectNhietDo.getString("temp_max");
                                String NhietDoThapNhat = jsonObjectNhietDo.getString("temp_min");

                                Double nd_cn = Double.valueOf(NhietDoCaoNhat);
                                Double nd_tn = Double.valueOf(NhietDoThapNhat);
                                String NhietDo_Max = String.valueOf(nd_cn.intValue());
                                String NhietDo_Min = String.valueOf(nd_tn.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWheather = jsonArrayWeather.getJSONObject(0);
                                String Trangthai = jsonObjectWheather.getString("description");
                                String Icon = jsonObjectWheather.getString("icon");
                                arrayList_ThoiTiet.add(new Thoitiet(Ngay, Gio, Trangthai, Icon, NhietDo_Max, NhietDo_Min));
                            }
                            thoiTietAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThoiTietActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
