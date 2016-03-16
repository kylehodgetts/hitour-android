package uk.ac.kcl.stranders.hitour.fragment;

import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.QUIZ_URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizFragment extends Fragment {

    private WebView mWebView;
    /**
     * Static String to name to store in a bundle the item's position in the feed addapter.
     */
    public static final String ARG_ITEM_POSITION = "ITEM_POSITION";

    private View mView;


    /**
     * Static {@link QuizFragment} tag used to identify a fragment.
     */
    public static final String FRAGMENT_TAG = "uk.ac.kcl.stranders.hitour.fragment.QuizFragment.TAG";

    public QuizFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_quiz, container, false);

        Map<String, String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", FeedActivity.currentTourId);
        Cursor tourCursor = null;

        try {
            tourCursor = FeedActivity.database.getWholeByPrimary(TOUR_TABLE, partialPrimaryMap);
            tourCursor.moveToFirst();
            String quizURL = tourCursor.getString(tourCursor.getColumnIndex(QUIZ_URL));
            mWebView = (WebView) mView.findViewById(R.id.fragment_quiz_webview);

            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(quizURL);

        } catch (NotInSchemaException e) {
            e.printStackTrace();
        }

        return mView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
