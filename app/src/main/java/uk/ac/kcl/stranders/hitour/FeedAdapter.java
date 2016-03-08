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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DESCRIPTION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_TOUR_COLUMN_UNLOCK;
/**
 * FeedAdapter provides a binding from a points data set to views
 * that are displayed within a {@link FeedActivity#mFeed}.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {

    /**
     * Stores the cursor that provides access to the feed data.
     */
    private Cursor pointTourCursor;

    /**
     * Stores the context of the application.
     */
    private Context mContext;

    /**
     * Public constructor.
     *
     * @param cursor {@link Cursor}
     * @param context {@link Context}
     */
    public FeedAdapter(Cursor cursor, Context context) {
        pointTourCursor = cursor;
        mContext = context;
    }

    /**
     * Sets a listener for each list item in addition to a default functionality.
     *
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        v.setContentDescription(v.getResources().getString(R.string.content_description_item_feed, viewHolder.tvTitle));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start a new activity on a phone or replace a detail fragment on tablets.


                Cursor cursor = pointTourCursor;
                cursor.moveToFirst();
                Log.i("point", cursor.getString(POINT_COLUMN_NAME));
                Log.i("Unlock state",cursor.getString(POINT_TOUR_COLUMN_UNLOCK));
                while(true){
                    cursor.moveToNext();
                    Log.i("point", cursor.getString(POINT_COLUMN_NAME));
                    Log.i("Unlock state",cursor.getString(POINT_TOUR_COLUMN_UNLOCK));
                    if(cursor.isLast()){break;}
                }

                Log.i("Column Index", ""+pointTourCursor.getColumnIndex("UNLOCK"));
                Log.i("adapter Position", ""+viewHolder.getAdapterPosition());
                Log.i("pointId",""+viewHolder.point_id);

                if (!(mContext.getResources().getBoolean(R.bool.isTablet))) {
                    Intent intent = new Intent(mContext, DetailActivity.class)
                            .putExtra(DetailActivity.EXTRA_BUNDLE, viewHolder.getAdapterPosition());
                    mContext.startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(DetailFragment.ARG_ITEM_ID, viewHolder.getAdapterPosition());

                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(bundle);

                    ((AppCompatActivity) mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.point_detail_container, fragment, DetailFragment.FRAGMENT_TAG)
                            .commit();
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
            Map<String,String> primaryMap = new HashMap<>();
            primaryMap.put("POINT_ID", pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID)));
            Cursor pointCursor = FeedActivity.database.getWholeByPrimary("POINT",primaryMap);
            pointCursor.moveToFirst();
            holder.tvTitle.setText(pointCursor.getString(pointCursor.getColumnIndex(NAME)));
            holder.tvDescription.setText(pointCursor.getString(pointCursor.getColumnIndex(DESCRIPTION)));

            String url = pointCursor.getString(pointCursor.getColumnIndex(URL));
            holder.point_id = Integer.parseInt(pointTourCursor.getString(POINT_TOUR_COLUMN_POINT_ID));
            url = FeedActivity.createFilename(url);
            String localFilesAddress = mContext.getFilesDir().toString();
            url = localFilesAddress + "/" + url;
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            holder.ivThumbnail.setImageBitmap(bitmap);

        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL",Log.getStackTraceString(e));
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
     * {@link android.support.v7.widget.RecyclerView.ViewHolder} for views
     * in the feed list items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvDescription;
        public Integer point_id;
        
//        public int databaseposition;
        /**
         * Public constructor.
         *
         * @param view {@link View}
         */
        public ViewHolder(View view) {
            super(view);
            ivThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.feed_title);
            tvDescription = (TextView) view.findViewById(R.id.feed_description);
        }
    }
}
