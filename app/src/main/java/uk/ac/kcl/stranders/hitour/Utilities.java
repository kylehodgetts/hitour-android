package uk.ac.kcl.stranders.hitour;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilities {
    /**
     * @return true if there is network connection
     */
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
