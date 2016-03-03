package uk.ac.kcl.stranders.hitour.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.ac.kcl.stranders.hitour.R;

/**
 * DialogFragment that shows the application information from any activity
 */
public class AppInfoFragment extends DialogFragment {

    /**
     * Stores the main text view shown on the section
     */
    private TextView mTextView;

    /**
     * Empty constructor required
     */
    public AppInfoFragment(){

    }

    /**
     * Creates and inflates the fragment layout in a DialogFragment format
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup} of where the views are to be created from
     * @param savedInstanceState {@link Bundle} with all the saved state variables
     * @return {@link View} DialogFragment containing all of its views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_info_fragment, container, false);

        // Collect view components
        mTextView = (TextView) view.findViewById(R.id.app_info_content);

        // Set HTML text
        mTextView.setText(Html.fromHtml(getString(R.string.about_app_content)));
        Log.d("____THIS____", "In the fragment");

        getDialog().setTitle("About hiTour");

        return view;
    }
}
