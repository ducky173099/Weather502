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
                if (city.equals("")) { // nếu khi run app mà city là rỗng
                    GetCurrentWeatherData(City);// thì nó sẽ gán mặc định biến city ="Hanoi"
                    GetAirAndPollen(Id_location);
                }else { // nếu tồn tại giá trị r
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
                // khi click vào button xem nhiều ngày này, thì nó sẽ lấy dl từ editText
                // nghĩa là lấy dl từ city mà người dụng nhập vào editText và chuyển qua màn hình 2
                String city = edtSearch.getText().toString();
                Intent intent = new Intent(MainActivity.this, ThoiTietActivity.class);
                // 'key' là 1 key: để khi chuyển sang màn hình 2, màn hình đó sẽ bắt cái 'key' để nhận giá trị
                intent.putExtra("key", city);
                startActivity(intent);
            }
        });
    }

    public void GetCurrentWeatherData(String data) {
        //Volley cho phép cùng một lúc thưc hiện nhiều request trên các thread khác nhau với các mức độ ưu tiên (priority ) khác nhau
        //Volley là một thư viện dùng để send và recieve response từ Server sử dụng giao thức HTTP
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);//requestQueue: thực thi các request(yêu cầu) mà mình gửi đi
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&appid=4cfa017f7ae8871934251cb53b5f651b";
        //StringRequest: Kết thừa từ Request, là class đại diện cho request trả về String.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try { // sử dụng try-catch để tránh các lỗi khi chạy chương trình mà khi compile không thể biết được(xử lý ngoại lệ)
                            // api ban đầu là 1 jsonobject, nên khởi tạo Jsonobject
                            JSONObject jsonObject = new JSONObject(response);
                            // khởi tạo biến lấy 2 giá trị tagname(tên từ file json) là dt và name
                            String ngay = jsonObject.getString("dt"); // dt là ngày tháng sẽ format lại sau
                            String name = jsonObject.getString("name");

                            txtThanhPho.setText("Tên thành phố: " + name);
                            // custom dt
                            long l = Long.valueOf(ngay);
                            Date date = new Date(l*1000L); // chuyển về dạng mini giây
                            // format lại kiểu định dạng (thứ - ngày,tháng,năm - giờ,phút,giây)
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM-dd-yyyy HH:mm" );
                            String Ngay = simpleDateFormat.format(date); // truyền vào giá trị format

                            txtNgayGioCapNhat.setText(Ngay);
                            // trong tagname weather có JsonArray, nên ta khởi tạo jsonArray đc get từ jsonObject ngoài cùng
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            // trong jsonarray có 1 jsonobject
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            // hứng các giá trị của từng tagname
                            String trangthai = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            // sử dụng thư viện PIcasso cho việc đọc dữ liệu hình ảnh
                            Picasso.get().load("http://openweathermap.org/img/wn/"+icon+".png").into(imgTrangThai); // dổ dl ra imgTrangThai
                            txtTrangThai.setText(trangthai);

                            // tagname Jsonobject (main)
                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            // hứng cá dữ liệu từ tag name con của main
                            String nhietdo = jsonObjectMain.getString("temp");
                            String doam = jsonObjectMain.getString("humidity");

                            // vì nhiệt độ có thể trả về dạng double ( vd: 31.2, 30.5)
                            Double nd = Double.valueOf(nhietdo);
                            // đổi kiểu dữ liệu cho biến nhietdo về dạng chuỗi
                            String NhietDo = String.valueOf(nd.intValue()); // gán về int r chuyển lại dạng String
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
                            // nếu lỗi xảy ra sẽ xử lý tại đây
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                // để thức thi đc request trên, ta gọi lại requestQueue
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
