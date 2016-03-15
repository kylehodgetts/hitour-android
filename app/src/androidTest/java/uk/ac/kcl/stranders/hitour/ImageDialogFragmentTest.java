package uk.ac.kcl.stranders.hitour;

import android.app.Fragment;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;
import uk.ac.kcl.stranders.hitour.fragment.ImageDialogFragment;

import static android.support.v4.app.ActivityCompat.startActivity;

public class ImageDialogFragmentTest extends ActivityInstrumentationTestCase2<DetailActivity> {
    private static DetailActivity mActivity;
    private static Fragment frag;

    public ImageDialogFragmentTest() {
        super(DetailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), DetailActivity.class);
        startActivity(mActivity, intent, null);
    }

    private Fragment startFragment(ImageDialogFragment fragment) {
        getInstrumentation().waitForIdleSync();
        frag = mActivity.getFragmentManager().findFragmentByTag("image_dialog_fragment");

        return frag;
    }

    public void testFragment() {
        ImageDialogFragment fragment = new ImageDialogFragment();

        frag = startFragment(fragment);
        assertNotNull(frag);
    }
}