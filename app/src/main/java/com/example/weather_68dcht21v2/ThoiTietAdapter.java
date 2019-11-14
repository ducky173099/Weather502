package com.example.weather_68dcht21v2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThoiTietAdapter extends BaseAdapter {
    Context context;
    ArrayList<Thoitiet> arrayList;

    public ThoiTietAdapter(Context context, ArrayList<Thoitiet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.row_thoitiet, null);

        TextView txtNgayThang = convertView.findViewById(R.id.textviewNgayThangBaoThoiTiet);
        TextView txtTrangThai = convertView.findViewById(R.id.textviewTrangThaiDuBaoThoiTiet);
        TextView txtNhietDoCaoNhat = convertView.findViewById(R.id.textviewMaxtemp);
        TextView txtNhietDoThapNhat = convertView.findViewById(R.id.textviewMintemp);
        TextView txtTieuDe = convertView.findViewById(R.id.textviewTieuDe);
        ImageView imgTrangThai = convertView.findViewById(R.id.imageviewTrangThaiDuBaoThoiTiet);

        Thoitiet thoiTiet = arrayList.get(position);
        String start = "01:00";
        String end = "22:00";
        if (thoiTiet.Gio.equalsIgnoreCase(start)) {
            txtTieuDe.setText("Bắt đầu");
            txtTieuDe.setTextColor(Color.rgb(200,0,200));
        }else if(thoiTiet.Gio.equalsIgnoreCase(end)) {
            txtTieuDe.setText("Kết thúc");
            txtTieuDe.setTextColor(Color.rgb(0,200,000));
        }

        txtNgayThang.setText(thoiTiet.Ngay+"h");
        txtTrangThai.setText(thoiTiet.TrangThai);
        txtNhietDoCaoNhat.setText(thoiTiet.NhietDoLonNhat+"°C");
        txtNhietDoThapNhat.setText(thoiTiet.NhietDoNhoNhat+"°C");
        Picasso.get().load("http://openweathermap.org/img/wn/"+thoiTiet.Image+".png").into(imgTrangThai);

        return convertView;
    }
}
