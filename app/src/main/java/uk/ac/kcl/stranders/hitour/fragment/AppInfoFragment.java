package uk.ac.kcl.stranders.hitour.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.ac.kcl.stranders.hitour.R;

public class AppInfoFragment extends Fragment {

    /**
     * Static {@link DetailFragment} tag used to identify a fragment.
     */
    public static final String FRAGMENT_TAG = "uk.ac.kcl.stranders.hitour.AppInfoFragment.TAG";

    /**
     * Stores the root view where the fragment is inflated to
     */
    private View mRootView;

    /**
     * Stores the main text view shown on the section
     */
    private TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.app_info_fragment, container, false);

        // collect view components
        mTextView = (TextView) mRootView.findViewById(R.id.app_info_content);

        // set HTML text
        mTextView.setText(Html.fromHtml(getString(R.string.about_app_content)));
        Log.d("____THIS____", "In the fragment");
        return mRootView;
    }
}
