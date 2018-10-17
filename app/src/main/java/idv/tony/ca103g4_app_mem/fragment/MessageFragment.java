package idv.tony.ca103g4_app_mem.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;
import idv.tony.ca103g4_app_mem.task.ImageTask;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MessageFragment extends Fragment {

    private final static String TAG = "MessageFragment";
//    private final static String SERVER_URI = "ws://192.168.196.209:8081/CA103G4/CustomerService/";
//    private final static String SERVER_URI = "ws://10.0.2.2:8081/CA103G4/CustomerService/";
    private final static String SERVER_URI = "ws://ca103g4.tk/CA103G4/CustomerService/";

    private MyWebSocketClient myWebSocketClient;
    private ImageTask getMemPhotoTask,getEmpPhotoTask;
    private TextView tvConnect;
    private EditText etMessage;
    private Button btSend;
    private ScrollView scrollView;
    private LinearLayout layout;
    private String memNo,myName,empName="";
    private Bitmap memPhoto,empPhoto;
    private URI uri;

    private class MyWebSocketClient extends WebSocketClient {

        MyWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR356）
            super(serverURI, new Draft_6455());
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    changeConnectStatus(true);
                }
            });
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", myName);
            jsonObject.addProperty("type", "sysMsg");
            jsonObject.addProperty("message", "連線客服系統成功!");
            myWebSocketClient.send(jsonObject.toString());
            Log.d(TAG, "output: " + jsonObject.toString());

        }

        @Override
        public void onMessage(final String message) {
            Log.d(TAG, "onMessage: " + message);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        String userName = jsonObject.get("username").toString();
                        String message = jsonObject.get("message").toString().trim();
                        String type = jsonObject.get("type").toString();

                        switch (type) {
                            case "sysMsg":
                                if(userName.equals(myName))
                                    tvConnect.setText(userName+" "+message);
                                break;
                            case "userMsg":
                                if(userName.equals(myName)) {
                                    showMessage(memPhoto, userName, message,false);
                                }

                                else{
                                    if(!userName.equals(empName)) {
                                        empName = userName;
                                        getEmpPhoto();
                                    }
                                    showMessage(empPhoto, userName, message,true);
                                }
                                break;
                        }

                        scrollView.fullScroll(View.FOCUS_DOWN);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            getActivity().runOnUiThread(new Runnable() {
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

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE,
                getActivity().MODE_PRIVATE);
        memNo = preferences.getString("mem_No","");
        myName = preferences.getString("mem_Name","");
        tvConnect = view.findViewById(R.id.tvConnect);
        etMessage = view.findViewById(R.id.etMessage);
        btSend = view.findViewById(R.id.btSend);
        scrollView = view.findViewById(R.id.scrollView);
        layout = view.findViewById(R.id.layout);

        //取出會員大頭照
        String url = Util.URL + "AndroidMemberServlet";
        String pk = myName;
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        getMemPhotoTask = new ImageTask(url, pk, imageSize);
        try {
            memPhoto = getMemPhotoTask.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            uri = new URI(SERVER_URI + encodeUrl(myName) + "/" + "E000000002");
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connect();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if (message.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "訊息不得為空", Toast.LENGTH_SHORT).show();
                    return;
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("username", myName);
                jsonObject.addProperty("message", message);
                jsonObject.addProperty("type", "userMsg");
                jsonObject.addProperty("time", new Date().toLocaleString());
                jsonObject.addProperty("picMem", "<img class=\"nav-item \" src=\"/CA103G4/front_end/member/member.do?mem_No="+memNo+"\" style=\"display:;height:50px;width:50px;border-radius:50%;\">");
                myWebSocketClient.send(jsonObject.toString());
                etMessage.setText("");
                Log.d(TAG, "output: " + jsonObject.toString());
            }
        });



        return view;
    }

    public void showMessage(Bitmap photo, String userName, String message, boolean left) {
        String text = userName + ": " + message;
        View view;
        // 準備左右2種layout給不同種類發訊者(他人/自己)使用
        if (left) {
            view = View.inflate(getActivity(), R.layout.message_left, null);
        } else {
            view = View.inflate(getActivity(), R.layout.message_right, null);

        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setMaxHeight(20);
        imageView.setMaxWidth(20);
        imageView.setScaleType(CENTER_CROP);
        imageView.setImageBitmap(photo);

        layout.addView(view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public String encodeUrl(String url) {
        return Uri.encode(url, "-![.:/,%?&=]");
    }

    public void getEmpPhoto() {
        String url = Util.URL + "AndroidEmployeeServlet";
        String pk = empName;
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        getEmpPhotoTask = new ImageTask(url, pk, imageSize);
        try {
            empPhoto = getEmpPhotoTask.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
        }
        super.onStop();
    }

}
