package idv.tony.ca103g4_app_mem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import idv.tony.ca103g4_app_mem.MemberVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;

public class LoginActivity extends AppCompatActivity {

    private final int LOGIN_REQUEST = 0;
    private EditText etMemId,etPassword;
    private CommonTask isMemberTask;
    private MemberVO memVO;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 會員資料欄位帶有日期時間，最好指定轉換成JSON時的格式
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        findViews();
    }

    private void findViews() {
        etMemId = findViewById(R.id.etMemId);
        etPassword = findViewById(R.id.etPassword);
    }

    public void onLoginClick(View view) {

        String mem_Id = etMemId.getText().toString().trim();
        String mem_Pw= etPassword.getText().toString().trim();

        // 驗證帳號密碼不為空值
        if (mem_Id.length() <= 0 || mem_Pw.length() <= 0) {
            Toast.makeText(this, "帳號密碼不得為空，請重新輸入!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 將輸入的帳號密碼連線至DB確認是否存在
        memVO = isMember(mem_Id, mem_Pw);
        if (memVO.isMem()) {

            // 登入成功則記錄該會員帳號、密碼及會員編號至偏好設定檔，接著轉回之前頁面
            SharedPreferences preferences = getSharedPreferences(
                    Util.PREF_FILE, MODE_PRIVATE);
            preferences.edit().putBoolean("login", true)
                    .putString("mem_Id", mem_Id)
                    .putString("mem_Pw", mem_Pw)
                    .putString("mem_No", memVO.getMem_No())
                    .putString("mem_Name", memVO.getMem_Name()).apply();

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            bundle.getString("whichBtn");
            intent.putExtras(bundle);

            setResult(RESULT_OK,intent);
            finish();

        } else {
            Toast.makeText(this, "帳號密碼錯誤，請重新輸入!", Toast.LENGTH_SHORT).show();
        }

    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public MemberVO isMember(final String mem_Id, final String mem_Pw) {

        if (Util.networkConnected(this)) {

            //宣告JasonObject物件，利用isMemberTask非同步任務連線到Servlet的 if ("isMember".equals(action))
            String url = Util.URL + "AndroidMemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isMember");
            jsonObject.addProperty("mem_Id", mem_Id);
            jsonObject.addProperty("mem_Pw", mem_Pw);
            String jsonOut = jsonObject.toString();
            isMemberTask = new CommonTask(url, jsonOut);
            try {

                String jsonIn = isMemberTask.execute().get();
                // 如果回傳值為空則設定isMem參數為false
                if("{}".equals(jsonIn)) {
                    memVO = new MemberVO();
                    memVO.setMem(false);
                }
                else {

                    // 將isMemberTask回傳的result重新轉型回MemberVO物件
                    Type listType = new TypeToken<MemberVO>() {
                    }.getType();
                    memVO = gson.fromJson(jsonIn, listType);

                    // 設定isMem參數為true
                    memVO.setMem(true);
                }

            } catch (Exception e) {
                Log.e("LoginActivity", e.toString());
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return memVO;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isMemberTask != null) {
            isMemberTask.cancel(true);
            isMemberTask = null;
        }
    }

}
