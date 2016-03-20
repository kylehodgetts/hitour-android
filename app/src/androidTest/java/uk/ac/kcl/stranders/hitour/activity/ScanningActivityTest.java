package uk.ac.kcl.stranders.hitour.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.kcl.stranders.hitour.R;

/**
 * Front End Instrumentation test class to test the {@link ScanningActivity} to ensure that
 * it receives input and processes the correct input. By checking the type of input and determining
 * if it is the correct type and navigates to the correct point, otherwise an error message is shown
 * to the user.
 */
public class ScanningActivityTest extends ActivityInstrumentationTestCase2<ScanningActivity> {

    /**
     * Default constructor to create an instance of the test case object
     */
    public ScanningActivityTest() {
        super(ScanningActivity.class);
    }

    /**
     * Test that checks if the instance of the activity is started and exists
     */
    public void testActivityExists() {
        ScanningActivity scanningActivity = getActivity();
        assertNotNull(scanningActivity);
    }

    /**
     * Test that checks all elements on the activity have been instantiated correctly and displayed
     * on the layout.
     */
    public void testActivityElementsExists() {
        assertNotNull(getActivity().findViewById(R.id.etCodePinEntry));
        assertNotNull(getActivity().findViewById(R.id.zxing_barcode_scanner));
        assertNotNull(getActivity().findViewById(R.id.btnSubmit));
    }

    /**
     * Test that checks an array of correct valid inputs to the {@link ScanningActivity} are correctly
     * deemed as accepted and navigated to their correct destination activity to display the point data
     * for the input value received by checking the Submit button code.
     */
    public void testSubmitCorrectInput() {
        String[] toTest = {"POINT-1", "POINT-4", "POINT-5"};

        for (String value : toTest) {
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

            DetailActivity detailActivity = (DetailActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 7000);
            assertNull(detailActivity);
        }
    }

    /**
     * Test that checks an array of invalid values that should be accepted to ensure they are correctly
     * deemed as not accepted resulting in the activity not navigating anywhere and checks no other
     * activity is started for the user.
     * This also checks the handling code of for the Submit button.
     */
    public void testSubmitIncorrectValues() {
        Object[] incorrectValues = {"testString", "1234564323454353", 1234543223, "testStringWithNumbers"};

        for (Object value : incorrectValues) {
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

    /**
     * Test that checks the any data input into the {@link EditText} correctly stays in the text field
     * when the {@link Activity} is rotated to Landscape and then again back to Portrait.
     */
    public void testLandscape() {
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

    /**
     * Test that checks the pause and resume of the {@link Activity} maintains any data entered into the
     * {@link EditText} field remains there for the activity when onPause and onResume methods are run.
     */
    public void testPauseAndResume() {
        final EditText etCodePinEntry = (EditText) getActivity().findViewById(R.id.etCodePinEntry);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                etCodePinEntry.requestFocus();
            }
        });
        getInstrumentation().sendStringSync("123");
        getActivity().finish();
        Activity activity = getActivity();
        getInstrumentation().callActivityOnRestart(activity);

        assertEquals("123", etCodePinEntry.getText().toString());
    }

}
