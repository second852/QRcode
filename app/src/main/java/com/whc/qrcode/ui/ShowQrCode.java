package com.whc.qrcode.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.whc.qrcode.R;


public class ShowQrCode extends Fragment {
    private View view;
    private Activity context;
    private BootstrapButton backP;
    private ImageView qrCode;
    private TextView message;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        } else {
            this.context = getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context.setTitle("QrCode");
        TypefaceProvider.registerDefaultIconSets();
        view = inflater.inflate(R.layout.show_qrcode, container, false);
        message = view.findViewById(R.id.message);
        backP = view.findViewById(R.id.backP);
        qrCode = view.findViewById(R.id.qrCode);
        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment = new ShowData();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });
        String data= (String) getArguments().getSerializable("data");
        message.setText(data);
        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
            Bitmap bit = encoder.encodeBitmap(data, BarcodeFormat.QR_CODE,
                    650, 650);
            qrCode.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return view;
    }







}
