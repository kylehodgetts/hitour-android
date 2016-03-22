package uk.ac.kcl.stranders.hitour.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    public static boolean sessionExistsOnline(String passphrase) {
        try {
            InputStream inputStream = new URL("https://hitour.herokuapp.com/api/A7DE6825FD96CCC79E63C89B55F88/" + passphrase).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
            String result = text.toString();
            //Only return true if it starts with a curly brace indicating JSON
            if(result.startsWith("{"))
                return true;
        }
        catch (IOException e) {
            Log.e("IO_FAIL", Log.getStackTraceString(e));
        }
        return false;
    }

    public static boolean sessionExistsOffline(String startDate, String duration) {
        Calendar calendarFinish = getFinishDate(startDate, duration);
        Calendar calendarNow = Calendar.getInstance();
        return !calendarNow.after(calendarFinish);
    }

    public static Calendar getFinishDate(String startDate, String duration) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar calendarFinish = Calendar.getInstance();
            calendarFinish.setTime(sdf.parse(startDate));
            calendarFinish.add(Calendar.DATE, Integer.parseInt(duration));
            return calendarFinish;
        } catch (ParseException e) {
            Log.e("PARSE_FAIL", Log.getStackTraceString(e));
        }
        return null;
    }

}
