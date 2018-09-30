package idv.tony.ca103g4_app_mem.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Contents;
import idv.tony.ca103g4_app_mem.main.QRCodeEncoder;
import idv.tony.ca103g4_app_mem.main.Util;

public class QrcodePersonalFragment extends Fragment {

    private final static String TAG = "QrcodePersonalFragment";
    private TextView tvMemNo;
    private ImageView ivCode;

    public QrcodePersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qrcode_personal, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);
        String mem_No = preferences.getString("mem_No", "");

        tvMemNo = view.findViewById(R.id.tvMemNo);
        ivCode = view.findViewById(R.id.ivCode);
        tvMemNo.setText("會員編號 : "+mem_No);

        qrcodeGenerate(mem_No);

        return view;
    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
        // 取得螢幕尺寸
        Display display = manager.getDefaultDisplay();
        // API 13列為deprecated，但為了支援舊版手機仍採用
        int width = display.getWidth();
        int height = display.getHeight();

        // 產生的QR code圖形尺寸(正方形)為螢幕較短一邊的1/2長度
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension / 2;

        // API 13開始支援
//                Display display = manager.getDefaultDisplay();
//                Point point = new Point();
//                display.getSize(point);
//                int width = point.x;
//                int height = point.y;
//                int smallerDimension = width < height ? width : height;
//                smallerDimension = smallerDimension / 2;
        return smallerDimension;
    }

    private void qrcodeGenerate(String qrCodeText) {

        int smallerDimension = getDimension();

        Log.e(TAG, qrCodeText);

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCodeText, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ivCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

}
