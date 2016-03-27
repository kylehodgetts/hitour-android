package stranders.hitour.fragment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import stranders.hitour.R;
import stranders.hitour.activity.DetailActivity;
import stranders.hitour.activity.FeedActivity;
import stranders.hitour.activity.ScanningActivity;

/**
 * Front end instrumentation test for the {@link DetailFragment} ensuring that its content is correctly
 * populated and the video behaves as it should when interacted with.
 */
public class DetailFragmentTest extends InstrumentationTestCase {

    /**
     * Tests the dynamic content on the loaded Detail Fragment matches the data that is returned by
     * the data for that point.
     *
     * This also checks the correct {@link View}'s are present for the correct item.
     */
    public void testDynamicContent() {
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

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10000);

        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(ScanningActivity.class.getName(), null, false);

        TouchUtils.clickView(this, fab);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etPasscodeEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("POINT-2");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

        instrumentation.removeMonitor(activityMonitor);
        activityMonitor = instrumentation.addMonitor(DetailActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);

        assertNotNull(currentActivity.findViewById(R.id.title));
        assertNotNull(currentActivity.findViewById(R.id.image));
        assertNotNull(currentActivity.findViewById(R.id.description));
    }


}
