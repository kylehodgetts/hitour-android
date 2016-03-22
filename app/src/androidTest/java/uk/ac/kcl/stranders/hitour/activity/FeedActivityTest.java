package uk.ac.kcl.stranders.hitour.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
     * Test that checks whether the locked item cannot be accessed.
     */
    public void testAccessLockedFragment() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        FloatingActionButton fab = (FloatingActionButton) currentActivity.findViewById(R.id.fab);

        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, fab);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);

        final EditText etPasscodeEntry = (EditText) currentActivity.findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("SNPenguins123");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

        Button btnSubmit = (Button) currentActivity.findViewById(R.id.btnSubmit);

        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 20000);
        getInstrumentation().waitForIdleSync();

        RecyclerView mFeed = (RecyclerView) currentActivity.findViewById(R.id.rv_feed);
        assertNotNull(mFeed);
        TouchUtils.clickView(this, mFeed.getChildAt(0));

        FeedActivity feedActivity = (FeedActivity) currentActivity;
        Boolean isTablet = feedActivity.getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            Fragment detailFragment =
                    feedActivity.getSupportFragmentManager().findFragmentByTag(DetailFragment.FRAGMENT_TAG);

            assertNull(detailFragment);
        } else {
            DetailActivity detailActivity = (DetailActivity)
                    getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

            assertNull(detailActivity);
        }
        instrumentation.removeMonitor(activityMonitor);

    }

    public void testLayoutDrawerHeader() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);


        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, fab);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);

        final EditText etPasscodeEntry = (EditText) currentActivity.findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("SNPenguins123");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

        Button btnSubmit = (Button) currentActivity.findViewById(R.id.btnSubmit);

        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10000);

        instrumentation.removeMonitor(activityMonitor);

        TextView nameTextView = (TextView) currentActivity.findViewById(R.id.nav_tour_info);

        assertEquals(nameTextView.getText().toString(), "Tour with 2nd year students");

    }

}
