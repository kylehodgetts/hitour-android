package uk.ac.kcl.stranders.hitour.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DURATION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.SESSION_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.SESSION_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.START_DATE;

public class SessionValidationOffline extends AsyncTask<Map<String, String>, Double, ArrayList<String>> {

    Context context;

    public SessionValidationOffline(Context context) {
        this.context = context;
    }

    protected ArrayList<String> doInBackground(Map<String, String>... params) {
        try {
            Map<String, String> sessionIdPassphraseMap = params[0];
            ArrayList<String> sessionIdArrayList = new ArrayList<>();
            for (Map.Entry<String, String> entry : sessionIdPassphraseMap.entrySet()) {
                Map<String, String> primaryKeysMap = new HashMap<>();
                primaryKeysMap.put(SESSION_ID, entry.getValue());
                // Get START_DATE and DURATION for this session
                Cursor sessionCursor = FeedActivity.database.getWholeByPrimary(SESSION_TABLE, primaryKeysMap);
                sessionCursor.moveToFirst();
                String startDate = sessionCursor.getString(sessionCursor.getColumnIndex(START_DATE));
                String duration = sessionCursor.getString(sessionCursor.getColumnIndex(DURATION));
                if (!Utilities.sessionExistsOffline(startDate, duration)) {
                    sessionIdArrayList.add(entry.getValue());
                }
            }
            return sessionIdArrayList;
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        return null;
    }
    protected void onPostExecute(ArrayList<String> result) {
        if(result != null) {
            for (int i = 0; i < result.size(); i++) {
                DataManipulation.removeSession(result.get(i), context, FeedActivity.database);
            }
        }
    }
}