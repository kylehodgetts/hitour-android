package uk.ac.kcl.stranders.hitour;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {
    private Cursor mCursor;
    private Context mContext;

    public FeedAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_feed, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.tvTitle.setText(mCursor.getString(PrototypeData.TITLE));
        holder.tvDescription.setText(mCursor.getString(PrototypeData.DESCRIPTION));
        int imageId = mCursor.getInt(PrototypeData.IMAGE);
        holder.ivThumbnail.setImageDrawable(mContext.getResources().getDrawable(imageId));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvDescription;

        public ViewHolder(View view) {
            super(view);
            ivThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.feed_title);
            tvDescription = (TextView) view.findViewById(R.id.feed_description);
        }
    }
}
