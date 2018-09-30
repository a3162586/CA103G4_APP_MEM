package idv.tony.ca103g4_app_mem.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.activity.BookingActivity;
import idv.tony.ca103g4_app_mem.activity.LoginActivity;
import idv.tony.ca103g4_app_mem.activity.MemInfoActivity;
import idv.tony.ca103g4_app_mem.activity.OrderActivity;
import idv.tony.ca103g4_app_mem.fragment.HomeFragment;
import idv.tony.ca103g4_app_mem.fragment.QrcodeFragment;

public class LoginCheck extends AppCompatActivity{

    private final int LOGIN_REQUEST = 0;
    private Activity activity;
    private String whichBtn;

    public LoginCheck(Context context, String whichBtn) {
        this.activity = (Activity) context;
        this.whichBtn = whichBtn;
    }

    public void loginCheck() {

        String message = "請先登入會員";

        SharedPreferences preferences = activity.getSharedPreferences(Util.PREF_FILE,
                activity.MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            if("btnQrcode" == whichBtn || "btnMessage" == whichBtn || "btnOrderHistory" == whichBtn)
                return;
            forwardActivity();
        }
        else {

            new AlertDialog.Builder(activity)
                    .setIcon(R.drawable.baboo)
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton("確認",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("whichBtn", whichBtn);
                                    activity.startActivityForResult(intent,LOGIN_REQUEST);
                                }
                            })

                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            }).setCancelable(false).show();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //判斷請求代碼是否相同，確認來源是否正確
        if (requestCode != LOGIN_REQUEST) {
            return;
        }

        switch (resultCode) {
            case Activity.RESULT_OK:
                Toast.makeText(activity, "登入成功", Toast.LENGTH_SHORT).show();
                forwardActivity();
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(activity, "取消登入", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void forwardActivity() {

        Intent intent = new Intent();

        switch (whichBtn) {
            case "btnOrder":
                intent.setClass(activity, OrderActivity.class);
                break;
            case "btnMemInfo":
                intent.setClass(activity, MemInfoActivity.class);
                break;
            case "btnBooking":
                intent.setClass(activity, BookingActivity.class);
                break;
        }
        activity.startActivity(intent);
    }

}
