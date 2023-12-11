package com.whc.qrcode.ui.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;




import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class QrCodeDB {

    private SQLiteOpenHelper db;
    private String TABLE_NAME = "Qrcode";
    private String COL_id = "data";

    public QrCodeDB(SQLiteOpenHelper db) {
        this.db = db;
    }


    private QrCodeVo configVO(Cursor cursor) {
        QrCodeVo qrcodeVo = new QrCodeVo();
        qrcodeVo.setData(cursor.getString(0));
        qrcodeVo.setType(cursor.getString(1));
        qrcodeVo.setTime(new Timestamp(cursor.getLong(2)));
        return qrcodeVo;
    }


    private ContentValues configContentValues(QrCodeVo qrcodeVo) {
        ContentValues values=new ContentValues();
        values.put("data", qrcodeVo.getData());
        values.put("type", qrcodeVo.getType());
        values.put("time", qrcodeVo.getTime().getTime());
        return values;
    }

    public List<QrCodeVo> getAll() {
        String sql = "SELECT * FROM Qrcode order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<QrCodeVo> qrcodeVoSList = new ArrayList<>();
        QrCodeVo qrcodeVo;
        while (cursor.moveToNext()) {
            qrcodeVo=configVO(cursor);
            qrcodeVoSList.add(qrcodeVo);
        }
        cursor.close();
        return qrcodeVoSList;
    }


    public List<QrCodeVo> findOne(String data) {
        String sql = "SELECT * FROM Qrcode where data = '"+ data +"'  order by time desc;";
        String[] args = {};
        Cursor cursor = db.getReadableDatabase().rawQuery(sql, args);
        List<QrCodeVo> qrcodeVoSList = new ArrayList<>();
        QrCodeVo qrcodeVo;
        while (cursor.moveToNext()) {
            qrcodeVo=configVO(cursor);
            qrcodeVoSList.add(qrcodeVo);
        }
        cursor.close();
        return qrcodeVoSList;
    }

    public long insert(QrCodeVo qrcodeVo) {
        ContentValues values = configContentValues(qrcodeVo);
        return db.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public int update(QrCodeVo qrcodeVo) {
        ContentValues values = configContentValues(qrcodeVo);
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {qrcodeVo.getData()};
        return db.getWritableDatabase().update(TABLE_NAME, values, whereClause, whereArgs);
    }


    public int deleteById(String url) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {url};
        return db.getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs);
    }





}
