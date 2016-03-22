package uk.ac.kcl.stranders.hitour.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.R;

/**
 * Test class that tests the Quiz is accessible only after all points are discovered
 */
public class QuizActivityTest extends InstrumentationTestCase {

    /**
     * Default set up method required to run the tests
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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

        assertNotNull(currentActivity);
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
        getInstrumentation().sendStringSync("POINT-4");
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);
        activityMonitor = instrumentation.addMonitor(DetailActivity.class.getName(), null, false);
        TouchUtils.clickView(this, btnSubmit);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 500);
        instrumentation.removeMonitor(activityMonitor);

        assertNotNull(currentActivity);
    }

    /**
     * Test that unlocks the third point in the test tour
     */
    private void unlockThirdPoint() {
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

        assertNotNull(currentActivity);

    }

    /**
     * test that checks after all points in the test tour have been discovered and the quiz is no
     * longer locked so it is now accessible.
     */
    public void testQuizAvailableAfterAllPointsUnlocked() {
        unlockFirstPoint();
        unlockSecondPoint();
        unlockThirdPoint();

        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        instrumentation.removeMonitor(activityMonitor);

        instrumentation.removeMonitor(activityMonitor);
        RecyclerView mFeed = (RecyclerView) currentActivity.findViewById(R.id.rv_feed);
        assertNotNull(mFeed);
        FeedAdapter.ViewHolder quizViewHolder = (FeedAdapter.ViewHolder) mFeed.findViewHolderForAdapterPosition(mFeed.getAdapter().getItemCount() - 1);

        activityMonitor = instrumentation.addMonitor(QuizActivity.class.getName(), null, false);
        TouchUtils.clickView(this, quizViewHolder.getView().findViewById(R.id.feed_title));
        instrumentation.removeMonitor(activityMonitor);
        assertFalse(quizViewHolder.getView().findViewById(R.id.fllock).isShown());
    }

    /**
     * Test that checks the Quiz activity loads and shows the quiz inside a webview
     */
    public void testQuizActivityLoads() {
        Instrumentation instrumentation = getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(FeedActivity.class.getName(), null, false);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), FeedActivity.class.getName());
        instrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        instrumentation.removeMonitor(activityMonitor);

        activityMonitor = getInstrumentation().addMonitor(QuizActivity.class.getName(), null, false);

        intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(getInstrumentation().getTargetContext(), QuizActivity.class.getName());
        getInstrumentation().startActivitySync(intent);

        currentActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 50000);
        assertNotNull(currentActivity);
        WebView webView = (WebView) currentActivity.findViewById(R.id.activity_quiz_webview);
        assertTrue(webView.isShown());
    }

}