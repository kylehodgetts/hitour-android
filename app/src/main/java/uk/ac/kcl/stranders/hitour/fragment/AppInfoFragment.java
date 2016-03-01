package uk.ac.kcl.stranders.hitour.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.ac.kcl.stranders.hitour.R;

public class AppInfoFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_info_fragment, container, false);

        //collect view components
        TextView content = (TextView) view.findViewById(R.id.app_info_content);

        // set HTML text
        content.setText(Html.fromHtml(getString(R.string.about_app_content)));

        return view;
    }
}
