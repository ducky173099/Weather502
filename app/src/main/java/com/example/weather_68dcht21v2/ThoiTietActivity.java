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
        // khởi tạo intent để get giá trị
        Intent intent = getIntent();
        // khai báo biến city dùng phương thwucs getStringExtra lấy 'key' bên MainActivity
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

        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // sử dụng getSupportActionBar để hỗ trợ set icon trở về
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //khi click vào thì nó sẽ dừng layout này lại
            }
        });
        toolbar.setTitle("Dự báo thời tiết");

        arrayList_ThoiTiet = new ArrayList<Thoitiet>();
        thoiTietAdapter = new ThoiTietAdapter(ThoiTietActivity.this, arrayList_ThoiTiet);
        lvThoiTiet.setAdapter(thoiTietAdapter);
    }


    private void Get5DaysData(String data) { // truyền vào giá trị String để truyền vào vị trí
        String url = "http://api.openweathermap.org/data/2.5/forecast?q="+data+"&units=metric&appid=4cfa017f7ae8871934251cb53b5f651b";
        RequestQueue requestQueue = Volley.newRequestQueue(ThoiTietActivity.this);//requestQueue: thực thi các request(yêu cầu) mà mình gửi đi
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // dữ liệu đc đổ về trong biến response
                        try {
                            JSONObject jsonObject = new JSONObject(response); // để đi vào đc dữ liệu bên trong, gán response vào
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String thanhpho = jsonObjectCity.getString("name");
                            Toast.makeText(ThoiTietActivity.this, thanhpho , Toast.LENGTH_SHORT).show();
                            txtThanhPho.setText(thanhpho);
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            // dùng vòng lặp for để đọc hết giá trị từng cặp thẻ jsonobject con của list
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String ngay = jsonObjectList.getString("dt");
                                String gio = jsonObjectList.getString("dt");
                                // đầu tiên là chuyển kiểu dl dạng String về long
                                long l = Long.valueOf(ngay);
                                // khởi tạo 1 biến là Date, vì cái dữ liệu có dạng là giây, mà format kiểu mini giây
                                Date date = new Date(l*1000L); // chuyển về mini giây
                                long g = Long.valueOf(gio);
                                Date date1 = new Date(g*1000L);

                                // khởi tạo SimpleDateFormat, trong đó truyền tham số muốn format ( thứ - ngày, tháng, năm...)
                                SimpleDateFormat simpleDateFormatNgay = new SimpleDateFormat("EEEE, MM/dd/yyyy HH:mm" );
                                String Ngay = simpleDateFormatNgay.format(date); // tạo biến String hứng giá trị mà sau khi format thành công

                                SimpleDateFormat simpleDateFormatGio = new SimpleDateFormat("HH:mm" );
                                String Gio = simpleDateFormatGio.format(date1);

                                JSONObject jsonObjectNhietDo = jsonObjectList.getJSONObject("main");
                                String NhietDoCaoNhat = jsonObjectNhietDo.getString("temp_max");
                                String NhietDoThapNhat = jsonObjectNhietDo.getString("temp_min");

                                // vì dữ liệu gửi về là dạng double ( vd: 26.5) nên ta chuyện từ String về double
                                Double nd_cn = Double.valueOf(NhietDoCaoNhat);
                                Double nd_tn = Double.valueOf(NhietDoThapNhat);
                                String NhietDo_Max = String.valueOf(nd_cn.intValue()); // đổi về kiểu số int sau đó cast về dạng chuỗi
                                String NhietDo_Min = String.valueOf(nd_tn.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                // trong JSONArray chỉ cso 1 cặp thẻ jsonObject nên truyền giá trị index là giá trị đầu tiên là = 0
                                JSONObject jsonObjectWheather = jsonArrayWeather.getJSONObject(0);
                                String Trangthai = jsonObjectWheather.getString("description");
                                String Icon = jsonObjectWheather.getString("icon");

                                // gọi lại mảng và add tất cả giá trị vào mảng này
                                arrayList_ThoiTiet.add(new Thoitiet(Ngay, Gio, Trangthai, Icon, NhietDo_Max, NhietDo_Min));
                            }
                            // khi có dữ liệu mới thì nó sẽ cập nhật lại adapter
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
