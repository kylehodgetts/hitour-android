package uk.ac.kcl.stranders.hitour.fragment;

import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.utilities.Utilities;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.*;

/**
 * A placeholder fragment containing a web view.
 */
public class QuizFragment extends Fragment {

    /**
     * Stores the web view shown in the fragment
     */
    private WebView mWebView;

    /**
     * Static String to name to store in a bundle the item's position in the feed adapter.
     */
    public static final String ARG_ITEM_POSITION = "ITEM_POSITION";

    /**
     * Static {@link QuizFragment} tag used to identify a fragment.
     */
    public static final String FRAGMENT_TAG = "uk.ac.kcl.stranders.hitour.fragment.QuizFragment.TAG";

    /**
     * Default empty required public constructor
     */
    public QuizFragment() {

    }

    /**
     * Creates and inflates the views on the {@link Fragment} from the url of the selected quiz
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup} of where the views are to be created into
     * @param savedInstanceState {@link Bundle} with all the saved state variables
     * @return {@link View} Fragment containing the web view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Get the cursor that will get the quiz url from the database
        Map<String, String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put(TOUR_ID, FeedActivity.currentTourId);
        Cursor tourCursor = null;

        try {
            tourCursor = FeedActivity.database.getWholeByPrimary(TOUR_TABLE, partialPrimaryMap);
            tourCursor.moveToFirst();
            String quizURL = tourCursor.getString(tourCursor.getColumnIndex(QUIZ_URL));

            // The web view to be displayed
            mWebView = (WebView) mView.findViewById(R.id.fragment_quiz_webview);

            // Enable Javascript and load the url
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl(quizURL);

        } catch (NotInSchemaException e) {
            e.printStackTrace();
        }

        // While browsing the quiz, if the network is turned off, stop the fragment
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!Utilities.isNetworkAvailable(getActivity().getApplicationContext())) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getActivity(),"@string/no_network_quiz", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return mView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
