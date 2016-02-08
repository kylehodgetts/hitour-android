package uk.ac.kcl.stranders.hitour;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";
    private int mItemId;
    private View mRootView;
    private ImageView mImageView;
    private Cursor mCursor;

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
            bodyView.setText(mCursor.getString(PrototypeData.TEXT));
            int imageId = mCursor.getInt(PrototypeData.IMAGE);
            mImageView.setImageDrawable(getActivity().getResources().getDrawable(imageId));
        }

        return mRootView;
    }

}
