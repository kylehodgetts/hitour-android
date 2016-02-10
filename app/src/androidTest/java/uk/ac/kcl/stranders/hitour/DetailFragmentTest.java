package uk.ac.kcl.stranders.hitour;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;


public class DetailFragmentTest extends ActivityInstrumentationTestCase2<DetailActivity> {


    public DetailFragmentTest() {
        super(DetailActivity.class);
    }

    public void testActivityExists() {
        assertNotNull(getActivity());
    }

    public void testFragmentViewsExist() {
        TextView titleView = (TextView) getActivity().findViewById(R.id.text_title);
        TextView bodyView = (TextView) getActivity().findViewById(R.id.text_body);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.photo);
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt("1"));

        assertNotNull(titleView);
        assertNotNull(bodyView);
        assertNotNull(imageView);
        assertNotNull(videoView);
    }

    public void testVideoSize() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt("1"));
        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.detail_body);

        getInstrumentation().waitForIdleSync();
        assertEquals(linearLayout.getWidth(), videoView.getWidth());
    }

    public void testVideoPlayPause() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt("1"));
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, videoView);
        assertEquals(true, videoView.isPlaying());

        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, videoView);
        assertEquals(false, videoView.isPlaying());
    }

    public void testVideoResume() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt("1"));
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

    public void testVideoResumeOnRotation() {
        VideoView videoView = (VideoView) getActivity().findViewById(Integer.parseInt("1"));
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

}
