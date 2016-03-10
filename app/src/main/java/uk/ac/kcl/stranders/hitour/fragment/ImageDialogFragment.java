package uk.ac.kcl.stranders.hitour.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uk.ac.kcl.stranders.hitour.R;

public class ImageDialogFragment extends DialogFragment {

    private ImageView mImageView;
    private Bitmap mBitmap;

    /**
     * Empty constructor required
     */
    public ImageDialogFragment() {

    }

    public static ImageDialogFragment newInstance(int arg, Bitmap bmp) {
        ImageDialogFragment frag = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt("count", arg);
        frag.setArguments(args);
        frag.setBitmap(bmp);
        return frag;
    }

    public void setBitmap(Bitmap bmp){
        mBitmap = bmp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_dialog_fragment, container, false);

        mImageView = (ImageView)view.findViewById(R.id.image_dialog);
        mImageView.setImageBitmap(mBitmap);

        return view;
    }
}
