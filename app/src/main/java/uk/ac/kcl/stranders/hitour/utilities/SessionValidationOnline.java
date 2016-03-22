package uk.ac.kcl.stranders.hitour.utilities;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.activity.FeedActivity;

public class SessionValidationOnline extends AsyncTask<Map<String, String>, Double, ArrayList<String>> {

    private final Context context;

    public SessionValidationOnline(Context context) {
        this.context = context;
    }

    protected ArrayList<String> doInBackground(Map<String, String>... params) {
        Map<String, String> sessionIdPassphraseMap = params[0];
        ArrayList<String> sessionIdArrayList = new ArrayList<>();
        for(Map.Entry<String, String> entry : sessionIdPassphraseMap.entrySet()) {
            if(!Utilities.sessionExistsOnline(entry.getKey())) {
                sessionIdArrayList.add(entry.getValue());
            }
        }
        return sessionIdArrayList;
    }
    protected void onPostExecute(ArrayList<String> result) {
        for (int i = 0; i < result.size(); i++) {
            DataManipulation.removeSession(result.get(i), context, FeedActivity.database);
        }
    }
}
