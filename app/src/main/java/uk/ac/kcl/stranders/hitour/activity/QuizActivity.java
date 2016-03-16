package uk.ac.kcl.stranders.hitour.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.QUIZ_URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;

public class QuizActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Map<String, String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", FeedActivity.currentTourId);
        Cursor tourCursor = null;

        try {
            tourCursor = FeedActivity.database.getWholeByPrimary(TOUR_TABLE, partialPrimaryMap);
            tourCursor.moveToFirst();
            String quizURL = tourCursor.getString(tourCursor.getColumnIndex(QUIZ_URL));
            mWebView = (WebView) findViewById(R.id.activity_quiz_webview);

            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl(quizURL);
        } catch (NotInSchemaException e) {
            e.printStackTrace();
        }
    }

}
