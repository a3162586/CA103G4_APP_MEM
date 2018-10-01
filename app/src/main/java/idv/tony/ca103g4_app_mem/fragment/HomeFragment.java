package idv.tony.ca103g4_app_mem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import idv.tony.ca103g4_app_mem.MenuVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.LoginCheck;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;
import idv.tony.ca103g4_app_mem.task.ImageTask;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";
    private final int LOGIN_REQUEST = 0;
    private CommonTask getMenuTask;
    private ImageTask menuImageTask;
    private List<MenuVO> menuList;
    private View view;
    private ImageButton btnOrder,btnMemInfo,btnBooing;
    private String whichBtn;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // check if the device connect to the network
        if (Util.networkConnected(getActivity())) {

            // 宣告JasonObject物件，利用getMenuTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getMenuTask = new CommonTask(Util.URL + "AndroidMenuServlet", jsonOut);

            try {

                // 將getDeskTask回傳的result重新轉型回List<MenuVO>物件存入menuList
                String jsonIn = getMenuTask.execute().get();
                Type listType = new TypeToken<List<MenuVO>>() {
                }.getType();
                menuList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (menuList == null || menuList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_MenusNotFound);
            } else {
                showResult(menuList);
            }

        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }

        findViews();

        return view;
    }

    public void findViews() {

        btnOrder = view.findViewById(R.id.btnOrder);
        btnMemInfo = view.findViewById(R.id.btnMemInfo);
        btnBooing = view.findViewById(R.id.btnBooing);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichBtn = "btnOrder";
                new LoginCheck(getActivity(),whichBtn).loginCheck();
            }
        });
        btnMemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichBtn = "btnMemInfo";
                new LoginCheck(getActivity(),whichBtn).loginCheck();
            }
        });
        btnBooing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichBtn = "btnBooking";
                new LoginCheck(getActivity(),whichBtn).loginCheck();
            }
        });

    }

    public class MenuAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<MenuVO> menuList;
        private int imageSize;
        boolean checkPosition0First = true;

        private MenuAdapter(Context context, List<MenuVO> menuList) {
            this.menuList = menuList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            // 在fragment中需先取得activity後才能調用getSystemService方法
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Object getItem(int i) {
            return menuList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.gridview_menuintro, parent, false);
                holder = new ViewHolder();
                holder.ivMenuImg = convertView.findViewById(R.id.ivMenuImg);
                holder.tvMenuId = convertView.findViewById(R.id.tvMenuId);
                holder.tvMenuIntro = convertView.findViewById(R.id.tvMenuIntro);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(!checkPosition0First && i==0) {
                return convertView;
            }

            MenuVO menu = menuList.get(i);
            final String menu_Id = menu.getMenu_Id();
            final String menu_Intro = menu.getMenu_Intro();

            //menuImageTask傳入ViewHolder物件，處理完之後會直接將圖show在對應的view上
            String url = Util.URL + "AndroidMenuServlet";
            String pk = menu.getMenu_No();
            menuImageTask = new ImageTask(url, pk, imageSize, holder.ivMenuImg);
            menuImageTask.execute();

            holder.tvMenuId.setText(menu_Id);
            holder.tvMenuIntro.setText(menu_Intro);

            checkPosition0First = false;
            return convertView;
        }

        private class ViewHolder {
            ImageView ivMenuImg;
            TextView tvMenuId, tvMenuIntro;
        }
    }

    public void showResult(List<MenuVO> result) {

        // 設定MenuAdapter帶入參數menuList
        GridView gvMenuIntro = view.findViewById(R.id.gvMenuIntro);
        gvMenuIntro.setAdapter(new MenuAdapter(getActivity(),result));
    }

    @Override
    public void onPause() {
        if (getMenuTask != null) {
            getMenuTask.cancel(true);
            getMenuTask = null;
        }
        super.onPause();
    }

}
