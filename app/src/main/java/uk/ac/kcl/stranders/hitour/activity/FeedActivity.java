package uk.ac.kcl.stranders.hitour.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.PrototypeData;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.database.DBWrap;
import uk.ac.kcl.stranders.hitour.database.schema.HiSchema;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;
import uk.ac.kcl.stranders.hitour.model.DataType;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.retrofit.HiTourRetrofit;

/**
 * The main activity that displays all available points for a given tour.
 */
public class FeedActivity extends AppCompatActivity implements HiTourRetrofit.CallbackRetrofit {

    /**
     * The list of all available points.
     */
    private RecyclerView mFeed;

    /**
     * {@link android.support.v7.widget.RecyclerView.LayoutManager used to set parameters
     * for the {@link FeedActivity#mFeed}.
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Stores a reference to the {@link DrawerLayout}
     * that contains a {@link NavigationView}.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * A middle layer to interact with a local database.
     */
    public static DBWrap database;

    private HiTourRetrofit hiTourRetrofit;

    /**
     * Initializes the UI and sets an adapter for the {@link FeedActivity#mFeed}
     * @param savedInstanceState {@link Bundle} with all the saved state variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DBWrap(this, new HiSchema(1));
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mFeed = (RecyclerView) findViewById(R.id.rv_feed);

        mFeed.setHasFixedSize(true);

        // Display list items depending on the device orientation.
        // Hide the Up button on tablets.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(getResources().getBoolean(R.bool.isTablet)) {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            }
        } else {
            if(getResources().getBoolean(R.bool.isTablet)) {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            }
        }

        mFeed.setLayoutManager(mLayoutManager);
        FeedAdapter adapter = new FeedAdapter(PrototypeData.getCursor(), this);
        mFeed.setAdapter(adapter);

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        // Fetch data from the HiTour web API
        hiTourRetrofit = new HiTourRetrofit(this);
        hiTourRetrofit.fetchAll();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(!(getResources().getBoolean(R.bool.isTablet))) {
                Intent intent = new Intent(this, DetailActivity.class)
                        .putExtra(DetailActivity.EXTRA_BUNDLE, data.getExtras().getInt("pin"));
                startActivity(intent);
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(DetailFragment.ARG_ITEM_ID, data.getExtras().getInt("pin"));

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.point_detail_container, fragment, DetailFragment.FRAGMENT_TAG)
                        .commitAllowingStateLoss();
            }
        }
    }

    /**
     * Initializes an {@link IntentIntegrator} and launches the scanning activity.
     */
    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanningActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAllRequestsFinished() {
        // TODO: populate/update the db
        // test
        String name = ((List<Tour>) hiTourRetrofit.getList(DataType.TOUR)).get(0).getName();
        Log.d("NAME", name);
    }


}
