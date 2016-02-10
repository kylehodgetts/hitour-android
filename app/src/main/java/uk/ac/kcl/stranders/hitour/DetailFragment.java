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

public class DetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";
    private int mItemId;
    private View mRootView;
    private ImageView mImageView;
    private VideoView videoView;
    private Cursor mCursor;
    private int currentPosition;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int itemId) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_ITEM_ID, itemId);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCursor = PrototypeData.getCursor();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getInt(ARG_ITEM_ID);
            mCursor.moveToPosition(mItemId);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
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
            if(mCursor.getString(PrototypeData.VIDEO) != null) {
                Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                        mCursor.getString(PrototypeData.VIDEO));
                videoView = new VideoView(getActivity());
                final LinearLayout linearLayout = (LinearLayout) mRootView.findViewById(R.id.detail_body);

                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.setLayoutParams(new LinearLayout.LayoutParams(1000, 1000));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.setLayoutParams(new LinearLayout.LayoutParams(linearLayout.getWidth(),
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (savedInstanceState != null && savedInstanceState.containsKey("currentPosition")) {
                            currentPosition = savedInstanceState.getInt("currentPosition");
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

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPosition", currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("currentPostion")){
            currentPosition = savedInstanceState.getInt("currentPosition");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
    }
}
