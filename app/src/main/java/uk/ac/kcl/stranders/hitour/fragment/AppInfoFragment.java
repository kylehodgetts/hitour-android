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

    private MenuInflater mInflater;
    /**
     * Default empty required public constructor
     */
    public AppInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("___LOG____", "Attempting to open About section");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.app_info_fragment, container, false);

        // Collect view components
        mTextView = (TextView) mRootView.findViewById(R.id.app_info_content);

        // Set HTML text
        mTextView.setText(Html.fromHtml(getString(R.string.about_app_content)));

        return mRootView;
    }

    public Integer getIntArgument(String key) {
        return getArguments() == null ? null : getArguments().getInt(key);
    }
}
