package uk.ac.kcl.stranders.hitour;


import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import uk.ac.kcl.stranders.hitour.models.Data;

/**
 * Fragment that shows the content for a particular point in the tour which could consist of
 * text, images, videos or any number of combinations between them.
 *
 * That pulls the required data from local storage and displays in an ordered list using this fragment.
 *
 */
public class DetailFragment extends Fragment {

    /**
     * Static String to name to store in a bundle the item's ID
     */
    public static final String ARG_ITEM_ID = "ITEM_ID";

    /**
     * Static String name to store in a bundle the video's current position
     */
    public static final String CURRENT_POSITION = "CURRENT_POSITION";

    /**
     * Stores the integer ID of the current item selected to view
     */
    private int mItemId;

    /**
     * Stores the root view where the fragment is inflated to
     */
    private View mRootView;

    /**
     * Stores the main image shown for the point
     */
    private ImageView mImageView;

    /**
     * Stores the video to be shown for the point
     */
    private VideoView videoView;

    /**
     * Stores the cursor to navigate the around the prototype data
     */
    private Cursor mCursor;

    /**
     * Stores the current position of the video
     */
    private int currentPosition;

    private ArrayList<Data> items;

    /**
     * Default empty required public constructor
     */
    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of a fragment for the specified item id
     *
     * @param itemId Integer item id
     * @return {@link Fragment}
     */
    public static DetailFragment newInstance(int itemId) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_ITEM_ID, itemId);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Set's up the {@link Fragment}'s data ready for it's views to be created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCursor = PrototypeData.getCursor();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getInt(ARG_ITEM_ID);
            mCursor.moveToPosition(mItemId);
        }
    }

    /**
     * Creates and inflates the view's on the {@link Fragment} from the data for the selected point
     * including its images, text and videos.
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup} of where the views are to be created into
     * @param savedInstanceState {@link Bundle} with all the saved state variables
     * @return {@link View} Fragment containing all of its views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mImageView = (ImageView) mRootView.findViewById(R.id.photo);

        TextView titleView = (TextView) mRootView.findViewById(R.id.text_title);
        TextView bodyView = (TextView) mRootView.findViewById(R.id.text_body);

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(PrototypeData.TITLE));
            bodyView.setText(mCursor.getString(PrototypeData.DESCRIPTION));
            int imageId = mCursor.getInt(PrototypeData.IMAGE);
            mImageView.setImageDrawable(getActivity().getResources().getDrawable(imageId));



            // TODO: retrieve video links from DB when available
            addVideos(savedInstanceState);

        }

        return mRootView;
    }

    /**
     * Adds any videos to the {@link DetailFragment} to be shown for the particular point. Including
     * it's listeners to resume from where it left off, to set the position of the video back to the
     * start when it has finished and to play and pause when touched.
     *
     * @param savedInstanceState {@link Bundle} that contains any previous position to be restored such as rotation.
     */
    private void addVideos(final Bundle savedInstanceState) {
        if(mCursor.getString(PrototypeData.VIDEO) != null) {
            Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                    mCursor.getString(PrototypeData.VIDEO));
            videoView = new VideoView(getActivity());
            videoView.setId(Integer.parseInt("1"));
            final LinearLayout linearLayout = (LinearLayout) mRootView.findViewById(R.id.detail_body);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.setLayoutParams(new LinearLayout.LayoutParams(1000, 1000));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(linearLayout.getWidth(),
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION)) {
                        currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
                    }
                }
            });
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            currentPosition = videoView.getCurrentPosition();
                        } else {
                            videoView.seekTo(currentPosition);
                            videoView.start();
                        }
                        return true;
                    }
                    return false;
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPosition = 0;
                }
            });

            linearLayout.addView(videoView);
        }
    }

    /**
     * Saves the current state of the fragment and saves the current position of the video used when
     * the fragment is paused.
     *
     * @param outState {@link Bundle}
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_POSITION, currentPosition);
        super.onSaveInstanceState(outState);
    }

    /**
     * Restores the state of the fragment from the {@link Bundle} and restores the video's positions
     * to its last position
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION)){
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }
    }

    /**
     * Pauses the fragment and stores the current position of the video.
     */
    @Override
    public void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
    }

}
