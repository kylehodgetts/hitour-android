package uk.ac.kcl.stranders.hitour.fragment;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.PrototypeData;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;

/**
 * Fragment that shows the content for a particular point in the tour which could consist of
 * text, images, videos or any number of combinations between them.
 *
 * That pulls the required data from local storage and displays in an ordered list using this fragment.
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
     * Static {@link DetailFragment} tag used to identify a fragment.
     */
    public static final String FRAGMENT_TAG = "uk.ac.kcl.stranders.hitour.DetailFragment.TAG";

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

    private VideoView currentVideo;

    /**
     * Stores the cursor to navigate the around the prototype data
     */
    private Cursor mCursor;

    private Cursor contentCursor;

    /**
     * Stores the current position of the video
     */
    private int currentPosition;

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
     * @param savedInstanceState {@link Bundle} with all the saved state variables
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Map<String,String> partialPrimaryMapTour = new HashMap<>();
        partialPrimaryMapTour.put("TOUR_ID", FeedActivity.currentTourId);
        try {
            mCursor = FeedActivity.database.getWholeByPrimaryPartial("POINT_TOUR", partialPrimaryMapTour);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getInt(ARG_ITEM_ID);
            mCursor.moveToPosition(mItemId);

            try {
                Map<String, String> partialPrimaryMapPoint = new HashMap<>();
                partialPrimaryMapPoint.put("POINT_ID", mCursor.getString(0));
                contentCursor = FeedActivity.database.getWholeByPrimaryPartial("POINT_DATA", partialPrimaryMapPoint);
                contentCursor.moveToFirst();
            } catch (Exception e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
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

        if(!(getResources().getBoolean(R.bool.isTablet))) {
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)
                    mRootView.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mImageView = (ImageView) mRootView.findViewById(R.id.photo);

        TextView titleView = (TextView) mRootView.findViewById(R.id.text_title);
        TextView bodyView = (TextView) mRootView.findViewById(R.id.text_body);

        if (mCursor != null && contentCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(2));
//            bodyView.setText(mCursor.getString(PrototypeData.DESCRIPTION));
//            int imageId = mCursor.getInt(PrototypeData.IMAGE);
//            mImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), imageId));

            LinearLayout linearLayout = (LinearLayout) mRootView.findViewById(R.id.detail_body);
            addContent(inflater, linearLayout, savedInstanceState);
        }
        return mRootView;
    }

    /**
     * Method that retrieves the dynamic content for the requested point and dynamically inflates
     * these views onto the fragment container in the order they are received in the content cursor.
     *
     * @param inflater {@link LayoutInflater} to inflate the content views
     * @param container {@link ViewGroup} to add all the inflated views for each item to
     * @param savedInstanceState {@link Bundle} to save the current state
     */
    private void addContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: Needs to change from PrototypeData to DB when available.
        for (int i = 0; i < contentCursor.getCount(); ++i) {
            LinearLayout layoutDetail;
            Map<String, String> pointMap = new HashMap<>();
            pointMap.put("DATA_ID", contentCursor.getString(0));
            try {
                Cursor dataCursor = FeedActivity.database.getWholeByPrimary("DATA", pointMap);
                dataCursor.moveToFirst();
                String url = dataCursor.getString(3);
                url = url.replace("/","");
                url = url.replace(":","");
                String filename = url.substring(0,url.lastIndexOf("."));
                String extension = url.substring(url.lastIndexOf("."));
                filename = filename.replace(".","");
                url = filename + extension;
                String localFilesAddress = getContext().getFilesDir().toString();
                url = localFilesAddress + "/" + url;
                String fileExtension = getFileExtension(dataCursor.getString(3));
                if (fileExtension.matches("jpg|jpeg|png")) {
                    layoutDetail = (LinearLayout) inflater.inflate(R.layout.image_detail, container, false);
                    ImageView imageView = (ImageView) layoutDetail.findViewById(R.id.image);
                    Bitmap bitmap = BitmapFactory.decodeFile(url);
                    imageView.setImageBitmap(bitmap);
                } else if (fileExtension.matches("mp4")) {
                    layoutDetail = (LinearLayout) inflater.inflate(R.layout.video_detail, container, false);
                    addVideo(savedInstanceState, layoutDetail, i, url);
                } else {
                    layoutDetail = (LinearLayout) inflater.inflate(R.layout.text_detail, container, false);
                    TextView tvText = (TextView) layoutDetail.findViewById(R.id.text);
                    tvText.setText(dataCursor.getString(1));
                }
                TextView tvTitle = (TextView) layoutDetail.findViewById(R.id.title);
                tvTitle.setText(contentCursor.getString(PrototypeData.DATA_TITLE));
                TextView tvDescription = (TextView) layoutDetail.findViewById(R.id.description);
                tvDescription.setText(contentCursor.getString(PrototypeData.DATA_DESCRIPTION));

                layoutDetail.setId(i + 100);
                container.addView(layoutDetail);
                contentCursor.moveToNext();
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }

        }
    }

    /**
     * Adds any videos to the {@link DetailFragment} to be shown for the particular point. Including
     * it's listeners to resume from where it left off, to set the position of the video back to the
     * start when it has finished and to play and pause when touched.
     *
     * @param savedInstanceState {@link Bundle} that contains any previous position to be restored such as rotation.
     */
    private void addVideo(final Bundle savedInstanceState, final LinearLayout linearLayout, int rank, String url) {

        if (url != null) {
            // TODO: retrieve links from DB and parse when available

            final VideoView videoView = (VideoView) linearLayout.findViewById(R.id.video);
            videoView.setId(Integer.parseInt(mItemId + rank + ""));
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
            videoView.seekTo(100);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(1000, 1000));
            videoView.setBackgroundResource(android.R.color.transparent);
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
                            currentVideo = videoView;
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
        if(currentVideo != null) {
            currentPosition = currentVideo.getCurrentPosition();
        }
    }

    private String getFileExtension(String url) {
        String extension = url.substring(url.lastIndexOf(".")+1);
        extension = extension.toLowerCase();
        return extension;
    }

}
