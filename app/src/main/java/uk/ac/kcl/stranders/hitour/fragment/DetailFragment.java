package uk.ac.kcl.stranders.hitour.fragment;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.AUDIENCE_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DATA_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DESCRIPTION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TITLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.URL;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devbrackets.android.exomedia.EMVideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.AUDIENCE_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DATA_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DESCRIPTION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.RANK;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TITLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.URL;

/**
 * Fragment that shows the content for a particular point in the tour which could consist of
 * text, images, videos or any number of combinations between them.
 *
 * That pulls the required data from local storage and displays in an ordered list using this fragment.
 */
public class DetailFragment extends Fragment {

    /**
     * Static String to name to store in a bundle the item's position in the feed addapter.
     */
    public static final String ARG_ITEM_POSITION = "ITEM_POSITION";

    /**
     * Static String to name to store in a bundle the item's ID.
     */
    public static final String ARG_ITEM_ID = "ITEM_ID";

    /**
     * Static String name to store in a bundle the videos' current positions
     */
    public static final String CURRENT_POSITION_ARRAY = "CURRENT_POSITION_ARRAY";

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

    /**
     * Stores a list of all the videos for the point
     */
    private ArrayList<EMVideoView> currentVideosArrayList;

    /**
     * Stores the cursor to navigate the points of the current tour
     */
    private Cursor pointTourCursor;

    /**
     * Stores the cursor to navigate the data of the current point
     */
    private Cursor pointDataCursor;

    /**
     * Stores the current position of the videos
     */
    private long[] currentPositionArray;

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
    public static DetailFragment newInstance(String itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ITEM_POSITION, itemId);
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
            pointTourCursor = FeedActivity.database.getUnlocked(DatabaseConstants.UNLOCK_STATE_UNLOCKED, FeedActivity.currentTourId);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Find the relevant data in a cursor
        int position = 0;
        if (getArguments().containsKey(ARG_ITEM_POSITION)) {
            // Get a cursor position if the detail fragment was launched from the feed
            position = findStartPosition(getArguments().getString(ARG_ITEM_POSITION));
        } else if (getArguments().containsKey(ARG_ITEM_ID)){
            // Get a cursor position if the detail fragment was launched from the scanner
            position = findStartPosition(getArguments().getString(ARG_ITEM_ID));
        }
        pointTourCursor.moveToPosition(position);

