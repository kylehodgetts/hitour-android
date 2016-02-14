package uk.ac.kcl.stranders.hitour;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

/**
 * Front end instrumentation test for the {@link DetailFragment} ensuring that its content is correctly
 * populated and the video behaves as it should when interacted with.
 *
 */
public class DetailFragmentTest extends ActivityInstrumentationTestCase2<DetailActivity> {

    /**
     * Default constructor to set up the test for the {@link DetailActivity} which hosts the
     * {@link DetailFragment}
     */
    public DetailFragmentTest() {
        super(DetailActivity.class);
    }

    /**
     * Test to check the activity is correctly started and exists.
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }

    /**
     * Test that all of the content for the point has been loaded correctly on the fragment and
     * that they all exist.
     */
    public void testFragmentViewsExist() {
        TextView titleView = (TextView) getActivity().findViewById(R.id.text_title);
        TextView bodyView = (TextView) getActivity().findViewById(R.id.text_body);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.photo);

        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt(0+""+0));
        TextView videoTitle = (TextView) getActivity().findViewById(R.id.title);
        TextView videoDescription = (TextView) getActivity().findViewById(R.id.description);

        assertNotNull(titleView);
        assertNotNull(bodyView);
        assertNotNull(imageView);
        assertNotNull(videoView);
        assertNotNull(videoTitle);
        assertNotNull(videoDescription);
    }

    /**
     * Tests that the video is correctly sized and positioned to be the whole width of the the
     * {@link DetailFragment} and the height wrapped to its content.
     */
    public void testVideoSize() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt(0+""+0));
        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.detail_body);

        getInstrumentation().waitForIdleSync();
        assertEquals(linearLayout.getWidth(), videoView.getWidth());
    }

    /**
     * Tests that the video behaves as it should when playing and pausing. Which checks on a touch if
     * the video is playing it will pause. If it is touched and the video is not playing then it will
     * start to play.
     */
    public void testVideoPlayPause() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt(0+""+0));
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, videoView);
        assertEquals(true, videoView.isPlaying());

        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, videoView);
        assertEquals(false, videoView.isPlaying());
    }

    /**
     * Tests that that video when resumed, starts playing from the last point it left off and
     * not returning back to the beginning.
     */
    public void testVideoResume() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt(0+""+0));
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, videoView);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TouchUtils.clickView(this, videoView);
        assertTrue(videoView.getCurrentPosition() > 0);
    }

    /**
     * Tests that if the device is rotated the video will resume playing from the last point
     * it left off before the rotation occurred rather than start again from the beginning.
     */
    public void testVideoResumeOnRotation() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt(0+""+0));
        TouchUtils.clickView(this, videoView);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TouchUtils.clickView(this, videoView);

        getInstrumentation().waitForIdleSync();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        assertTrue(videoView.getCurrentPosition() > 0);
    }

    public void testDynamicContent() {

        Cursor contentCursor = PrototypeData.getContentCursor(0);
        contentCursor.moveToFirst();

        for(int i = 0; i < contentCursor.getCount(); ++i) {

            LinearLayout itemLayout = (LinearLayout) getActivity().findViewById(i + 100);
            getInstrumentation().waitForIdleSync();
            TextView videoTitle = (TextView) itemLayout.findViewById(R.id.title);
            TextView videoDescription = (TextView) itemLayout.findViewById(R.id.description);

            if(i == 0) {
                getInstrumentation().waitForIdleSync();
                assertNotNull(itemLayout.findViewById(Integer.parseInt(0+""+0)));
            }
            else {
                assertNotNull(itemLayout.findViewById(R.id.image));
            }

            assertNotNull(videoTitle);
            assertNotNull(videoDescription);

            assertEquals(videoTitle.getText(), contentCursor.getString(PrototypeData.DATA_TITLE));
            assertEquals(videoDescription.getText().toString(), contentCursor.getString(PrototypeData.DATA_DESCRIPTION));
            contentCursor.moveToNext();
        }

    }

}
