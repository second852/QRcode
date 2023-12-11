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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.AwesomeTextView;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.whc.qrcode.R;


import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;


public final class ScanFragment extends Fragment {

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private View view;
    private Activity activity;
    private BootstrapButton history;

    /**
     * Initializes the UI and creates the detector pipeline.
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main, container, false);
        mPreview =view.findViewById(R.id.preview);
        mGraphicOverlay =view.findViewById(R.id.faceOverlay);
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        history = view.findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment = new ShowData();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }





    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
    }





    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {
        Context context = activity;
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay,activity);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        mCameraSource = new CameraSource.Builder(activity, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }







    @Override
    public void onStart() {
        super.onStart();
        startCameraSource();
    }



    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the camerasource
            Fragment fragment=new ScanFragment();
            fragment.setArguments(getArguments());
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            Common.showToast(activity,"開始QRCode掃描!");
            return;
        }

        String remain;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            remain="沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
        } else {
            remain="沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))
                {
                    Common.askPermissions(Manifest.permission.CAMERA, activity,0);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent,6);
                }
            }
        };

        DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("無法使用相機!")
                .setMessage(remain)
                .setPositiveButton("YES", listener)
                .setNegativeButton("NO",nolistener)
                .show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {

            String remain;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                remain = "沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
            } else {
                remain = "沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                        Common.askPermissions(Manifest.permission.CAMERA, activity, 0);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 6);
                    }
                }
            };

            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("無法使用相機!")
                    .setMessage(remain)
                    .setPositiveButton("YES", listener)
                    .setNegativeButton("NO", nolistener)
                    .show();

        } else {
            Fragment fragment=new ScanFragment();
            fragment.setArguments(getArguments());
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.body, fragment);
            fragmentTransaction.commit();
            Common.showToast(activity,"開始QRCode掃描!");
        }
    }



    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
                Log.d("XXXXXXXX",e.toString());
            }
        }
    }



}
