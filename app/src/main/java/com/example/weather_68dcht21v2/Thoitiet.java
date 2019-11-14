package com.example.weather_68dcht21v2;

import java.io.Serializable;

public class Thoitiet implements Serializable {
    public String Ngay;
    public String Gio;
    public String TrangThai;
    public String Image;
    public String NhietDoLonNhat;
    public String NhietDoNhoNhat;

    public Thoitiet(String ngay, String gio, String trangThai, String image, String nhietDoLonNhat, String nhietDoNhoNhat) {
        Ngay = ngay;
        Gio = gio;
        TrangThai = trangThai;
        Image = image;
        NhietDoLonNhat = nhietDoLonNhat;
        NhietDoNhoNhat = nhietDoNhoNhat;
    }
}
