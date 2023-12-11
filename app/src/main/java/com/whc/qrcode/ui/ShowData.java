package com.whc.qrcode.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.qrcode.R;
import com.whc.qrcode.ui.db.QrCodeVo;

import java.util.List;


public class ShowData extends Fragment {
    private View view;
    private Activity context;
    private ListView showData;
    private BootstrapButton backP;
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
        context.setTitle("歷史紀錄");
        TypefaceProvider.registerDefaultIconSets();
        view = inflater.inflate(R.layout.show_data, container, false);
        showData =  view.findViewById(R.id.showData);
        message = view.findViewById(R.id.message);
        backP = view.findViewById(R.id.backP);

        backP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment fragment = new ScanFragment();
                fragmentTransaction.replace(R.id.body, fragment);
                fragmentTransaction.commit();
            }
        });

        setListView();
        return view;
    }

    private void setListView(){
        List<QrCodeVo> dataAll = Common.getDBQrCodeDB(context).getAll();
        if(dataAll.isEmpty()){
            message.setVisibility(View.VISIBLE);
            message.setText("沒有資料，請掃描Qrcode!");
        }else{
            message.setVisibility(View.GONE);
        }


        Adapter adapter=showData.getAdapter();
        if(adapter == null)
        {
            showData.setAdapter(new QrCodeAdapter(context,dataAll));
        }else{
            QrCodeAdapter adapter1 = (QrCodeAdapter) showData.getAdapter();
            adapter1.setQrCodeVoList(dataAll);
            adapter1.notifyDataSetChanged();
            showData.invalidate();
        }
    }





    private class QrCodeAdapter extends BaseAdapter {
        Activity context;
        List<QrCodeVo> qrCodeVoList;

        QrCodeAdapter(Activity context, List<QrCodeVo> qrCodeVoList) {
            this.context = context;
            this.qrCodeVoList = qrCodeVoList;
        }

        public void setQrCodeVoList(List<QrCodeVo> qrCodeVoList) {
            qrCodeVoList = qrCodeVoList;
        }

        @Override
        public int getCount() {
            return qrCodeVoList.size();
        }

        @Override
        public View getView(final int position, View itemView, final ViewGroup parent) {
            if (itemView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                itemView = layoutInflater.inflate(R.layout.qr_item, parent, false);
            }
            final QrCodeVo qrCodeVo = qrCodeVoList.get(position);
            TextView qrcodeId = (TextView) itemView.findViewById(R.id.qrcodeId);
            BootstrapButton deleteQrode=itemView.findViewById(R.id.deleteQrode);
            BootstrapButton goToQrcode=itemView.findViewById(R.id.goToQrcode);
            BootstrapButton copyQrcode=itemView.findViewById(R.id.copyQrcode);
            BootstrapButton createQrcode=itemView.findViewById(R.id.createQrcode);
            createQrcode.setVisibility(View.VISIBLE);
            goToQrcode.setVisibility(View.VISIBLE);
            deleteQrode.setVisibility(View.VISIBLE);
            copyQrcode.setVisibility(View.VISIBLE);

            createQrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new ShowQrCode();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", qrCodeVo.getData());
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.body, fragment);
                    fragmentTransaction.commit();
                }
            });

            goToQrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(qrCodeVo.getData());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
            copyQrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(qrCodeVo.getData(), qrCodeVo.getData());
                    clipboard.setPrimaryClip(clip);
                }
            });


            deleteQrode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Common.getDBQrCodeDB(context).deleteById(qrCodeVo.getData());
                            setListView();
                            dialog.dismiss();
                        }
                    };

                    DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("是否刪除");
                    builder.setPositiveButton("Yes",listener);
                    builder.setNegativeButton("No", nolistener);
                    builder.setMessage(qrCodeVo.getData());
                    builder.show();
                }
            });
            qrcodeId.setText(qrCodeVo.getData());
            return itemView;
        }

        @Override
        public Object getItem(int position) {
            return qrCodeVoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


}
