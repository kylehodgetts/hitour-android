package uk.ac.kcl.stranders.hitour.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.kcl.stranders.hitour.R;

public class DetailActivityTest extends InstrumentationTestCase {


    /**
     * Fetches the tour and unlocks two points.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unlockFirstPoint();
        unlockSecondPoint();
    }

    /**
     * Checks if the ViewPager embedded in the DetailActivity works correctly
     * allowing users to switch between unlocked points.
     *
     * The testing setup unlocks first and third element in the adapter, leaving the second one locked.
     * If the code is correct the ViewPager will switch from the first to the third fragment.
     */
    public void testSwipe() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Boolean isTablet = activityMonitor.getLastActivity().getResources().getBoolean(R.bool.isTablet);
        if(isTablet) { fail("The ViewPager and the DetailActivity are not used on tablets; " +
                "therefore, the activity cannot be tested"); }


        instrumentation.removeMonitor(activityMonitor);
        RecyclerView recyclerView = (RecyclerView) activityMonitor.getLastActivity().findViewById(R.id.rv_feed);

        activityMonitor =
                getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);
        TouchUtils.clickView(this, recyclerView.getChildAt(2));

        getInstrumentation().waitForIdleSync();

        DetailActivity detailActivity = (DetailActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 2000);
        getInstrumentation().removeMonitor(activityMonitor);
        int _pos1 = detailActivity.getCurrentPosition();

        int[] xy = new int[2];
        View v = detailActivity.findViewById(R.id.detail_body);
        v.getLocationOnScreen(xy);
        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();
        float x = xy[0] + (viewWidth / 6.0f);
        float fromY = xy[1] + (viewHeight / 6.0f);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        detailActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        //Drag from centre of screen to Leftmost edge of display
        TouchUtils.drag(this, (screenWidth - 1), x, fromY, fromY, 5);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getInstrumentation().waitForIdleSync();

        int _pos2 = detailActivity.getCurrentPosition();

        assertNotNull("Check if loaded fragments are different.", _pos1 != _pos2);
    }

    /**
     * Unlocks the first point in the test tour
     */
    private void unlockFirstPoint() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        instrumentation.removeMonitor(activityMonitor);

        FloatingActionButton fbtn = (FloatingActionButton) currentActivity.findViewById(R.id.fab);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, fbtn);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 1000);

        final EditText etPasscodeEntry = (EditText) currentActivity.findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("SNPenguins123");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

        instrumentation.removeMonitor(activityMonitor);

        Button btnSubmit = (Button) currentActivity.findViewById(R.id.btnSubmit);

        activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 3000);
        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);
        TouchUtils.clickView(this, fbtn);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        instrumentation.removeMonitor(activityMonitor);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("POINT-2");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);
        activityMonitor = instrumentation.addMonitor(DetailActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        instrumentation.removeMonitor(activityMonitor);
    }

    /**
     * Test that unlocks the second point in the test tour
     */
    private void unlockSecondPoint() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        instrumentation.removeMonitor(activityMonitor);

        FloatingActionButton fbtn = (FloatingActionButton) currentActivity.findViewById(R.id.fab);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, fbtn);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 1000);

        final EditText etPasscodeEntry = (EditText) currentActivity.findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("SNPenguins123");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

        instrumentation.removeMonitor(activityMonitor);

        Button btnSubmit = (Button) currentActivity.findViewById(R.id.btnSubmit);

        activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 3000);
        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);
        TouchUtils.clickView(this, fbtn);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        instrumentation.removeMonitor(activityMonitor);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("POINT-5");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);
        activityMonitor = instrumentation.addMonitor(DetailActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        instrumentation.removeMonitor(activityMonitor);
    }
}
