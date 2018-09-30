package idv.tony.ca103g4_app_mem.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import idv.tony.ca103g4_app_mem.CouponhistoryVO;
import idv.tony.ca103g4_app_mem.MenuVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;
import idv.tony.ca103g4_app_mem.task.ImageTask;

public class QrcodeCouponFragment extends Fragment {

    private RecyclerView rvCouponDetail;
    private final static String TAG = "QrcodeCouponFragment";
    private View view;
    private CommonTask getCouponTask;
    private ImageTask menuImageTask;
    private List<CouponhistoryVO> couponList;
//    private List<CouponhistoryVO> couponList = new ArrayList<>();

    public QrcodeCouponFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qrcode_coupon, container, false);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            //宣告JasonObject物件，利用getCouponTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getCouponTask = new CommonTask(Util.URL + "AndroidCouponhistoryServlet", jsonOut);

            try {

                //將getCouponTask回傳的result重新轉型回List<CouponhistoryVO>物件
                String jsonIn = getCouponTask.execute().get();
                Type listType = new TypeToken<List<CouponhistoryVO>>() {
                }.getType();
                couponList = new Gson().fromJson(jsonIn, listType);
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

        private List<CouponhistoryVO> couponList;
        private int imageSize;

        public CouponAdapter(List<CouponhistoryVO> couponList) {
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

            final CouponhistoryVO coupon = couponList.get(position);
            holder.tvCoupon_Name.setText(coupon.getMem_no());
            holder.tvCoupon_Period.setText(coupon.getOrder_no());
            holder.tvCoupon_Discount.setText("");

            //menuImageTask傳入ViewHolder物件，處理完之後會直接將圖show在對應的view上
            String url = Util.URL + "AndroidCouponhistoryServlet";
            String pk = coupon.getCoup_sn();
            menuImageTask = new ImageTask(url, pk, imageSize, holder.ivCoupon_Photo);
            menuImageTask.execute();

        }

        @Override
        public int getItemCount() {
            return couponList.size();
        }
    }

    public void showResult(List<CouponhistoryVO> result) {

        rvCouponDetail = view.findViewById(R.id.rvCouponDetail);
        rvCouponDetail.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCouponDetail.setLayoutManager(layoutManager);
        rvCouponDetail.setAdapter(new CouponAdapter(result));

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
