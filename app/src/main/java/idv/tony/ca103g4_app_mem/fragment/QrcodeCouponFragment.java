package idv.tony.ca103g4_app_mem.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.lang.reflect.Type;
import java.util.List;

import idv.tony.ca103g4_app_mem.CouponVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Contents;
import idv.tony.ca103g4_app_mem.main.QRCodeEncoder;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;
import idv.tony.ca103g4_app_mem.task.ImageTask;

public class QrcodeCouponFragment extends Fragment {

    private RecyclerView rvCouponDetail;
    private final static String TAG = "QrcodeCouponFragment";
    private View view;
    private CommonTask getCouponTask;
    private ImageTask menuImageTask;
    private List<CouponVO> couponList;
    private Gson gson;
    private ImageView ivCode;
    private TextView tvCoupSn;

    public QrcodeCouponFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qrcode_coupon, container, false);

        ivCode = view.findViewById(R.id.ivCode);
        tvCoupSn = view.findViewById(R.id.tvCoupSn);

        // 優惠卷資料欄位帶有日期時間，最好指定轉換成JSON時的格式
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);
        String mem_No = preferences.getString("mem_No", "");

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getCouponTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCouponByMemNo");
            jsonObject.addProperty("mem_No", mem_No);
            String jsonOut = jsonObject.toString();
            getCouponTask = new CommonTask(Util.URL + "AndroidCouponhistoryServlet", jsonOut);

            try {

                //將getCouponTask回傳的result重新轉型回List<CouponVO>物件
                String jsonIn = getCouponTask.execute().get();
                Type listType = new TypeToken<List<CouponVO>>() {
                }.getType();
                couponList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (couponList == null || couponList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_CouponNotFound);
            } else {
                showResult(couponList);
            }

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        return view;
    }

    private class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

        private List<CouponVO> couponList;
        private int imageSize;

        public CouponAdapter(List<CouponVO> couponList) {
            this.couponList = couponList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivCoupon_Photo;
            private TextView tvCoupon_Name,tvCoupon_Period,tvCoupon_Discount;

            public ViewHolder(View view) {
                super(view);
                ivCoupon_Photo = view.findViewById(R.id.ivCoupon_Photo);
                tvCoupon_Name = view.findViewById(R.id.tvCoupon_Name);
                tvCoupon_Period = view.findViewById(R.id.tvCoupon_Period);
                tvCoupon_Discount = view.findViewById(R.id.tvCoupon_Discount);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String coupSn = couponList.get(getAdapterPosition()).getCoup_Sn();
                        tvCoupSn.setText("優惠卷序號 : "+coupSn);
                        qrcodeGenerate(coupSn);
                    }
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_coupon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final CouponVO coupon = couponList.get(position);
            holder.tvCoupon_Name.setText(coupon.getCoucatVO().getCoucat_Name());

            java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String Valid = sdf1.format(coupon.getCoucatVO().getCoucat_Valid());
            String Invalid = sdf1.format(coupon.getCoucatVO().getCoucat_Invalid());

            holder.tvCoupon_Period.setText("使用期限 : \n"+Valid+" ~ "+Invalid);
            holder.tvCoupon_Discount.setText("折扣$"+Integer.toString(coupon.getCoucatVO().getCoucat_Value()));

            //menuImageTask傳入ViewHolder物件，處理完之後會直接將圖show在對應的view上
            String url = Util.URL + "AndroidCoucatServlet";
            String pk = coupon.getCoucatVO().getCoucat_No();
            menuImageTask = new ImageTask(url, pk, imageSize, holder.ivCoupon_Photo);
            menuImageTask.execute();

        }

        @Override
        public int getItemCount() {
            return couponList.size();
        }
    }

    public void showResult(List<CouponVO> result) {

        rvCouponDetail = view.findViewById(R.id.rvCouponDetail);
        rvCouponDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCouponDetail.setLayoutManager(layoutManager);
        rvCouponDetail.setAdapter(new CouponAdapter(result));

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

    @Override
    public void onPause() {
        if (getCouponTask != null) {
            getCouponTask.cancel(true);
            getCouponTask = null;
        }
        if (menuImageTask != null) {
            menuImageTask.cancel(true);
            menuImageTask = null;
        }
        super.onPause();
    }

}
