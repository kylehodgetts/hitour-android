package uk.ac.kcl.stranders.hitour;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.activity.ScanningActivity;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;

/**
 * Front-end instrumentation tests for the {@link uk.ac.kcl.stranders.hitour.activity.FeedActivity}.
 */
public class FeedActivityTest extends ActivityInstrumentationTestCase2<FeedActivity> {
    /**
     * How to test:
     * -Delete application if installed
     * -Enter manually the tour :Penguins123 , ensure that all points are locked.
     * -Run the test.
     */

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
     */
    public void testOpenLockedFragment() {
        int pointsOfTour [] = {4,5,2};
        FeedActivity feedActivity = getActivity();
        for(int i = 0 ; i < pointsOfTour.length ; i++) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            Instrumentation.ActivityMonitor activityMonitor =
                    getInstrumentation().addMonitor(ScanningActivity.class.getName(), null, false);
            getInstrumentation().waitForIdleSync();

            TouchUtils.clickView(this, floatingActionButton);
            getInstrumentation().waitForIdleSync();

            ScanningActivity scanningActivity = (ScanningActivity)
                    getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 9000);

            getInstrumentation().waitForIdleSync();
            final EditText etCodePinEntry = (EditText) scanningActivity.findViewById(R.id.etCodePinEntry);
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    etCodePinEntry.requestFocus();
                }
            });
            getInstrumentation().sendStringSync(pointsOfTour[i] + "");
            getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);
            getInstrumentation().waitForIdleSync();
            Button btnSubmit = (Button) scanningActivity.findViewById(R.id.btnSubmit);
            TouchUtils.clickView(this, btnSubmit);

            getInstrumentation().waitForIdleSync();


             feedActivity = getActivity();
            getInstrumentation().waitForIdleSync();
            getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_BACK);
            getInstrumentation().waitForIdleSync();
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
    /* Attempt to scroll down nb9
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
}
