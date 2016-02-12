package uk.ac.kcl.stranders.hitour;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.kcl.stranders.hitour.activity.DetailActivity;
import uk.ac.kcl.stranders.hitour.activity.FeedActivity;

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
                Intent intent = new Intent(mContext, DetailActivity.class)
                .putExtra(DetailActivity.EXTRA_BUNDLE, viewHolder.getAdapterPosition());
                mContext.startActivity(intent);
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
        holder.tvTitle.setText(mCursor.getString(PrototypeData.TITLE));
        holder.tvDescription.setText(mCursor.getString(PrototypeData.TEXT));
        int imageId = mCursor.getInt(PrototypeData.IMAGE);
        holder.ivThumbnail.setImageDrawable(ContextCompat.getDrawable(mContext, imageId));
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
