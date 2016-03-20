package uk.ac.kcl.stranders.hitour.activity;

import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.R;
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
     * Test that checks whether the {@link uk.ac.kcl.stranders.hitour.fragment.DetailFragment}
     * exists after selecting the list item from the {@link FeedAdapter}.
     */
    public void testListItemSelection() {
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

}
