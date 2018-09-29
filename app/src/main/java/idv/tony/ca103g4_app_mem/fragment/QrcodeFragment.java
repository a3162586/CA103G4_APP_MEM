package idv.tony.ca103g4_app_mem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import idv.tony.ca103g4_app_mem.R;

public class QrcodeFragment extends Fragment {

    private final static String TAG = "QrcodeFragment";

    public QrcodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        return view;
    }

}