        try {
            Map<String, String> partialPrimaryMapPoint = new HashMap<>();
            partialPrimaryMapPoint.put("POINT_ID", pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID)));
            pointDataCursor = FeedActivity.database.getWholeByPrimaryPartialSorted("POINT_DATA", partialPrimaryMapPoint, RANK);
        } catch (Exception e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private int findStartPosition(String pin) {
        int position = -1;
        pointTourCursor.moveToPosition(0);
        do {
            String id = pointTourCursor.getString(pointTourCursor.getColumnIndex(DatabaseConstants.POINT_ID));
            ++position;
            if (id.equals(pin)) {
                break;
            }
        } while (pointTourCursor.moveToNext());
        return position;
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

        TextView titleView = (TextView) mRootView.findViewById(R.id.text_title);
        TextView bodyView = (TextView) mRootView.findViewById(R.id.text_body);
        mImageView = (ImageView) mRootView.findViewById(R.id.photo);

        if (pointTourCursor != null && pointDataCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            try {
                Map<String,String> primaryMap = new HashMap<>();
                primaryMap.put("POINT_ID", pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID)));
                Cursor pointCursor = FeedActivity.database.getWholeByPrimary("POINT",primaryMap);
                pointCursor.moveToFirst();

                titleView.setText(pointCursor.getString(pointCursor.getColumnIndex(NAME)));
                bodyView.setText(pointCursor.getString(pointCursor.getColumnIndex(DESCRIPTION)));

                String url = pointCursor.getString(pointCursor.getColumnIndex(URL));
                url = FeedActivity.createFilename(url);
                String localFilesAddress = getContext().getFilesDir().toString();
                url = localFilesAddress + "/" + url;
                Bitmap bitmap = BitmapFactory.decodeFile(url);
                mImageView.setImageBitmap(bitmap);
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }

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
        for (int i = 0; i < pointDataCursor.getCount(); ++i) {
            pointDataCursor.moveToPosition(i);
            if(checkDataAudience(pointDataCursor.getString(pointDataCursor.getColumnIndex(DATA_ID)))) {
                LinearLayout layoutDetail;
                Map<String, String> pointMap = new HashMap<>();
                pointMap.put("DATA_ID", pointDataCursor.getString(pointDataCursor.getColumnIndex(DATA_ID)));
                try {
                    Cursor dataCursor = FeedActivity.database.getWholeByPrimary("DATA", pointMap);
                    dataCursor.moveToFirst();
                    String url = dataCursor.getString(dataCursor.getColumnIndex(URL));
                    url = FeedActivity.createFilename(url);
                    String localFilesAddress = getContext().getFilesDir().toString();
                    url = localFilesAddress + "/" + url;
                    String fileExtension = FeedActivity.getFileExtension(dataCursor.getString(dataCursor.getColumnIndex(URL)));

                    StringBuilder text = new StringBuilder();
                    if (fileExtension.matches("jpg|jpeg|png")) {
                        layoutDetail = (LinearLayout) inflater.inflate(R.layout.image_detail, container, false);
                        final ImageView imageView = (ImageView) layoutDetail.findViewById(R.id.image);
                        final Bitmap bitmap = BitmapFactory.decodeFile(url);
                        imageView.setImageBitmap(bitmap);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                ImageDialogFragment imageDialogFragment = new ImageDialogFragment().newInstance(getArguments().describeContents(), bitmap, getActivity());
                                imageDialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);
                                imageDialogFragment.show(fm, "image_dialog_fragment");
                            }
                        });

                    } else if (fileExtension.matches("mp4")) {
                        layoutDetail = (LinearLayout) inflater.inflate(R.layout.video_detail, container, false);
                        addVideo(savedInstanceState, layoutDetail, i, url);
                    } else {
                        layoutDetail = (LinearLayout) inflater.inflate(R.layout.text_detail, container, false);
                        try {
                            File file = new File(url);
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                            text.append("\n\n");
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                        } catch (IOException e) {
                            Log.e("FILE_NOT_FOUND", Log.getStackTraceString(e));
                        }
                    }
                    TextView tvTitle = (TextView) layoutDetail.findViewById(R.id.title);
                    tvTitle.setText(dataCursor.getString(dataCursor.getColumnIndex(TITLE)));
                    TextView tvDescription = (TextView) layoutDetail.findViewById(R.id.description);
                    tvDescription.setText(dataCursor.getString(dataCursor.getColumnIndex(DESCRIPTION)) + text);
                    container.addView(layoutDetail);

                    layoutDetail.setId(i + 100);
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
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
            if(currentVideosArrayList == null) {
                currentVideosArrayList = new ArrayList<>();
            }
            final EMVideoView videoView = (EMVideoView) linearLayout.findViewById(R.id.video);
            currentVideosArrayList.add(videoView);
            videoView.setId(Integer.parseInt(mItemId + rank + ""));
            videoView.setLayoutParams(new LinearLayout.LayoutParams(1000, 1000));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Calculations to make video player in a 16:9 aspect ratio
                    int intWidth = linearLayout.getWidth();
                    float floatWidth = (float) intWidth;
                    float floatHeight = floatWidth * (9f / 16);
                    int intHeight = Math.round(floatHeight);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(intWidth, intHeight);
                    videoView.setLayoutParams(layoutParams);

                    // Resumes video in same play if device rotated or fragment is paused
                    if (currentPositionArray != null) {
                        long currentPosition = currentPositionArray[currentVideosArrayList.indexOf(videoView)];
                        videoView.seekTo((int) currentPosition);
                    }
                }
            });
            videoView.setVideoURI(Uri.parse(url));
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
        if(currentVideosArrayList != null) {
            long[] positionArray = new long[currentVideosArrayList.size()];
            for(int i = 0; i < currentVideosArrayList.size(); i++) {
                positionArray[i] = currentVideosArrayList.get(i).getCurrentPosition();
            }
            outState.putLongArray(CURRENT_POSITION_ARRAY, positionArray);
        }
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
        if(savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION_ARRAY)){
           currentPositionArray = savedInstanceState.getLongArray(CURRENT_POSITION_ARRAY);
        }
    }

    /**
     * Pauses the fragment and stores the current position of the video.
     */
    @Override
    public void onPause() {
        super.onPause();
        if(currentVideosArrayList != null) {
            currentPositionArray = new long[currentVideosArrayList.size()];
            for(int i = 0; i < currentVideosArrayList.size(); i++) {
                currentPositionArray[i] = currentVideosArrayList.get(i).getCurrentPosition();
                currentVideosArrayList.get(i).pause();
            }
        }
    }

    private boolean checkDataAudience(String dataId) {
        try {
            Map<String, String> partialPrimaryMap = new HashMap<>();
            partialPrimaryMap.put("DATA_ID", dataId);
            Cursor dataAudienceCursor = FeedActivity.database.getWholeByPrimaryPartial("AUDIENCE_DATA", partialPrimaryMap);
            String currentTourId = FeedActivity.currentTourId;
            Map<String,String> primaryMap = new HashMap<>();
            primaryMap.put("TOUR_ID", currentTourId);
            Cursor tourCursor = FeedActivity.database.getWholeByPrimary("TOUR", primaryMap);
            tourCursor.moveToFirst();
            String audienceId = tourCursor.getString(tourCursor.getColumnIndex(AUDIENCE_ID));
            for(int i = 0; i < dataAudienceCursor.getCount(); i++) {
                dataAudienceCursor.moveToPosition(i);
                if(audienceId.equals(dataAudienceCursor.getString(dataAudienceCursor.getColumnIndex(AUDIENCE_ID)))) {
                    return true;
                }
            }
        } catch(NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        return false;
    }
}
