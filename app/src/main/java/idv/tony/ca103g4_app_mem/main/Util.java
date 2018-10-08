package idv.tony.ca103g4_app_mem.main;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {
//    public final static String URL = "http://192.168.1.103:8081/CA103G4/";
    public final static String URL = "http://10.0.2.2:8081/CA103G4/";

    // 偏好設定檔案名稱
    public final static String PREF_FILE = "preference";

    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    // show the message
    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

//    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        // 轉成PNG不會失真，所以quality參數值會被忽略
//        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }

    /*
     * options.inJustDecodeBounds取得原始圖片寬度與高度資訊 (但不會在記憶體裡建立實體)
     * 當輸出寬與高超過自訂邊長邊寬最大值，scale設為2 (寬變1/2，高變1/2)
     */
//    public static int getImageScale(byte[] imageBytes, int width, int height) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,options);
//        int scale = 1;
//        while (options.outWidth / scale >= width ||
//                options.outHeight / scale >= height) {
//            scale *= 2;
//        }
//        return scale;
//    }

    // 功能分類
//    public final static Page[] PAGES = {
//            new Page(0, "Book", R.drawable.books, BookActivity.class),
//            new Page(1, "Order", R.drawable.cart_empty, CartActivity.class),
//            new Page(2, "Member", R.drawable.user, MemberShipActivity.class),
//            new Page(3, "Setting", R.drawable.setting, ChangeUrlActivity.class)
//    };

    // 要讓商品在購物車內順序能夠一定，且使用RecyclerView顯示時需要一定順序，List較佳
//    public static ArrayList<OrderBook> CART = new ArrayList<>();
}
