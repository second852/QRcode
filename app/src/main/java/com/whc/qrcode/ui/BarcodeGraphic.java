/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whc.qrcode.ui;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.barcode.Barcode;
import com.whc.qrcode.R;
import com.whc.qrcode.ui.db.QrCodeDB;
import com.whc.qrcode.ui.db.QrCodeVo;

import java.sql.Timestamp;
import java.util.List;


/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
public class BarcodeGraphic extends TrackedGraphic<Barcode> {


    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };
    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private volatile Barcode mBarcode;
    private Activity activity;
    private boolean isScan = true;


    BarcodeGraphic(GraphicOverlay overlay, Activity activity) {
        super(overlay);
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);
        this.activity = activity;
    }






    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
    }




    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;

        if(barcode ==null){
            return;
        }

        if(!isScan){
            return;
        }

        if (barcode != null) {
            // Draws the bounding box around the barcode.
            RectF rect = new RectF(barcode.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, mRectPaint);
        }



        if(barcode.rawValue != null && !barcode.rawValue.isEmpty()){
            isScan = false;
            boolean isHttp = barcode.rawValue.indexOf("http")!=1;
            try {
                QrCodeDB qer = Common.getDBQrCodeDB(activity);
                List<QrCodeVo> dataArray = qer.findOne(barcode.rawValue);
                QrCodeVo vo = new QrCodeVo();
                if(dataArray.isEmpty()){
                    vo.setData(barcode.rawValue);
                    if(isHttp){
                        vo.setType("網站");
                    }else{
                        vo.setType("其他");
                    }
                    vo.setTime(new Timestamp(System.currentTimeMillis()));
                    qer.insert(vo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Uri uri = Uri.parse(barcode.rawValue);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                    isScan = true;
                    dialog.dismiss();
                }
            };

            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    isScan = true;
                    dialog.dismiss();
                }
            };

            String message = barcode.rawValue;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("QrCode掃描結果");
            if(isHttp){
                builder.setPositiveButton("前往網站", listener);
            }
            builder.setNegativeButton("繼續掃描", nolistener);
            builder.setMessage(message);
            builder.show();



        }


    }



}
