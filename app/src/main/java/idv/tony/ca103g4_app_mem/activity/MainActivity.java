package idv.tony.ca103g4_app_mem.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.fragment.HomeFragment;
import idv.tony.ca103g4_app_mem.fragment.MessageFragment;
import idv.tony.ca103g4_app_mem.fragment.OrderHistoryFragment;
import idv.tony.ca103g4_app_mem.fragment.QrcodeFragment;
import idv.tony.ca103g4_app_mem.main.BottomNavigationViewHelper;
import idv.tony.ca103g4_app_mem.main.LoginCheck;
import idv.tony.ca103g4_app_mem.main.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int LOGIN_REQUEST = 0;
    private boolean change_fragment = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    String whichBtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            SharedPreferences preferences;
            boolean login = false;

            //切換fragment畫面(首頁、訂購查詢、信息、QRcode)
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.frameLayout,new HomeFragment());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_orderHistory:
                    new LoginCheck(MainActivity.this,"btnOrderHistory").loginCheck();
                    preferences = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                    login = preferences.getBoolean("login", false);
                    if(login) {
                        fragmentTransaction.replace(R.id.frameLayout,new OrderHistoryFragment());
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.navigation_message:
                    new LoginCheck(MainActivity.this,"btnMessage").loginCheck();
                    preferences = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                    login = preferences.getBoolean("login", false);
                    if(login) {
                        fragmentTransaction.replace(R.id.frameLayout, new MessageFragment());
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.navigation_qrcode:
                    new LoginCheck(MainActivity.this,"btnQrcode").loginCheck();
                    preferences = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                    login = preferences.getBoolean("login", false);
                    if(login) {
                        fragmentTransaction.replace(R.id.frameLayout,new QrcodeFragment());
                        fragmentTransaction.commit();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationViewHelper.disableShiftMode(navigation);


        //底部Menu的ICON大小調整
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
        initFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "test1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initFragment() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout,homeFragment)
                .show(homeFragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //判斷請求代碼是否相同，確認來源是否正確
        if (requestCode != LOGIN_REQUEST) {
            return;
        }

        switch (resultCode) {
            case Activity.RESULT_OK:
                Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show();
                Bundle bundle = data.getExtras();
                whichBtn = bundle.getString("whichBtn");
                forwardActivity();
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(this, "取消登入", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void forwardActivity() {

        Intent intent = new Intent();

        switch (whichBtn) {
            case "btnOrder":
                intent.setClass(this, OrderActivity.class);
                break;
            case "btnMemInfo":
                intent.setClass(this, MemInfoActivity.class);
                break;
            case "btnBooking":
                intent.setClass(this, BookingActivity.class);
                break;
            case "btnOrderHistory":
                change_fragment = true;
                return;
            case "btnMessage":
                change_fragment = true;
                return;
            case "btnQrcode":
                change_fragment = true;
                return;
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(change_fragment) {

            change_fragment=false;
            switch (whichBtn) {
                case "btnOrderHistory":
                    fragmentTransaction.replace(R.id.frameLayout,new OrderHistoryFragment());
                    fragmentTransaction.commit();
                    break;
                case "btnMessage":
                    fragmentTransaction.replace(R.id.frameLayout, new MessageFragment());
                    fragmentTransaction.commit();
                    break;
                case "btnQrcode":
                    fragmentTransaction.replace(R.id.frameLayout,new QrcodeFragment());
                    fragmentTransaction.commit();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = getSharedPreferences(
                Util.PREF_FILE, MODE_PRIVATE);
        preferences.edit().clear().commit();
        super.onDestroy();
    }
}
