package com.whc.qrcode.ui;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.whc.qrcode.ui.db.QrCodeAPPDB;
import com.whc.qrcode.ui.db.QrCodeDB;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    private static QrCodeAPPDB qrCodeAPPDB;

    private static QrCodeDB qrCodeDB;






    public static QrCodeDB getDBQrCodeDB(Activity activity){
        if(qrCodeAPPDB ==null ){
            qrCodeAPPDB = new QrCodeAPPDB(activity);
        }
        if(qrCodeDB ==null){
            qrCodeDB = new QrCodeDB(qrCodeAPPDB);
        }
        return qrCodeDB;
    }

     

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    public static void askPermissions(String s, Activity context, int requestCode) {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {s};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }

}
