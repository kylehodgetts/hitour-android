package uk.ac.kcl.stranders.hitour.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uk.ac.kcl.stranders.hitour.R;

public class ImageDialogFragment extends DialogFragment {

    private ImageView mImageView;

    /**
     * Empty constructor required
     */
    public ImageDialogFragment() {

    }

    public void setImageView(ImageView v) {
        mImageView = v;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_dialog_fragment, container, false);

        return view;
    }
}
