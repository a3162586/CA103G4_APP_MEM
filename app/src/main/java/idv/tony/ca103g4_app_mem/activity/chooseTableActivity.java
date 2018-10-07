package idv.tony.ca103g4_app_mem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import idv.tony.ca103g4_app_mem.DeskVO;
import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.CommonTask;

public class chooseTableActivity extends AppCompatActivity {

    private final static String TAG = "chooseTableActivity";
    private final static String SERVER_URI = "ws://192.168.1.103:8081/CA103G4/AndroidMyBookingServer/";
//    private final static String SERVER_URI = "ws://10.0.2.2:8081/CA103G4/AndroidMyBookingServer/";
    private MyWebSocketClient myWebSocketClient;
    private URI uri;
    private List<DeskVO> deskList;
    private CommonTask getDeskTask;
    private String branch_no,mem_No;
    private Map<String,Integer> seatStatus = new TreeMap<>();

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
                        JSONObject jsonObject = new JSONObject(message);
                        String seat = jsonObject.get("seat").toString();
                        String mem_no = jsonObject.get("mem_no").toString();

                        if(!mem_no.equals(mem_No)) {
Log.e(TAG,"1");
                            if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                                seatStatus.put(seat,2);
                                initTable();

                            } else if(seatStatus.get(seat) == 2) {

                                seatStatus.put(seat,3);
                                initTable();
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

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        branch_no = preferences.getString("branch_No", "");
        mem_No = preferences.getString("mem_No","");

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
                if(region.equals(desk.getDek_id().substring(0,2))) {
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
                    case "0A":
                        convertView = layoutInflater.inflate(R.layout.gridview_table, parent, false);
                        seatPosition = i+1;
                        break;
                    case "0B":
                        convertView = layoutInflater.inflate(R.layout.gridview_tableb, parent, false);
                        seatPosition = (i*2)+1;
                        break;
                    case "0C":
                        convertView = layoutInflater.inflate(R.layout.gridview_tablec, parent, false);
                        seatPosition = (i*4)+1;
                        break;
                    case "0D":
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

                    String seat = region.substring(1,2)+Integer.toString(finalSeatPosition);

                    if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                        seatStatus.put(seat,1);
                        holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("seat", seat);
                        jsonObject.addProperty("mem_no", mem_No);
                        jsonObject.addProperty("status", seatStatus.get(seat));
                        myWebSocketClient.send(jsonObject.toString());
                        Log.d(TAG, "output: " + jsonObject.toString());

                    } else if(seatStatus.get(seat) == 1) {

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

            if("0B".equals(region) || "0C".equals(region) || "0D".equals(region)) {
                holder.ivTableImg2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(1,2)+Integer.toString(finalSeatPosition+1);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            seatStatus.put(seat,1);
                            holder.ivTableImg2.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

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

            if("0C".equals(region) || "0D".equals(region)) {
                holder.ivTableImg3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(1,2)+Integer.toString(finalSeatPosition+2);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            seatStatus.put(seat,1);
                            holder.ivTableImg3.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

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

                        String seat = region.substring(1,2)+Integer.toString(finalSeatPosition+3);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            seatStatus.put(seat,1);
                            holder.ivTableImg4.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

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

            if("0D".equals(region)) {
                holder.ivTableImg5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String seat = region.substring(1,2)+Integer.toString(finalSeatPosition+4);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            seatStatus.put(seat,1);
                            holder.ivTableImg5.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

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

                        String seat = region.substring(1,2)+Integer.toString(finalSeatPosition+5);

                        if(!seatStatus.containsKey(seat) || seatStatus.get(seat) == 3) {

                            seatStatus.put(seat,1);
                            holder.ivTableImg6.setBackgroundResource(R.color.colorRed);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("seat", seat);
                            jsonObject.addProperty("mem_no", mem_No);
                            jsonObject.addProperty("status", seatStatus.get(seat));
                            myWebSocketClient.send(jsonObject.toString());
                            Log.d(TAG, "output: " + jsonObject.toString());

                        } else if(seatStatus.get(seat) == 1) {

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
                    case "0A":
                        seatPosition = i+1;
                        if(("A"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 2) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorYellow);
                        }
                        if(("A"+Integer.toString(seatPosition)).equals(seat) && seatStatus.get(myKey) == 1) {
                            holder.ivTableImg1.setBackgroundResource(R.color.colorRed);
                        }
                        break;
                    case "0B":
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
                    case "0C":
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
                    case "0D":
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
        gdTable.setAdapter(new TableAdapter(this,deskList,"0A"));

        GridView gdTableB = findViewById(R.id.gvTableB);
        gdTableB.setAdapter(new TableAdapter(this,deskList,"0B"));

        GridView gdTableC = findViewById(R.id.gvTableC);
        gdTableC.setAdapter(new TableAdapter(this,deskList,"0C"));

        GridView gdTableD = findViewById(R.id.gvTableD);
        gdTableD.setAdapter(new TableAdapter(this,deskList,"0D"));

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
