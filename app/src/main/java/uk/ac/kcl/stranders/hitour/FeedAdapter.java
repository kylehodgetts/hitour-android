package uk.ac.kcl.stranders.hitour;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.activity.QuizActivity;
import uk.ac.kcl.stranders.hitour.database.DBWrap;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;
import uk.ac.kcl.stranders.hitour.fragment.QuizFragment;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DESCRIPTION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_TOUR_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.QUIZ_URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.SESSION_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.UNLOCK;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.URL;

/**
 * FeedAdapter provides a binding from a points data set to views
 * that are displayed within a {@link FeedActivity#mFeed}.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> implements Observer {

    /**
     * Stores the cursor that provides access to the feed data.
     */
    private Cursor pointTourCursor;

    /**
     * Stores the context of the application.
     */
    private Context mContext;

    /**
     * Stores the viewHolders matched by their respective tour_point primary key value.
     */
    private HashMap<Pair<Integer, Integer>, View> views;

    /**
     * Public constructor.
     *
     * @param cursor  {@link Cursor}
     * @param context {@link Context}
     */
    public FeedAdapter(Cursor cursor, Context context) {
        pointTourCursor = cursor;
        mContext = context;
        views = new HashMap<>();
    }

    /**
     * Sets a listener for each list item in addition to a default functionality.
     *
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        v.setContentDescription(v.getResources().getString(R.string.content_description_item_feed, viewHolder.tvTitle));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //Start a new activity on a phone or replace a detail fragment on tablets.
                if (isUnLocked(viewHolder.point_id, viewHolder.tour_id)) {
                    if (!(mContext.getResources().getBoolean(R.bool.isTablet))) {
                        // Start an activity for the quiz
//                        Intent quizIntent = new Intent(mContext, QuizActivity.class);
//                        mContext.startActivity(quizIntent);

                        Intent intent = new Intent(mContext, DetailActivity.class)
                                .putExtra(DetailActivity.EXTRA_POINT_ID, viewHolder.point_id);
                        Log.e("TEST_Awesome", "" + viewHolder.point_id);
                        mContext.startActivity(intent);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(DetailFragment.ARG_ITEM_POSITION, "" + viewHolder.point_id);
                        // Start a new activity on a phone or replace a detail fragment on tablets if unlocked.

                        DetailFragment fragment = new DetailFragment();
                        fragment.setArguments(bundle);

                        ((AppCompatActivity) mContext).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.point_detail_container, fragment, DetailFragment.FRAGMENT_TAG)
                                .commit();
                    }
                    viewHolder.getView().findViewById(R.id.fllock).setVisibility(View.GONE);

                }

            }
        });
        return viewHolder;
    }

    /**
     * Sets the point data to the feed list item.
     *
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        pointTourCursor.moveToPosition(position);
        try {
            Map<String, String> primaryMap = new HashMap<>();
            primaryMap.put("POINT_ID", pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID)));
            Cursor pointCursor = FeedActivity.database.getWholeByPrimary("POINT", primaryMap);
            pointCursor.moveToFirst();
            holder.tvTitle.setText(pointCursor.getString(pointCursor.getColumnIndex(NAME)));
            holder.tvDescription.setText(pointCursor.getString(pointCursor.getColumnIndex(DESCRIPTION)));

            String url = pointCursor.getString(pointCursor.getColumnIndex(URL));
            holder.point_id = Integer.parseInt(pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID)));
            holder.tour_id = Integer.parseInt(pointTourCursor.getString(pointTourCursor.getColumnIndex(TOUR_ID)));
            url = FeedActivity.createFilename(url);
            String localFilesAddress = mContext.getFilesDir().toString();
            url = localFilesAddress + "/" + url;
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            holder.ivThumbnail.setImageBitmap(bitmap);
            if (isUnLocked(holder.point_id, holder.tour_id)) {
                holder.getView().findViewById(R.id.fllock).setVisibility(View.GONE);
            }
            views.put(new Pair<>(holder.point_id, holder.tour_id), holder.getView());
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return pointTourCursor.getCount();
    }

    /**
     * Observes the ScannerActivity and update a viewHolder's view if appropriate
     *
     * @param observable in class ScannerActivity
     * @param data       a pair point and tour from ScannerActivty
     */
    @Override
    public void update(Observable observable, Object data) {
        @SuppressWarnings("unchecked")
        Integer point_id = ((Pair<Integer, Integer>) data).first;
        @SuppressWarnings("unchecked")
        Integer tour_id = ((Pair<Integer, Integer>) data).second;
        if (getView(point_id, tour_id) != null) {
            getView(point_id, tour_id).findViewById(R.id.fllock).setVisibility(View.GONE);
        }
    }

    /**
     * {@link android.support.v7.widget.RecyclerView.ViewHolder} for views
     * in the feed list items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvDescription;
        public Integer point_id;
        public Integer tour_id;
        private View view;

        /**
         * Public constructor.
         *
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ivThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.feed_title);
            tvDescription = (TextView) view.findViewById(R.id.feed_description);
        }

        public View getView() {
            return view;
        }
    }

    /**
     * Method to verify if ViewHolder has a tour_point that is unlocked.
     * If a tour_point for a specific viewHolder is unlocked then the lock FrameLayout is removed.
     *
     * @return unlocked state of a specific point_tour
     */
    private boolean isUnLocked(Integer point_id, Integer tour_id) {
        Map<String, String> tourPointPrimaryKeysMap = new HashMap<>();
        tourPointPrimaryKeysMap.put("TOUR_ID", "" + tour_id);
        tourPointPrimaryKeysMap.put("POINT_ID", "" + point_id);
        Cursor cursor;
        boolean unlockState = false;
        try {
            cursor = FeedActivity.database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, tourPointPrimaryKeysMap);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(UNLOCK) != -1) {
                    unlockState = (0 != Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNLOCK))));
                }
            }

        } catch (NotInSchemaException e) {
            e.printStackTrace();
        }
        return unlockState;
    }

    /**
     * Method to retrieve a specific ViewHolder
     *
     * @param point_id a point's id
     * @param tour_id  a tour's id
     * @return viewHolder for a specific pointid and tourid
     */
    private View getView(Integer point_id, Integer tour_id) {

        return views.get(new Pair<>(point_id, tour_id));
    }


}
