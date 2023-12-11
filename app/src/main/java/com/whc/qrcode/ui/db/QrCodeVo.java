package com.whc.qrcode.ui.db;


import java.sql.Timestamp;

public class QrCodeVo {

    private String data;

    private String type;

    private Timestamp time;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
