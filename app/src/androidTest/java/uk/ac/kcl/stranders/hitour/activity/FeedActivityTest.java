package uk.ac.kcl.stranders.hitour.activity;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;

/**
 * Front-end instrumentation tests for the {@link uk.ac.kcl.stranders.hitour.activity.FeedActivity}.
 */
public class FeedActivityTest extends ActivityInstrumentationTestCase2<FeedActivity> {

    public FeedActivityTest() {
        super(FeedActivity.class);
    }

    /**
     * Test whether the activity exists.
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }

    /**
     * Tests if all crucial UI components exist.
     */
    public void testActivityElementsExists() {
        assertNotNull(getActivity().findViewById(R.id.toolbar));
        assertNotNull(getActivity().findViewById(R.id.fab));
        assertNotNull(getActivity().findViewById(R.id.nav_view));
        assertNotNull(getActivity().findViewById(R.id.rv_feed));
    }

    /**
     * Test that the app does not crash upon rotation.
     */
    public void testOrientationChange() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getInstrumentation().waitForIdleSync();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getInstrumentation().waitForIdleSync();
        assertNotNull(getActivity());
    }

    /**
     * Test whether the fab button launches the scanning activity.
     */
    public void testFabButton() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        getInstrumentation().waitForIdleSync();
        Instrumentation.ActivityMonitor activityMonitor =
                getInstrumentation().addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, floatingActionButton);
        getInstrumentation().waitForIdleSync();

        ScanningActivity scanningActivity = (ScanningActivity)
                getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

        assertNotNull(scanningActivity);
        scanningActivity.finish();
    }

    /**
     * Enters the point's number via the scanner and unlock's the quiz
     * Returns to the FeedActivity.
     * Tested on phone
     */
    public void testOpenLockedFragment() {
        int pointsOfTour[] = {4, 5, 2};
        FeedActivity feedActivity = getActivity();
        for (int i = 0; i < pointsOfTour.length; i++) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            Instrumentation.ActivityMonitor activityMonitor =
                    getInstrumentation().addMonitor(ScanningActivity.class.getName(), null, false);
            getInstrumentation().waitForIdleSync();

            TouchUtils.clickView(this, floatingActionButton);
            getInstrumentation().waitForIdleSync();

            ScanningActivity scanningActivity = (ScanningActivity)
                    getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 1000);

            getInstrumentation().waitForIdleSync();
            final EditText etCodePinEntry = (EditText) scanningActivity.findViewById(R.id.etCodePinEntry);
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    etCodePinEntry.requestFocus();
                }
            });
            getInstrumentation().sendStringSync(pointsOfTour[i] + "");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getInstrumentation().waitForIdleSync();
            Button btnSubmit = (Button) scanningActivity.findViewById(R.id.btnSubmit);
            TouchUtils.clickView(this, btnSubmit);
            getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_BACK);
            feedActivity = getActivity();
            assertNotNull(feedActivity);
        }
        feedActivity.finish();

    }

    /**
     * Test that checks whether the {@link uk.ac.kcl.stranders.hitour.fragment.DetailFragment}
     * exists after selecting the list item from the {@link FeedAdapter}.
     */
    public void testlistItemSelection() {
        int ITEM_ID = 0;
        Instrumentation.ActivityMonitor activityMonitor =
                getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_feed);
        getInstrumentation().waitForIdleSync();

        // Select the first list item.
        TouchUtils.clickView(this, recyclerView.getChildAt(ITEM_ID));
        getInstrumentation().waitForIdleSync();

        FeedActivity feedActivity = getActivity();
        Boolean isTablet = feedActivity.getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            Fragment detailFragment =
                    feedActivity.getSupportFragmentManager().findFragmentByTag(DetailFragment.FRAGMENT_TAG);

            assertNotNull(detailFragment);
        } else {
            DetailActivity detailActivity = (DetailActivity)
                    getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

            Fragment detailFragment = detailActivity.getSupportFragmentManager().getFragments().get(0);
            assertNotNull(detailFragment);
            detailActivity.finish();

        }

    }


    //Missing
    /**
     * swipes left to right and right to left.
     * Need to assert a difference between frames swiped.
     */
    public void testSwipe() {
        Instrumentation.ActivityMonitor activityMonitor =
                getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_feed);
        TouchUtils.clickView(this, recyclerView.getChildAt(0));
        getInstrumentation().waitForIdleSync();
        int[] xy = new int[2];
        View v = getActivity().getCurrentFocus();
        v.getLocationOnScreen(xy);
        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 6.0f);
        float fromY = xy[1] + (viewHeight / 6.0f);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        //Drag from centre of screen to Leftmost edge of display
        TouchUtils.drag(this, (screenWidth - 1), x, fromY, fromY, 5);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FeedActivity feedActivity = getActivity();
        Boolean isTablet = feedActivity.getResources().getBoolean(R.bool.isTablet);

        getInstrumentation().waitForIdleSync();
        if (isTablet) {
            Fragment detailFragment = feedActivity.getSupportFragmentManager().findFragmentByTag(DetailFragment.FRAGMENT_TAG);
            //ASSERT SOMETHING
            assertNotNull(detailFragment);
        } else {
            DetailActivity detailActivity = (DetailActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 2000);
            //ASSERT SOMETHING ELSE
            detailActivity.finish();
        }
        x = x - (viewWidth / 6.0f);
        fromY = fromY - (viewWidth / 6.0f);
        TouchUtils.drag(this, (screenWidth - 1), x, fromY, fromY, 5);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Assert that it is another frame being displayed
        getInstrumentation().waitForIdleSync();
        getActivity().finish();

    }
    /* Attempt to scroll down nb9 scroll&launch quiz.
    public void testOpenQuiz() {

        int ITEM_ID = 1;
        Instrumentation.ActivityMonitor activityMonitor =
                getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_feed);
        getInstrumentation().waitForIdleSync();
        // Select the first list item.

        Log.i("Number of Layouts",""+recyclerView.getAdapter().getItemCount());
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int mScrollPosition = 0;
        if(layoutManager != null && layoutManager instanceof LinearLayoutManager){
            mScrollPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        layoutManager.scrollToPosition(mScrollPosition);
        TouchUtils.clickView(this, recyclerView.getChildAt(ITEM_ID));
        getInstrumentation().waitForIdleSync();

        FeedActivity feedActivity = getActivity();
        Boolean isTablet = feedActivity.getResources().getBoolean(R.bool.isTablet);

        if(isTablet) {
            Fragment detailFragment =
                    feedActivity.getSupportFragmentManager().findFragmentByTag(DetailFragment.FRAGMENT_TAG);

            assertNotNull(detailFragment);
        } else {
            DetailActivity detailActivity = (DetailActivity)
                    getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

            Fragment detailFragment = detailActivity.getSupportFragmentManager().getFragments().get(0);
            assertNotNull(detailFragment);
            detailActivity.finish();

        }

    }
    */

    private void setUpMockData() {
        Map<String,String> tourColumnsMap = new HashMap<>();
        tourColumnsMap.put("NAME","Michael Jackson Tour");
        tourColumnsMap.put("AUDIENCE_ID", "Children");
        tourColumnsMap.put("NAME", "https://hitour.herokuapp.com/quizzes/attempt_quiz/Unguessable983");
        Map<String, String> tourPrimaryKeysMap = new HashMap<>();
        tourPrimaryKeysMap.put("TOUR_ID", "0");

        Map<String,String> tourSessionColumnsMap = new HashMap<>();
        tourSessionColumnsMap.put("TOUR_ID","0");
        tourSessionColumnsMap.put("START_DATE", "2016-03-16");
        tourSessionColumnsMap.put("DURATION", "30");
        tourSessionColumnsMap.put("PASSPHRASE","Unguessable983");
        tourSessionColumnsMap.put("NAME", "Michael Jackson Tour");
        Map<String,String> tourSessionPrimaryKeysMap = new HashMap<>();
        tourSessionPrimaryKeysMap.put("SESSION_ID", "1");

        Map<String,String> pointColumnMap = new HashMap<>();
        pointColumnMap.put("NAME", "Fluorscope");
        pointColumnMap.put("URL","https://s3-us-west-2.amazonaws.com/hitourbucket/ExampleData/PointPhoto/fluroscopy.jpg");
        pointColumnMap.put("DESCRIPTION", "Don't be afraid of the Description");
        Map<String,String> pointPrimaryKeysMap = new HashMap<>();
        pointPrimaryKeysMap.put("POINT_ID", "1");

        Map<String, String> datumColumnsMap = new HashMap<>();
        datumColumnsMap.put("URL", "https://s3-us-west-2.amazonaws.com/hitourbucket/ExampleData/Fluoroscopy/OCH%27s+New+Fluoroscopy+System.mp4");
        datumColumnsMap.put("DESCRIPTION", "Come with me if you want to live");
        datumColumnsMap.put("TITLE", "Fluroscopy System Video - Modified");
        Map<String, String> datumPrimaryKeysMap = new HashMap<>();
        datumPrimaryKeysMap.put("DATA_ID", "1");

        Map<String, String> pointDatumColumnsMap = new HashMap<>();
        pointDatumColumnsMap.put("RANK", "1");
        Map<String, String> pointDataPrimaryKeysMap = new HashMap<>();
        pointDataPrimaryKeysMap.put("POINT_ID", "1");
        pointDataPrimaryKeysMap.put("DATA_ID", "1");

        Map<String, String> dataAudienceColumnsMap = new HashMap<>();
        Map<String, String> dataAudiencePrimaryKeysMap = new HashMap<>();
        dataAudiencePrimaryKeysMap.put("DATA_ID", "1");
        dataAudiencePrimaryKeysMap.put("AUDIENCE_ID","1");

        Map<String, String> tourPointColumnsMap = new HashMap<>();
        tourPointColumnsMap.put("RANK", "1");
        tourPointColumnsMap.put("UNLOCK","0");
        Map<String, String> tourPointPrimaryKeysMap = new HashMap<>();
        tourPointPrimaryKeysMap.put("TOUR_ID", "1");
        tourPointPrimaryKeysMap.put("POINT_ID","1");

        Map<String, String> audienceColumnsMap = new HashMap<>();
        Map<String, String> audiencePrimaryKeysMap = new HashMap<>();
        audiencePrimaryKeysMap.put("AUDIENCE_ID", "1");
        try {
            FeedActivity.database.insert(audienceColumnsMap, audiencePrimaryKeysMap, "AUDIENCE");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        try {
            FeedActivity.database.insert(tourPointColumnsMap, tourPointPrimaryKeysMap, "POINT_TOUR");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        try {
            FeedActivity.database.insert(dataAudienceColumnsMap, dataAudiencePrimaryKeysMap, "AUDIENCE_DATA");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        try {
            FeedActivity.database.insert(pointDatumColumnsMap, pointDataPrimaryKeysMap, "POINT_DATA");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        try {
            FeedActivity.database.insert(datumColumnsMap, datumPrimaryKeysMap, "DATA");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        try {
            FeedActivity.database.insert(pointColumnMap, pointPrimaryKeysMap, "POINT");
        } catch(NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        try {
            FeedActivity.database.insert(tourSessionColumnsMap, tourSessionPrimaryKeysMap, "SESSION");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        try {
            FeedActivity.database.insert(tourColumnsMap, tourPrimaryKeysMap, "TOUR");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }
}
