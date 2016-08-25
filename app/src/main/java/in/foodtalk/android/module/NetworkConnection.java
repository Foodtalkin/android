package in.foodtalk.android.module;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by RetailAdmin on 24-08-2016.
 */
public class NetworkConnection {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
