package idv.tony.ca103g4_app_mem.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.activity.BookingActivity;
import idv.tony.ca103g4_app_mem.activity.MemInfoActivity;
import idv.tony.ca103g4_app_mem.activity.OrderActivity;
import idv.tony.ca103g4_app_mem.main.LoginCheck;
import idv.tony.ca103g4_app_mem.main.QrcodePage;

public class QrcodeFragment extends Fragment {

    private final static String TAG = "QrcodeFragment";
    private final int LOGIN_REQUEST = 0;
    private View view;

    public QrcodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        findViews();

        return view;
    }


    public void findViews() {

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

//        btnOrder = view.findViewById(R.id.btnOrder);
//        btnMemInfo = view.findViewById(R.id.btnMemInfo);
//        btnBooing = view.findViewById(R.id.btnBooing);
//
//        btnOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                whichBtn = "btnOrder";
//                new LoginCheck(getActivity(),whichBtn).loginCheck();
//            }
//        });
//        btnMemInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                whichBtn = "btnMemInfo";
//                new LoginCheck(getActivity(),whichBtn).loginCheck();
//            }
//        });
//        btnBooing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                whichBtn = "btnBooing";
//                new LoginCheck(getActivity(),whichBtn).loginCheck();
//            }
//        });

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        List<QrcodePage> pageList;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            pageList = new ArrayList<>();
            pageList.add(new QrcodePage(new QrcodePersonalFragment(), "個人QRCode"));
            pageList.add(new QrcodePage(new QrcodeCouponFragment(), "我的優惠卷"));
        }

        @Override
        public Fragment getItem(int position) {
            return pageList.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageList.get(position).getTitle();
        }
    }

}

