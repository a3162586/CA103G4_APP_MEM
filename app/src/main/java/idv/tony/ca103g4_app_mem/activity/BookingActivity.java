package idv.tony.ca103g4_app_mem.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import idv.tony.ca103g4_app_mem.BranchVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;

public class BookingActivity extends AppCompatActivity {

    private final static String TAG = "BookingActivity";
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8;
    private Spinner spBranch,spDiningPeople;
    private ImageButton ibDiningDate;
    private  static TextView tvDiningDate;
    private static int year, month, day;
    private CommonTask getBranchTask;
    private List<BranchVO> branchList = null;
    private static String branchName,diningDate,diningPeople,diningTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        findViews();
        initDate();
        showRightNow();

        // check if the device connect to the network
        if (Util.networkConnected(this)) {

            // 宣告JasonObject物件，利用getBranchTask非同步任務連線到Servlet的 if ("getAll".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            getBranchTask = new CommonTask(Util.URL + "AndroidBranchServlet", jsonOut);

            try {

                // 將getBranchTask回傳的result重新轉型回List<BranchVO>物件
                String jsonIn = getBranchTask.execute().get();
                Type listType = new TypeToken<List<BranchVO>>() {
                }.getType();
                branchList = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (branchList == null || branchList.isEmpty())
                Util.showToast(this, R.string.msg_BranchNotFound);
            else {
                ArrayAdapter<String> adapter;
                Set<String> set = new LinkedHashSet<>();
                for(BranchVO branchVO : branchList)
                        set.add(branchVO.getBranch_Name());

                // 集合轉陣列
                String[] branchName = set.toArray(new String[set.size()]);

                // ArrayAdapter用來管理整個選項的內容與樣式，android.R.layout.simple_spinner_item為內建預設樣式
                adapter = new ArrayAdapter<>
                        (this, R.layout.spinner_right_item, branchName);
//                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                        this,branchName,R.layout.spinner_right_item);

                // android.R.layout.simple_spinner_dropdown_item為內建下拉選單樣式
                adapter.setDropDownViewResource(R.layout.spinner_right_item);
                spBranch.setAdapter(adapter);
                spBranch.setSelection(0, true);
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }

    private void findViews() {
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        spBranch = findViewById(R.id.spBranch);
        ibDiningDate = findViewById(R.id.ibDiningDate);
        spDiningPeople = findViewById(R.id.spDiningPeople);
        tvDiningDate = findViewById(R.id.tvDiningDate);
        btn1.setOnClickListener(myListener);
        btn2.setOnClickListener(myListener);
        btn3.setOnClickListener(myListener);
        btn4.setOnClickListener(myListener);
        btn5.setOnClickListener(myListener);
        btn6.setOnClickListener(myListener);
        btn7.setOnClickListener(myListener);
        btn8.setOnClickListener(myListener);
        spBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String branch_No = branchList.get(i).getBranch_No();
                SharedPreferences preferences = getSharedPreferences(
                        Util.PREF_FILE, MODE_PRIVATE);
                preferences.edit().putString("branch_No", branch_No).apply();
                branchName = branchList.get(i).getBranch_Name();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // nothing to do
            }
        });
        ibDiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                FragmentManager fm = getSupportFragmentManager();
                datePickerFragment.show(fm, "datePicker");
            }
        });
        spDiningPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                diningPeople = Integer.toString(i+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // nothing to do
            }
        });
    }

    private Button.OnClickListener myListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button btn = v.findViewById(v.getId());
            diningTime = btn.getText().toString();
            String message = "該時段還可接受訂位，\n請點選 \"確認\" 選擇預訂座位。";

            new AlertDialog.Builder(BookingActivity.this)
                    .setIcon(R.drawable.baboo)
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton("確認",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    Intent intent = new Intent(BookingActivity.this,chooseTableActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("branchName",branchName);
                                    bundle.putString("diningDate",diningDate);
                                    bundle.putString("diningPeople",diningPeople);
                                    bundle.putString("diningTime",diningTime);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            })

                    .setNegativeButton("修改",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            }).setCancelable(false).show();
        }
    };

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        // 改寫此方法以提供Dialog內容
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // 建立DatePickerDialog物件
            // this為OnDateSetListener物件
            // year、month、day會成為日期挑選器預選的年月日
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (24*60*60*1000));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (8*24*60*60*1000));
            return datePickerDialog;
        }

        @Override
        // 日期挑選完成會呼叫此方法，並傳入選取的年月日
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            year = y;
            month = m;
            day = d;
            updateInfo();
        }


    }

    private static void showRightNow() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH)+1;
        updateInfo();
    }

    // 將指定的日期顯示在TextView上
    private static void updateInfo() {
        tvDiningDate.setText(new StringBuilder().append(year).append("-")
                //「month + 1」是因為一月的值是0而非1
                .append(parseNum(month + 1)).append("-").append(parseNum(day)));
        diningDate = tvDiningDate.getText().toString();
    }

    // 若數字有十位數，直接顯示；若只有個位數則補0後再顯示。例如7會改成07後再顯示
    private static String parseNum(int day) {
        if (day >= 10)
            return String.valueOf(day);
        else
            return "0" + String.valueOf(day);
    }

    private static void initDate() {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }
}
