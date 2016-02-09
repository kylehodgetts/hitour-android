package uk.ac.kcl.stranders.hitour;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CBaker on 08/02/2016.
 */
public class ScanningActivityTest extends ActivityInstrumentationTestCase2<ScanningActivity> {

    public ScanningActivityTest() {
        super(ScanningActivity.class);
    }

    public void testActivityExists() {
        ScanningActivity scanningActivity = getActivity();
        assertNotNull(scanningActivity);
    }

    public void testActivityElementsExists() {
        assertNotNull(getActivity().findViewById(R.id.etCodePinEntry));
        assertNotNull(getActivity().findViewById(R.id.zxing_barcode_scanner));
        assertNotNull(getActivity().findViewById(R.id.btnSubmit));
    }

    public void testSubmitCorrectInput() {
        int[] toTest = {0, 1, 2, 3};

        for(int value : toTest) {
            Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);

            final EditText etCodePinEntry = (EditText) getActivity().findViewById(R.id.etCodePinEntry);
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    etCodePinEntry.requestFocus();
                }
            });
            getInstrumentation().sendStringSync(value + "");
            getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

            Button btnSubmit = (Button) getActivity().findViewById(R.id.btnSubmit);
            TouchUtils.clickView(this, btnSubmit);

            getInstrumentation().waitForIdleSync();

            DetailActivity detailActivity = (DetailActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
            assertNotNull(detailActivity);
            detailActivity.finish();
        }
    }

    public void testSubmitIncorrectValues() {
        Object[] incorrectValues = {"testString", "1234564323454353", 1234543223, "testStringWithNumbers"};

        for(Object value : incorrectValues) {
            Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(DetailActivity.class.getName(), null, false);
            final EditText etCodePinEntry = (EditText) getActivity().findViewById(R.id.etCodePinEntry);
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    etCodePinEntry.requestFocus();
                }
            });
            getInstrumentation().sendStringSync(value + "");
            getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_ENTER);

            Button btnSubmit = (Button) getActivity().findViewById(R.id.btnSubmit);
            TouchUtils.clickView(this, btnSubmit);

            getInstrumentation().waitForIdleSync();

            DetailActivity detailActivity = (DetailActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
            assertNull(detailActivity);
        }
    }

    public void testLandscapeAndResume() {
        final EditText etCodePinEntry = (EditText) getActivity().findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etCodePinEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("123");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        assertEquals("123", etCodePinEntry.getText().toString());

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        assertEquals("123", etCodePinEntry.getText().toString());


        getActivity().finish();
        Activity activity = getActivity();
        getInstrumentation().callActivityOnRestart(activity);

        assertEquals("123", etCodePinEntry.getText().toString());
    }

}
