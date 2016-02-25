package uk.ac.kcl.stranders.hitour;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

/**
 * FeedAdapter provides a binding from a points data set to views
 * that are displayed within a {@link FeedActivity#mFeed}.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {

    /**
     * Stores the cursor that provides access to the feed data.
     */
    private Cursor mCursor;

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
        mCursor = cursor;
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

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start a new activity on a phone or replace a detail fragment on tablets.
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
        mCursor.moveToPosition(position);
        try {
            Map<String,String> primaryMap = new HashMap<>();
            primaryMap.put("POINT_ID", mCursor.getString(2));
            Cursor pointCursor = FeedActivity.database.getWholeByPrimary("POINT",primaryMap);
            pointCursor.moveToFirst();
            holder.tvTitle.setText(pointCursor.getString(1));
//        holder.tvDescription.setText(mCursor.getString(PrototypeData.DESCRIPTION));
//        int imageId = mCursor.getInt(PrototypeData.IMAGE);
//        holder.ivThumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, imageId));
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL",Log.getStackTraceString(e));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * {@link android.support.v7.widget.RecyclerView.ViewHolder} for views
     * in the feed list items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvDescription;

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
