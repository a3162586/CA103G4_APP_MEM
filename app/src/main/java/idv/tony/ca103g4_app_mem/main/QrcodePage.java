package idv.tony.ca103g4_app_mem.main;

import android.support.v4.app.Fragment;

public class QrcodePage {
    private Fragment fragment;
    private String title;

    public QrcodePage(Fragment fragment, String title) {
        this.title = title;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
