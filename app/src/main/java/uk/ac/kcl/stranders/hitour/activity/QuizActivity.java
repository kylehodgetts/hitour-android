package uk.ac.kcl.stranders.hitour.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.Utilities;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.QUIZ_URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;

public class QuizActivity extends AppCompatActivity {

    /**
     * Stores the web view shown in the fragment
     */
    private WebView mWebView;

    /**
     * Initializes and populates {@link QuizActivity}
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get the cursor that will get the quiz url from the database
        Map<String, String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", FeedActivity.currentTourId);
        Cursor tourCursor = null;

        try {
            tourCursor = FeedActivity.database.getWholeByPrimary(TOUR_TABLE, partialPrimaryMap);
            tourCursor.moveToFirst();
            String quizURL = tourCursor.getString(tourCursor.getColumnIndex(QUIZ_URL));

            // The web view to be displayed
            mWebView = (WebView) findViewById(R.id.activity_quiz_webview);

            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(quizURL);
        } catch (NotInSchemaException e) {
            e.printStackTrace();
        }

        // While browsing the quiz, if the network is turned off, stop the activity
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!Utilities.isNetworkAvailable(getApplicationContext())) {
                    finish();
                    Toast.makeText(getApplicationContext(), "@string/no_network_quiz", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

}
