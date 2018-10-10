package idv.tony.ca103g4_app_mem.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import idv.tony.ca103g4_app_mem.DeskVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.ResVO;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;

public class chooseTableActivity extends AppCompatActivity {

    private final static String TAG = "chooseTableActivity";
//    private final static String SERVER_URI = "ws://192.168.1.103:8081/CA103G4/AndroidMyBookingServer/";
    private final static String SERVER_URI = "ws://10.0.2.2:8081/CA103G4/AndroidMyBookingServer/";
    private MyWebSocketClient myWebSocketClient;
    private URI uri;
    private List<DeskVO> deskList;
    private CommonTask getDeskTask,reservationAddTask;
    private String branch_no,mem_No,branchName,diningDate,diningPeople,diningTime;
    private Map<String,Integer> seatStatus = new TreeMap<>();
    private Gson gson;
    private StringBuilder seatStr;
    private TextView tvPeople;

    private class MyWebSocketClient extends WebSocketClient {

        MyWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR356）
            super(serverURI, new Draft_17());
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    changeConnectStatus(true);
                }
            });
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());

            Log.d(TAG, text);
        }



        @Override
        public void onMessage(final String message) {
            Log.d(TAG, "onMessage: " + message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if(message.isEmpty())
                            return;
                        else if("{".equals(message.substring(0,1))) {

                            JSONObject jsonObject = new JSONObject(message);
                            String seat = jsonObject.get("seat").toString();
                            String mem_no = jsonObject.get("mem_no").toString();

                            if(!mem_no.equals(mem_No)) {

                                if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                                    seatStatus.put(seat,2);
                                    initTable();

                                } else if(seatStatus.get(seat) == 2) {

                                    seatStatus.put(seat,3);
                                    initTable();
                                }

                            }

                        } else {
                            String[] seatArray = message.split(":");

                            if("add".equals(seatArray[seatArray.length-1])) {
                                for(String seat : seatArray) {
                                    if(!"add".equals(seat)) {
                                        seatStatus.put(seat,2);
                                        initTable();
                                    }
                                }
                            }

                            if("clear".equals(seatArray[seatArray.length-1])) {
                                for(String seat : seatArray) {
                                    if(!"clear".equals(seat)) {
                                        seatStatus.put(seat,3);
                                        initTable();
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    changeConnectStatus(false);
                }
            });
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_table);

        // 訂位紀錄資料欄位帶有日期時間，最好指定轉換成JSON時的格式
        gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        branch_no = preferences.getString("branch_No", "");
        mem_No = preferences.getString("mem_No","");
        Bundle bundle = getIntent().getExtras();
        branchName = bundle.getString("branchName");
        diningDate = bundle.getString("diningDate");
        diningPeople = bundle.getString("diningPeople");
        diningTime = bundle.getString("diningTime");

        tvPeople = findViewById(R.id.tvPeople);
        tvPeople.setText(diningPeople);

        try {
            uri = new URI(SERVER_URI + mem_No);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();

        // check if the device connect to the network
        if (Util.networkConnected(this)) {

            // 宣告JasonObject物件，利用getDeskTask非同步任務連線到Servlet的 if ("getBranchNo".equals(action))
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getBranchNo");

            jsonObject.addProperty("branch_no",branch_no);
            String jsonOut = jsonObject.toString();
            getDeskTask = new CommonTask(Util.URL + "AndroidDeskServlet", jsonOut);

            try {

                // 將getDeskTask回傳的result重新轉型回List<DeskVO>物件存入deskList
                String jsonIn = getDeskTask.execute().get();
                Type listType = new TypeToken<List<DeskVO>>() {
                }.getType();
                deskList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

        initTable();

    }

    public class TableAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<DeskVO> deskList = new ArrayList<>();
        private String region;

        private TableAdapter(Context context, List<DeskVO> deskList, String region) {

            for(DeskVO desk : deskList) {
                if(region.equals(desk.getDek_id().substring(2,3))) {
                    this.deskList.add(desk);
                }
            }
            this.region = region;

            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return deskList.size();
        }

        @Override
        public Object getItem(int i) {
            return deskList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            int seatPosition = 1;
            if (convertView == null) {
                switch (region) {
                    case "A":
                        convertView = layoutInflater.inflate(R.layout.gridview_table, parent, false);
                        seatPosition = i+1;
                        break;
                    case "B":
                        convertView = layoutInflater.inflate(R.layout.gridview_tableb, parent, false);
                        seatPosition = (i*2)+1;
                        break;
                    case "C":
                        convertView = layoutInflater.inflate(R.layout.gridview_tablec, parent, false);
                        seatPosition = (i*4)+1;
                        break;
                    case "D":
                        convertView = layoutInflater.inflate(R.layout.gridview_tabled, parent, false);
                        seatPosition = (i*6)+1;
                        break;
                }

                holder = new ViewHolder();
                holder.ivTableImg1 = convertView.findViewById(R.id.ivTableImg1);
                holder.ivTableImg2 = convertView.findViewById(R.id.ivTableImg2);
                holder.ivTableImg3 = convertView.findViewById(R.id.ivTableImg3);
                holder.ivTableImg4 = convertView.findViewById(R.id.ivTableImg4);
                holder.ivTableImg5 = convertView.findViewById(R.id.ivTableImg5);
                holder.ivTableImg6 = convertView.findViewById(R.id.ivTableImg6);
//                holder.tvTableStatus = convertView.findViewById(R.id.tvTableStatus);
                holder.tvTableNo = convertView.findViewById(R.id.tvTableNo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DeskVO desk = deskList.get(i);
            final String dek_No = desk.getDek_no();
            final String dek_Id = desk.getDek_id();

            holder.tvTableNo.setText(dek_Id);
            final int finalSeatPosition = seatPosition;
            holder.ivTableImg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String seat = region.substring(0,1)+Integer.toString(finalSeatPosition);

                    if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                        if("0".equals(tvPeople.getText().toString()))
                            return;
                        else {
                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                        }

                        seatStatus.put(seat,1);
                        holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("seat", seat);
                        jsonObject.addProperty("mem_no", mem_No);
                        jsonObject.addProperty("status", seatStatus.get(seat));
                        myWebSocketClient.send(jsonObject.toString());
                        Log.d(TAG, "output: " + jsonObject.toString());

                    } else if(seatStatus.get(seat) == 1) {

                        tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                        seatStatus.put(seat,3);
                        holder.ivTableImg1.setBackgroundResource(0);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("seat", seat);
                        jsonObject.addProperty("mem_no", mem_No);
                        jsonObject.addProperty("status", seatStatus.get(seat));
                        myWebSocketClient.send(jsonObject.toString());
                        Log.d(TAG, "output: " + jsonObject.toString());
                    }
                }
            });

            if("B".equals(region) || "C".equals(region) || "D".equals(region)) {
                holder.ivTableImg2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(0,1)+Integer.toString(finalSeatPosition+1);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            if("0".equals(tvPeople.getText().toString()))
                                return;
                            else {
                                tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                            }

                            seatStatus.put(seat,1);
                            holder.ivTableImg2.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                            seatStatus.put(seat,3);
                            holder.ivTableImg2.setBackgroundResource(0);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());
                        }
                    }
                });
            }

            if("C".equals(region) || "D".equals(region)) {
                holder.ivTableImg3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(0,1)+Integer.toString(finalSeatPosition+2);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            if("0".equals(tvPeople.getText().toString()))
                                return;
                            else {
                                tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                            }

                            seatStatus.put(seat,1);
                            holder.ivTableImg3.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                            seatStatus.put(seat,3);
                            holder.ivTableImg3.setBackgroundResource(0);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());
                        }
                    }
                });

                holder.ivTableImg4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(0,1)+Integer.toString(finalSeatPosition+3);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            if("0".equals(tvPeople.getText().toString()))
                                return;
                            else {
                                tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                            }

                            seatStatus.put(seat,1);
                            holder.ivTableImg4.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                            seatStatus.put(seat,3);
                            holder.ivTableImg4.setBackgroundResource(0);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());
                        }
                    }
                });
            }

            if("D".equals(region)) {
                holder.ivTableImg5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(0,1)+Integer.toString(finalSeatPosition+4);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            if("0".equals(tvPeople.getText().toString()))
                                return;
                            else {
                                tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                            }

                            seatStatus.put(seat,1);
                            holder.ivTableImg5.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                            seatStatus.put(seat,3);
                            holder.ivTableImg5.setBackgroundResource(0);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());
                        }
                    }
                });

                holder.ivTableImg6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(0,1)+Integer.toString(finalSeatPosition+5);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            if("0".equals(tvPeople.getText().toString()))
                                return;
                            else {
                                tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())-1));
                            }

                            seatStatus.put(seat,1);
                            holder.ivTableImg6.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

                            tvPeople.setText(Integer.toString(Integer.parseInt(tvPeople.getText().toString())+1));

                            seatStatus.put(seat,3);
                            holder.ivTableImg6.setBackgroundResource(0);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());
                        }
                    }
                });
            }

            // 依座位狀態變更顏色
            Set set = seatStatus.keySet();
            Iterator it = set.iterator();
            String seat;
            while(it.hasNext()) {
                Object myKey = it.next();
                seat = myKey.toString();
                switch (region) {
                    case "A":
                        seatPosition = i+1;
                        if(("A"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("A"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        }
                        break;
                    case "B":
                        seatPosition = (i*2)+1;
                        if(("B"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("B"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorRed);
                        }
                        if(("B"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("B"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        }

                        break;
                    case "C":
                        seatPosition = (i*4)+1;
                        if(("C"+Integer.toString(seatPosition+3)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg4.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("C"+Integer.toString(seatPosition+3)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg4.setBackgroundResource(R.color.colorRed);
                        }
                        if(("C"+Integer.toString(seatPosition+2)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg3.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("C"+Integer.toString(seatPosition+2)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg3.setBackgroundResource(R.color.colorRed);
                        }
                        if(("C"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("C"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorRed);
                        }
                        if(("C"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("C"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        }
                        break;
                    case "D":
                        seatPosition = (i*6)+1;
                        if(("D"+Integer.toString(seatPosition+5)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg6.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition+5)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg6.setBackgroundResource(R.color.colorRed);
                        }
                        if(("D"+Integer.toString(seatPosition+4)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg5.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition+4)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg5.setBackgroundResource(R.color.colorRed);
                        }
                        if(("D"+Integer.toString(seatPosition+3)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg4.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition+3)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg4.setBackgroundResource(R.color.colorRed);
                        }
                        if(("D"+Integer.toString(seatPosition+2)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg3.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition+2)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg3.setBackgroundResource(R.color.colorRed);
                        }
                        if(("D"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition+1)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg2.setBackgroundResource(R.color.colorRed);
                        }
                        if(("D"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("D"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        }
                        break;
                }




            }


            return convertView;
        }

        private class ViewHolder {
            ImageView ivTableImg1,ivTableImg2,ivTableImg3,ivTableImg4,ivTableImg5,ivTableImg6;
            TextView tvTableNo, tvTableStatus;
        }

    }

    public void initTable() {

        // 設定TableAdapter帶入參數deskList
        GridView gdTable = findViewById(R.id.gvTable);
        gdTable.setAdapter(new TableAdapter(this,deskList,"A"));

        GridView gdTableB = findViewById(R.id.gvTableB);
        gdTableB.setAdapter(new TableAdapter(this,deskList,"B"));

        GridView gdTableC = findViewById(R.id.gvTableC);
        gdTableC.setAdapter(new TableAdapter(this,deskList,"C"));

        GridView gdTableD = findViewById(R.id.gvTableD);
        gdTableD.setAdapter(new TableAdapter(this,deskList,"D"));

    }

    public void onBookingClick(View view) {
        seatStr = new StringBuilder();
        StringBuilder bookingCheckStr = new StringBuilder();
        bookingCheckStr
                .append("請確認訂位資料是否正確\n\n")
                .append("分店名稱 : ").append(branchName).append("\n")
                .append("訂位日期 : ").append(diningDate).append("\n")
                .append("用餐人數 : ").append(diningPeople).append("\n")
                .append("訂位時間 : ").append(diningTime).append("\n")
                .append("-預訂座位-\n");

        Set set = seatStatus.keySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Object myKey = it.next();
            if(seatStatus.get(myKey) == 1) {
                bookingCheckStr.append(myKey.toString()).append(":");
                seatStr.append(myKey.toString()).append(":");
            }
        }

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.baboo)
                .setTitle(R.string.app_name)
                .setMessage(bookingCheckStr)
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                if (Util.networkConnected(chooseTableActivity.this)) {
                                    String url = Util.URL + "AndroidReservationServlet";

                                    ResVO res = new ResVO();
                                    res.setMem_no(mem_No);
                                    res.setRes_submit(new Timestamp(System.currentTimeMillis()));
                                    res.setRes_timebg(stringToTimestamp(diningDate+" "+diningTime));
                                    long timefn = stringToTimeInMillis(diningDate+" "+diningTime);
                                    timefn+=5400000;
                                    res.setRes_timefn(timeInMillisToTimestamp(timefn));
                                    res.setRes_people(Integer.parseInt(diningPeople));
                                    res.setRes_status(1);

                                    // 宣告JasonObject物件，利用reservationAddTask非同步任務連線到Servlet的 if ("add".equals(action))
                                    String resStr = gson.toJson(res);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("action", "add");
                                    jsonObject.addProperty("reservation", resStr);
                                    jsonObject.addProperty("branch_no", branch_no);
                                    jsonObject.addProperty("branch_name", branchName);
                                    jsonObject.addProperty("seatStr", seatStr.toString());
                                    String jsonOut = jsonObject.toString();
                                    reservationAddTask = new CommonTask(url, jsonOut);

                                    // 訂位紀錄新增成功會轉換至MainActivity頁面，失敗則show出FailCreateReservation訊息
                                    ResVO successRes = null;
                                    try {

                                        // 回傳的Json字串以ResVO格式解析
                                        String result = reservationAddTask.execute().get();
                                        successRes = gson.fromJson(result, ResVO.class);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }

                                    if (successRes == null) {
                                        Util.showToast(chooseTableActivity.this, R.string.msg_FailCreateRes);
                                    } else {
                                        Toast.makeText(chooseTableActivity.this, "訂位成功!", Toast.LENGTH_SHORT).show();
                                        myWebSocketClient.close();
                                        Intent intent = new Intent(chooseTableActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }


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

    public Timestamp stringToTimestamp(String dateStr) {
        DateFormat formatter ;
        Date date = new Date();
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timeStampDate = new Timestamp(date.getTime());
        return timeStampDate;
    }

    public long stringToTimeInMillis(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    public Timestamp timeInMillisToTimestamp(long millis) {

        return new Timestamp(millis);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myWebSocketClient != null) {
                myWebSocketClient.close();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        if (getDeskTask != null) {
            getDeskTask.cancel(true);
            getDeskTask = null;
        }
        super.onPause();
    }

}
