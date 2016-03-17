package uk.ac.kcl.stranders.hitour;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.DisplayMetrics;
import android.view.View;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;


public class DetailActivityTest extends ActivityInstrumentationTestCase2<DetailActivity>  {
    private DetailActivity mDetailActivity;
    private Instrumentation mInstrumentation;
    public DetailActivityTest(Class<DetailActivity> activityClass) {
        super(activityClass);
    }

    public DetailActivityTest() {
        super(DetailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        mInstrumentation = getInstrumentation();
        mDetailActivity = getActivity();

    }

    public void testActivityExists() {
        assertNotNull(mDetailActivity);
    }

    public void testOnCreate() throws Exception {

    }

    public void testOnCreateOptionsMenu() throws Exception {

    }

    public void testOnOptionsItemSelected() throws Exception {

    }

    public void testSwipeRight() {
        int[] xy = new int[2];
        View v = getActivity().getCurrentFocus();
        v.getLocationOnScreen(xy);
        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();
        final float x = xy[0] + (viewWidth / 2.0f);
        float fromY = xy[1] + (viewHeight / 2.0f);
        DisplayMetrics metrics = mDetailActivity.getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels;
//        int heightPixels = metrics.heightPixels;
        TouchUtils.drag(this, (widthPixels - 1), x,  fromY, fromY , 5);
    }

}