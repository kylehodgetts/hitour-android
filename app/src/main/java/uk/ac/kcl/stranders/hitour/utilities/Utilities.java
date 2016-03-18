package uk.ac.kcl.stranders.hitour.utilities;

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

    /**
     * Create an acceptable filename for location in internal storage
     * @param url the url for the data location on the internet
     * @return a changed url for using as the filename in internal storage
     */
    public static String createFilename(String url) {
        url = url.replace("/","");
        url = url.replace(":","");
        url = url.replace("%","");
        String filename = url.substring(0, url.lastIndexOf("."));
        String extension = url.substring(url.lastIndexOf("."));
        filename = filename.replace(".","");
        url = filename + extension;
        return url;
    }

    /**
     * Get the file extension of the file so data type can be identified
     * @param url the url of the file address
     * @return the file extension
     */
    public static String getFileExtension(String url) {
        String extension = url.substring(url.lastIndexOf(".") + 1);
        extension = extension.toLowerCase();
        return extension;
    }

}
