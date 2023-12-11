package com.whc.qrcode.ui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QrCodeAPPDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "QrCodeAPPDB";
    private static final int DB_VERSION = 1;


    private static final String TABLE_QrCode = "CREATE TABLE QrCode ( data TEXT NOT NULL, TYPE TEXT NOT NULL,TIME DATETIME );";

    public QrCodeAPPDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_QrCode);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS QrCode" );
        onCreate(db);
    }

}
