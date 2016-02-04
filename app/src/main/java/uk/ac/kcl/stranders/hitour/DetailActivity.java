package uk.ac.kcl.stranders.hitour;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.ViewGroup;

public class DetailActivity extends AppCompatActivity {

    public final static String EXTRA_BUNDLE = "uk.ac.kcl.stranders.hitour.DetailActivity.bundle";

    private Cursor mCursor;
    private int mStartId;
    private ViewPager mPager;
    private DetailPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCursor = PrototypeData.getCursor();

        mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(R.color.colorDivider)));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mStartId = getIntent().getIntExtra(EXTRA_BUNDLE, 0);
            }
        }

        mPager.setCurrentItem(mStartId, false);
    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {
        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return DetailFragment.newInstance(mCursor.getInt(PrototypeData._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}