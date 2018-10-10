package idv.tony.ca103g4_app_mem.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;

import idv.tony.ca103g4_app_mem.R;
import idv.tony.ca103g4_app_mem.main.Util;

public class MessageFragment extends Fragment {

    private final static String TAG = "MessageFragment";
//    private final static String SERVER_URI = "ws://192.168.1.103:8081/CA103G4/CustomerService/";
    private final static String SERVER_URI = "ws://10.0.2.2:8081/CA103G4/CustomerService/";

    private MyWebSocketClient myWebSocketClient;
    private TextView tvMessageOther,tvMessageSelf,tvConnect;
    private EditText etMessage;
    private Button btSend;
    private ScrollView scrollView;
    private String myName;
    private URI uri;

    private class MyWebSocketClient extends WebSocketClient {

        MyWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR356）
            super(serverURI, new Draft_17());
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
                        String message = jsonObject.get("message").toString();
                        String type = jsonObject.get("type").toString();
                        String time = "";
                        if("userMsg".equals(type))
                            time = jsonObject.get("time").toString();

                        switch (type) {
                            case "sysMsg":
                                tvConnect.setText(userName+" "+message);
                                break;
                            case "userMsg":
                                if(userName.equals(myName)) {
                                    tvMessageSelf.append(userName+":\n"+message+"\n");
                                    tvMessageOther.append("\n\n");
                                }

                                else{
                                    tvMessageOther.append(userName+":\n"+message+"\n");
                                    tvMessageSelf.append("\n\n");
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
        myName = preferences.getString("mem_No","");

        tvMessageOther = view.findViewById(R.id.tvMessageOther);
        tvMessageSelf = view.findViewById(R.id.tvMessageSelf);
        tvConnect = view.findViewById(R.id.tvConnect);
        etMessage = view.findViewById(R.id.etMessage);
        btSend = view.findViewById(R.id.btSend);
        scrollView = view.findViewById(R.id.scrollView);

        try {
            uri = new URI(SERVER_URI + myName + "/" + "E000000002");
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
                myWebSocketClient.send(jsonObject.toString());
                Log.d(TAG, "output: " + jsonObject.toString());
            }
        });



        return view;
    }

    @Override
    public void onStop() {
        if (myWebSocketClient != null) {
            myWebSocketClient.close();
        }
        super.onStop();
    }

}
