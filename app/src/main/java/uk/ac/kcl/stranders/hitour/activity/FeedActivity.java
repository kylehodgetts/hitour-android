package uk.ac.kcl.stranders.hitour.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import uk.ac.kcl.stranders.hitour.utilities.CustomTypefaceSpan;
import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.utilities.Utilities;
import uk.ac.kcl.stranders.hitour.database.DBWrap;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants;
import uk.ac.kcl.stranders.hitour.database.schema.HiSchema;
import uk.ac.kcl.stranders.hitour.fragment.AppInfoFragment;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;
import uk.ac.kcl.stranders.hitour.model.Data;
import uk.ac.kcl.stranders.hitour.model.Point;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.TourSession;
import uk.ac.kcl.stranders.hitour.retrofit.HiTourRetrofit;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.AUDIENCE_DATA_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.AUDIENCE_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DATA_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DATA_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.DURATION;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.PASSPHRASE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_DATA_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.POINT_TOUR_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.QUIZ_URL;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.RANK;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.SESSION_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.SESSION_TABLE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.START_DATE;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_ID;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;

/**
 * The main activity that displays all available points for a given tour.
 */
public class FeedActivity extends AppCompatActivity implements HiTourRetrofit.CallbackRetrofit {

    /**
     * Int value for result of requesting camera permission
     */
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    /**
     * Static String to name to store in a bundle the currently selected tour's id
     */
    public static final String CURRENT_TOUR_ID = "CURRENT_TOUR_ID";

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

    /**
     * The TOUR_ID of the currently selected tour
     */
    public static String currentTourId;

    /**
     * Integer stating number of items in download list
     */
    private int downloadItemCount;

    /**
     * Integer stating current position through download queue
     */
    private int downloadPosition;

    /**
     * Stores a reference to the {@link ProgressDialog} for showing data is being downloaded
     */
    private ProgressDialog progressDialog;

    /**
     * Stores a reference to the {@link Menu} for the {@link DrawerLayout}
     */
    private Menu mMenu;

    private static HiTourRetrofit hiTourRetrofit;

    private static FeedAdapter currentFeedAdapter;

    /**
     * Initializes the UI and sets an adapter for the {@link FeedActivity#mFeed}
     *
     * @param savedInstanceState {@link Bundle} with all the saved state variables.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(CURRENT_TOUR_ID)) {
                currentTourId = savedInstanceState.getString(CURRENT_TOUR_ID);
            }
        }

        database = new DBWrap(this, new HiSchema(1));
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleFont();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        checkSessionDates();

        mFeed = (RecyclerView) findViewById(R.id.rv_feed);

        mFeed.setHasFixedSize(true);

        // Display list items depending on the device orientation.
        // Hide the Up button on tablets.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getResources().getBoolean(R.bool.isTablet)) {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            }
        } else {
            if (getResources().getBoolean(R.bool.isTablet)) {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            } else {
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            }
        }

        mFeed.setLayoutManager(mLayoutManager);

        try {
            final Cursor sessionCursor = database.getAll(SESSION_TABLE);
            if (currentTourId != null) {
                populateFeedAdapter(currentTourId);
                for(int i = 0; i < sessionCursor.getCount(); i++) {
                    sessionCursor.moveToPosition(i);
                    if(sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)).equals(currentTourId)) {
                        mDrawerLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                updateHeader(sessionCursor, 0);
                            }
                        });
                        break;
                    }
                }
            } else {
                if (sessionCursor.getCount() > 0) {
                    sessionCursor.moveToFirst();
                    populateFeedAdapter(sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)));
                    mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            updateHeader(sessionCursor, 0);
                        }
                    });
                }

            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        mMenu = navigationView.getMenu();
        updateMenu();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {

                        // TODO: Refactor this block of code

                        // If the "about" section is clicked, the DialogFragment shows up
                        if (item.getItemId() == R.id.app_info_item) {
                            FragmentManager fm = getSupportFragmentManager();
                            AppInfoFragment appInfoFragment = new AppInfoFragment();
                            appInfoFragment.show(fm, "app_info_fragment");
                        } else {
                            try {
                                Cursor sessionCursor = database.getAll(SESSION_TABLE);
                                if(sessionCursor.getCount() > 0) {
                                    sessionCursor.moveToPosition(item.getItemId());
                                    if (!sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)).equals(currentTourId)) {
                                        CardView cardView = (CardView) findViewById(R.id.point_detail_container);
                                        if(cardView != null) {
                                            cardView.removeAllViews();
                                        }
                                        populateFeedAdapter(sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)));
                                        updateHeader(sessionCursor, item.getItemId());
                                    }
                                }
                            } catch (NotInSchemaException e) {
                                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                            }
                        }
                        mDrawerLayout.closeDrawers();

                        return true;
                    }
                }
        );


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setContentDescription(fab.getResources().getString(R.string.content_description_launch_scanner));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Request camera permissions on Android 6.0 +
                if(ContextCompat.checkSelfPermission(FeedActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FeedActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    scanCode();
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the currently selected tour's ID
        savedInstanceState.putString(CURRENT_TOUR_ID, currentTourId);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Method called when {@link ScanningActivity} returns a result
     * @param requestCode which request we're responding to
     * @param resultCode whether a successful result returned
     * @param data the information of the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(data.getExtras().getString("mode").equals("point")) {
                // Instructions for when a point is entered
                if (!(getResources().getBoolean(R.bool.isTablet))) {
                    Intent intent = new Intent(this, DetailActivity.class)
                            .putExtra(DetailActivity.EXTRA_PIN, data.getExtras().getString(DetailActivity.EXTRA_PIN));
                    startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(DetailFragment.ARG_ITEM_ID, data.getExtras().getString(DetailActivity.EXTRA_PIN));

                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.point_detail_container, fragment, DetailFragment.FRAGMENT_TAG)
                            .commitAllowingStateLoss();
                }
            } else {
                // Instructions for when a tour is entered
                if(Utilities.isNetworkAvailable(this)) {
                    // Set ProgressDialog so user knows data is being downloaded
                    setRequestedOrientation(getResources().getConfiguration().orientation);
                    progressDialog = new ProgressDialog(this);
                    progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    progressDialog.setMessage("Downloading data");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    hiTourRetrofit = new HiTourRetrofit(this, data.getExtras().getString("pin"));
                    hiTourRetrofit.fetchTour();
                } else {
                    Snackbar.make(mFeed, getString(R.string.no_network), Snackbar.LENGTH_LONG).show();
                }
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

    /**
     * Called when request for permissions returns a result
     * @param requestCode the request code that was passed in
     * @param permissions list of requested permissions
     * @param grantResults result of permissions that were requested
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            // Launch the ScanningActivity
            scanCode();
        }
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
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Invoked when the data has been successfully fetched from the web API.
     */
    public void onAllRequestsFinished() {

        ArrayList<String> urlArrayList = new ArrayList<>();

        // Add the tour session to the local database
        TourSession tourSession = hiTourRetrofit.getTourSession();
        Map<String,String> tourSessionColumnsMap = new HashMap<>();
        tourSessionColumnsMap.put("TOUR_ID", tourSession.getTourId().toString());
        tourSessionColumnsMap.put("START_DATE", tourSession.getStartDate());
        tourSessionColumnsMap.put("DURATION", tourSession.getDuration().toString());
        tourSessionColumnsMap.put("PASSPHRASE", tourSession.getPassphrase());
        tourSessionColumnsMap.put(NAME, tourSession.getName());
        Map<String,String> tourSessionPrimaryKeysMap = new HashMap<>();
        tourSessionPrimaryKeysMap.put("SESSION_ID", tourSession.getId().toString());
        try {
            database.insert(tourSessionColumnsMap, tourSessionPrimaryKeysMap, "SESSION");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        Tour tour = hiTourRetrofit.getTour();

        // Add the tour to the local database
        Map<String,String> tourColumnsMap = new HashMap<>();
        tourColumnsMap.put("NAME", tour.getName());
        tourColumnsMap.put("AUDIENCE_ID", tour.getAudienceId().toString());
        tourColumnsMap.put(QUIZ_URL, tour.getQuizUrl());
        Map<String, String> tourPrimaryKeysMap = new HashMap<>();
        tourPrimaryKeysMap.put("TOUR_ID", tour.getId().toString());
        try {
            database.insert(tourColumnsMap, tourPrimaryKeysMap, "TOUR");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Add points to the local database
        List<Point> points = tour.getPoints();
        for(Point point : points) {
            Map<String,String> pointColumnMap = new HashMap<>();
            pointColumnMap.put("NAME", point.getName());
            pointColumnMap.put("URL", point.getUrl());
            pointColumnMap.put("DESCRIPTION", point.getDescription());
            Map<String,String> pointPrimaryKeysMap = new HashMap<>();
            pointPrimaryKeysMap.put("POINT_ID", point.getId().toString());
            try {
                database.insert(pointColumnMap, pointPrimaryKeysMap, "POINT");
            } catch(NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
            // Add url of the header image to list to be downloaded to local storage
            urlArrayList.add(point.getUrl());
            // Add data to the local database
            List<Data> data = point.getData();
            for (int i = 0; i < data.size(); i ++) {
                Data datum = data.get(i);
                Map<String, String> datumColumnsMap = new HashMap<>();
                datumColumnsMap.put("URL", datum.getUrl());
                datumColumnsMap.put("DESCRIPTION", datum.getDescription());
                datumColumnsMap.put("TITLE", datum.getTitle());
                Map<String, String> datumPrimaryKeysMap = new HashMap<>();
                datumPrimaryKeysMap.put("DATA_ID", datum.getId().toString());
                try {
                    database.insert(datumColumnsMap, datumPrimaryKeysMap, "DATA");
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
                // Add url of the physical data to list to be downloaded to local storage
                urlArrayList.add(datum.getUrl());
                // Add point data to the local database
                Map<String, String> pointDatumColumnsMap = new HashMap<>();
                pointDatumColumnsMap.put("RANK", datum.getRank().toString());
                Map<String, String> pointDataPrimaryKeysMap = new HashMap<>();
                pointDataPrimaryKeysMap.put("POINT_ID", point.getId().toString());
                pointDataPrimaryKeysMap.put("DATA_ID", datum.getId().toString());
                try {
                    database.insert(pointDatumColumnsMap, pointDataPrimaryKeysMap, "POINT_DATA");
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
                // Add data audience to the local database
                Map<String, String> dataAudienceColumnsMap = new HashMap<>();
                Map<String, String> dataAudiencePrimaryKeysMap = new HashMap<>();
                dataAudiencePrimaryKeysMap.put("DATA_ID", datum.getId().toString());
                dataAudiencePrimaryKeysMap.put("AUDIENCE_ID", tour.getAudienceId().toString());
                try {
                    database.insert(dataAudienceColumnsMap, dataAudiencePrimaryKeysMap, "AUDIENCE_DATA");
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
            }
            // Add tour points to the local database
            Map<String, String> tourPointColumnsMap = new HashMap<>();
            tourPointColumnsMap.put("RANK", point.getRank().toString());
            tourPointColumnsMap.put("UNLOCK","0");
            Map<String, String> tourPointPrimaryKeysMap = new HashMap<>();
            tourPointPrimaryKeysMap.put("TOUR_ID", tour.getId().toString());
            tourPointPrimaryKeysMap.put("POINT_ID", point.getId().toString());
            try {
                database.insert(tourPointColumnsMap, tourPointPrimaryKeysMap, "POINT_TOUR");
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
        }

        // Add audience to the local database
        Map<String, String> audienceColumnsMap = new HashMap<>();
        Map<String, String> audiencePrimaryKeysMap = new HashMap<>();
        audiencePrimaryKeysMap.put("AUDIENCE_ID", tour.getAudienceId().toString());
        try {
            database.insert(audienceColumnsMap, audiencePrimaryKeysMap, "AUDIENCE");
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Remove any URLs of data that should not be downloaded again
        ArrayList<String> toRemove = new ArrayList<>();
        for(String url : urlArrayList) {
            String filename = Utilities.createFilename(url);
            String localPath = this.getFilesDir().toString();
            File tempFile = new File(localPath + "/" + filename);
            if (tempFile.exists()) {
                toRemove.add(url);
            }
        }
        urlArrayList.removeAll(toRemove);

        // Download all data that us not already on the device
        downloadItemCount = urlArrayList.size();
        progressDialog.setMessage("Downloading data: 0 of " + downloadItemCount + " files downloaded");
        for(String url : urlArrayList) {
            try {
                DownloadToStorage downloadToStorage = new DownloadToStorage(url);
                downloadToStorage.run();
            } catch (Exception e) {
                Log.e("STORAGE_FAIL", Log.getStackTraceString(e));
                onDownloadFinish();
            }
        }

        updateMenu();

        currentTourId = tourSession.getTourId().toString();
        try {
            Cursor sessionCursor = database.getAll(SESSION_TABLE);
            for(int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                if(sessionCursor.getString(sessionCursor.getColumnIndex(DatabaseConstants.TOUR_ID)).equals(currentTourId)) {
                    mMenu.getItem(i).setChecked(true);
                    updateHeader(sessionCursor, i);
                    break;
                }
            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    /**
     * Changes a font of the app title.
     */
    public void setTitleFont() {
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/ubuntu_l.ttf");

        // Set font for title in action bar on a phone.
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null && !getResources().getBoolean(R.bool.isTablet)) {
            // Set the title of the action bar as the title with the custom font on a phone.
            SpannableString s = new SpannableString(getString(R.string.app_name));
            s.setSpan(new CustomTypefaceSpan("", font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            supportActionBar.setTitle(s);
        } else {
            // Set a typeface for the app title on a tablet.
            TextView tvTitle = (TextView) findViewById(R.id.tv_app_title);
            if(tvTitle != null) { tvTitle.setTypeface(font); }
        }
    }

    /**
     * Invoked to fill drawer with list of tour sessions saved on the device's database
     */
    private void updateMenu() {
        mMenu.clear();
        try {
            Cursor sessionCursor = database.getAll(SESSION_TABLE);
            for(int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                Map<String, String> primaryKeysMap = new HashMap<>();
                primaryKeysMap.put(TOUR_ID, sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)));
                Cursor tourCursor = database.getWholeByPrimary(TOUR_TABLE, primaryKeysMap);
                tourCursor.moveToFirst();
                mMenu.add(0, i, Menu.NONE, tourCursor.getString(tourCursor.getColumnIndex(NAME))).setIcon(R.drawable.ic_action_local_hospital);
                // TODO: Fix content description
//                mMenu.getItem(i).getActionView().setContentDescription(getString(R.string.content_description_tour_selection, mMenu.getItem(i).getTitle()));
            }
            mMenu.setGroupCheckable(0, true, true);
            if(mMenu.size() > 0) {
                mMenu.getItem(0).setChecked(true);
            }
        } catch(NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        mMenu.addSubMenu("s");
        mMenu.add(R.id.end_padder, R.id.app_info_item, Menu.NONE, getString(R.string.about)).setIcon(R.drawable.ic_action_live_help);
//        mMenu.getItem(i).getActionView().setContentDescription(getString(R.string.content_description_tour_selection, mMenu.getItem(i).getTitle()));
    }

    /**
     * Update the header portion of the drawer layout for a different session
     * @param sessionCursor cursor of the whole SESSION table
     * @param position position in the cursor of the session we want
     */
    private void updateHeader(Cursor sessionCursor, int position) {
        sessionCursor.moveToPosition(position);

        TextView nameTextView = (TextView) findViewById(R.id.nav_tour_info);
        TextView startDateTextView = (TextView) findViewById(R.id.tour_date);
        TextView expirationDateTextView = (TextView) findViewById(R.id.expiration_date);

        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfFinish = new SimpleDateFormat("dd-MM-yyyy");

        String name = sessionCursor.getString(sessionCursor.getColumnIndex(NAME));
        String startDate = sessionCursor.getString(sessionCursor.getColumnIndex(START_DATE));
        String duration = sessionCursor.getString(sessionCursor.getColumnIndex(DURATION));

        nameTextView.setText(name);

        Calendar expirationDateCalendar = getFinishDate(startDate, duration);
        String expirationDate = sdfFinish.format(expirationDateCalendar.getTime());
        expirationDateTextView.setText(Html.fromHtml("<b>" + FeedActivity.this.getString(R.string.expiration_date) + "</b><br/>" + expirationDate));

        try {
            startDate = sdfFinish.format(sdfStart.parse(startDate));
        } catch (ParseException e) {
            Log.e("PARSE_FAIL", Log.getStackTraceString(e));
        }
        startDateTextView.setText(Html.fromHtml("<b>" + FeedActivity.this.getString(R.string.start_date) + "</b><br/>" + startDate));
    }

    private void populateFeedAdapter(String tourId) {
        Map<String,String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", tourId);
        try {
            Cursor tourCursor = database.getWholeByPrimary(TOUR_TABLE,partialPrimaryMap);
            // Clear the fragment on change so point from previous tour does not show on tablet
            if(currentFeedAdapter != null)
                currentFeedAdapter.clearFragment();


            Cursor feedCursor = database.getWholeByPrimaryPartialSorted(POINT_TOUR_TABLE, partialPrimaryMap, RANK);
            tourCursor.moveToFirst();feedCursor.moveToFirst();
            FeedAdapter adapter = new FeedAdapter(feedCursor, this);
            adapter.setEmptyView(findViewById(R.id.empty_feed));
            adapter.setEmptyViewVisibility(View.VISIBLE);
            mFeed.setAdapter(adapter);
            adapter.setEmptyView(findViewById(R.id.empty_feed));
            adapter.setEmptyViewVisibility(View.GONE);
            currentTourId = tourId;
            setCurrentFeedAdapter(adapter);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private class DownloadToStorage {

        private final OkHttpClient client = new OkHttpClient();
        private String url;

        private DownloadToStorage(String url) {
            this.url = url;
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            client.setWriteTimeout(5, TimeUnit.SECONDS);
            client.setReadTimeout(5, TimeUnit.SECONDS);
        }

        public void run() throws Exception {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override public void onFailure(Request request, IOException throwable) {
                    throwable.printStackTrace();
                    onDownloadFinish();
                }

                @Override public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    InputStream inputStream = response.body().byteStream();
                    url = Utilities.createFilename(url);

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024 * 5);
                    FileOutputStream fileOutputStream = openFileOutput(url, Context.MODE_WORLD_READABLE);
                    byte[] buffer = new byte[5 * 1024];

                    int len;
                    while ((len = bufferedInputStream.read(buffer)) != -1)
                    {
                        fileOutputStream.write(buffer,0,len);
                    }

                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();

                    onDownloadFinish();
                }
            });
        }
    }

    private void onDownloadFinish() {
        downloadPosition++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Downloading data: " + downloadPosition + " of " + downloadItemCount + " files downloaded");
            }
        });
        if(downloadPosition == downloadItemCount) {
            progressDialog.dismiss();
            downloadItemCount = 0;
            downloadPosition = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CardView cardView = (CardView) findViewById(R.id.point_detail_container);
                    if(cardView != null) {
                        cardView.removeAllViews();
                    }
                    populateFeedAdapter(currentTourId);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                    progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }
    }

    private void checkSessionDates() {
        try {
            Cursor sessionCursor = database.getAll("SESSION");
            Map<String, String> sessionIdPassphraseMap = new HashMap<>();
            for(int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                String passphrase = sessionCursor.getString(sessionCursor.getColumnIndex(PASSPHRASE));
                String sessionId = sessionCursor.getString(sessionCursor.getColumnIndex(SESSION_ID));
                sessionIdPassphraseMap.put(passphrase, sessionId);
            }
            if(Utilities.isNetworkAvailable(this)) {
                SessionValidationOnline sessionValidationOnline = new SessionValidationOnline();
                sessionValidationOnline.execute(sessionIdPassphraseMap);
            } else {
                SessionValidationOffline sessionValidationOffline = new SessionValidationOffline();
                sessionValidationOffline.execute(sessionIdPassphraseMap);
            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private class SessionValidationOnline extends AsyncTask<Map<String, String>, Double, ArrayList<String>> {
        protected ArrayList<String> doInBackground(Map<String, String>... params) {
            Map<String, String> sessionIdPassphraseMap = params[0];
            ArrayList<String> sessionIdArrayList = new ArrayList<>();
            for(Map.Entry<String, String> entry : sessionIdPassphraseMap.entrySet()) {
                if(!sessionExistsOnline(entry.getKey())) {
                    sessionIdArrayList.add(entry.getValue());
                }
            }
            return sessionIdArrayList;
        }
        protected void onPostExecute(ArrayList<String> result) {
            for (int i = 0; i < result.size(); i++) {
                removeSession(result.get(i));
            }
        }
    }

    public static boolean sessionExistsOnline(String passphrase) {
        try {
            InputStream inputStream = new URL("https://hitour.herokuapp.com/api/A7DE6825FD96CCC79E63C89B55F88/" + passphrase).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
            String result = text.toString();
            //Only return true if it starts with a curly brace indicating JSON
            if(result.startsWith("{"))
                return true;
        }
        catch (IOException e) {
            Log.e("IO_FAIL", Log.getStackTraceString(e));
        }
        return false;
    }

    private void setCurrentFeedAdapter(FeedAdapter adapter){
        currentFeedAdapter = adapter;
    }

    /**
     * Method to get current feed adapter
     * @return FeedAdapter
     */
    public static FeedAdapter getCurrentFeedAdapter(){
        return currentFeedAdapter;
    }

    private class SessionValidationOffline extends AsyncTask<Map<String, String>, Double, ArrayList<String>> {
        protected ArrayList<String> doInBackground(Map<String, String>... params) {
            try {
                Map<String, String> sessionIdPassphraseMap = params[0];
                ArrayList<String> sessionIdArrayList = new ArrayList<>();
                for (Map.Entry<String, String> entry : sessionIdPassphraseMap.entrySet()) {
                    Map<String, String> primaryKeysMap = new HashMap<>();
                    primaryKeysMap.put(SESSION_ID, entry.getValue());
                    // Get START_DATE and DURATION for this session
                    Cursor sessionCursor = database.getWholeByPrimary(SESSION_TABLE, primaryKeysMap);
                    sessionCursor.moveToFirst();
                    String startDate = sessionCursor.getString(sessionCursor.getColumnIndex(START_DATE));
                    String duration = sessionCursor.getString(sessionCursor.getColumnIndex(DURATION));
                    if (!sessionExistsOffline(startDate, duration)) {
                        sessionIdArrayList.add(entry.getValue());
                    }
                }
                return sessionIdArrayList;
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
            return null;
        }
        protected void onPostExecute(ArrayList<String> result) {
            if(result != null) {
                for (int i = 0; i < result.size(); i++) {
                    removeSession(result.get(i));
                }
            }
        }
    }

    private boolean sessionExistsOffline(String startDate, String duration) {
        Calendar calendarFinish = getFinishDate(startDate, duration);
        Calendar calendarNow = Calendar.getInstance();
        if (calendarNow.after(calendarFinish)) {
            return false;
        }
        return true;
    }

    private Calendar getFinishDate(String startDate, String duration) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendarFinish = Calendar.getInstance();
            calendarFinish.setTime(sdf.parse(startDate));
            calendarFinish.add(Calendar.DATE, Integer.parseInt(duration));
            return calendarFinish;
        } catch (ParseException e) {
            Log.e("PARSE_FAIL", Log.getStackTraceString(e));
        }
        return null;
    }

    private void removeSession(String sessionId) {
        try {
            // Remove session from session table making note of the TOUR_ID
            Map<String,String> columnsMapSession = new HashMap<>();
            Map<String,String> primaryKeysMapSession = new HashMap<>();
            primaryKeysMapSession.put(SESSION_ID, sessionId);
            Cursor exactSessionCursor = database.getWholeByPrimary(SESSION_TABLE, primaryKeysMapSession);
            exactSessionCursor.moveToFirst();
            String completedTourId = exactSessionCursor.getString(exactSessionCursor.getColumnIndex(DatabaseConstants.TOUR_ID));
            database.delete(columnsMapSession, primaryKeysMapSession, SESSION_TABLE);

            // Get the updated session table
            Cursor sessionCursor = database.getAll("SESSION");

            // If no other sessions now exist remove all entries from all tables
            if (sessionCursor.getCount() == 0) {
                database.deleteAll("TOUR");
                database.deleteAll("POINT_TOUR");
                Cursor pointCursor = database.getAll(POINT_TABLE);
                for(int i = 0; i < pointCursor.getCount(); i++) {
                    pointCursor.moveToPosition(i);
                    String url = pointCursor.getString(pointCursor.getColumnIndex(DatabaseConstants.URL));
                    deleteDataFile(url);
                }
                database.deleteAll("POINT");
                database.deleteAll("POINT_DATA");
                Cursor dataCursor = database.getAll(DATA_TABLE);
                for(int i = 0; i < dataCursor.getCount(); i++) {
                    dataCursor.moveToPosition(i);
                    String url = dataCursor.getString(dataCursor.getColumnIndex(DatabaseConstants.URL));
                    deleteDataFile(url);
                }
                database.deleteAll("DATA");
                database.deleteAll("AUDIENCE_DATA");
                database.deleteAll("AUDIENCE");
                // No other checks needed, so exit the method
                return;
            }

            // Check if other sessions use the same tour
            for (int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                if (sessionCursor.getString(sessionCursor.getColumnIndex(DatabaseConstants.TOUR_ID)).equals(completedTourId)) {
                    // Nothing else should be deleted as tours use same data, so exit the method
                    return;
                }
            }

            // Remove tour from tour table
            Map<String,String> columnsMapTour = new HashMap<>();
            Map<String,String> primaryKeysMapTour = new HashMap<>();
            primaryKeysMapTour.put(TOUR_ID, completedTourId);
            Cursor exactTourCursor = database.getWholeByPrimary(TOUR_TABLE, primaryKeysMapTour);
            exactTourCursor.moveToFirst();
            String completedAudienceId = exactTourCursor.getString(exactTourCursor.getColumnIndex(AUDIENCE_ID));
            database.delete(columnsMapTour, primaryKeysMapTour, TOUR_TABLE);

            // Check to see if other tours use same audience, if not remove the audience from the AUDIENCE table
            Cursor updatedTourCursor = database.getAll("TOUR");
            boolean audienceStillNeeded = false;
            for(int i = 0; i < updatedTourCursor.getCount(); i++) {
                updatedTourCursor.moveToPosition(i);
                String audienceId = updatedTourCursor.getString(updatedTourCursor.getColumnIndex(AUDIENCE_ID));
                if(audienceId.equals(completedAudienceId)) {
                    audienceStillNeeded = true;
                    break;
                }
            }
            if(!audienceStillNeeded) {
                Map<String, String> columnsMapAudience = new HashMap<>();
                Map<String, String> primaryKeysMapAudience = new HashMap<>();
                primaryKeysMapAudience.put("AUDIENCE_ID", completedAudienceId);
                database.delete(columnsMapAudience, primaryKeysMapAudience, "AUDIENCE");
                database.delete(columnsMapAudience, primaryKeysMapAudience, "AUDIENCE_DATA");
            }

            // Get list of POINT_IDs that tour used and delete rows for tour from POINT_TOUR table
            ArrayList<String> forRemovingPointIdArrayList = new ArrayList<>();
            Map<String, String> columnsMapPointTour = new HashMap<>();
            Map<String, String> primaryKeysMapPointTour = new HashMap<>();
            primaryKeysMapPointTour.put("TOUR_ID", completedTourId);
            Cursor completedPointTourCursor = database.getWholeByPrimaryPartial("POINT_TOUR", primaryKeysMapPointTour);
            for(int i = 0; i < completedPointTourCursor.getCount(); i++) {
                completedPointTourCursor.moveToPosition(i);
                String tempPointId = completedPointTourCursor.getString(completedPointTourCursor.getColumnIndex(POINT_ID));
                forRemovingPointIdArrayList.add(tempPointId);
            }
            database.delete(columnsMapPointTour, primaryKeysMapPointTour, "POINT_TOUR");

            // Go through POINT_TOUR table to find which, if any, other tours use same points
            ArrayList<String> stillNeededPointIdArrayList = new ArrayList<>();
            for(String pointId : forRemovingPointIdArrayList) {
                // Get Cursor that shows other tours that use this POINT_ID
                Map<String, String> primaryKeysMapUpdatedPointTour = new HashMap<>();
                primaryKeysMapUpdatedPointTour.put("POINT_ID", pointId);
                Cursor updatedPointTourCursor = database.getWholeByPrimaryPartial("POINT_TOUR", primaryKeysMapUpdatedPointTour);
                if(updatedPointTourCursor.getCount() > 0) {
                    // Add to list of points that cannot be deleted as used by other tours
                    stillNeededPointIdArrayList.add(pointId);
                }
            }
            // Remove POINT_IDs from list if they are still needed
            forRemovingPointIdArrayList.removeAll(stillNeededPointIdArrayList);

            // Remove all points that are no longer needed from POINT and POINT_DATA tables
            ArrayList<String> dataIdArrayList = new ArrayList<>();
            // Also make note of DATA_IDs used by these no longer needed points
            for(String pointId : forRemovingPointIdArrayList) {
                Map<String, String> columnsMapRemoving = new HashMap<>();
                Map<String, String> primaryKeysMapRemoving = new HashMap<>();
                primaryKeysMapRemoving.put("POINT_ID", pointId);
                database.delete(columnsMapRemoving, primaryKeysMapRemoving, "POINT");
                Cursor pointDataRemovingCursor = database.getWholeByPrimaryPartial("POINT_DATA", primaryKeysMapRemoving);
                for(int i = 0; i < pointDataRemovingCursor.getCount(); i++) {
                    pointDataRemovingCursor.moveToPosition(i);
                    String tempDataId = pointDataRemovingCursor.getString(pointDataRemovingCursor.getColumnIndex(DATA_ID));
                    if(!dataIdArrayList.contains(tempDataId))
                        dataIdArrayList.add(tempDataId);
                }
                database.delete(columnsMapRemoving, primaryKeysMapRemoving, "POINT_DATA");
            }

            // Make note of DATA_IDs of data from points that are still needed
            for(String pointId : stillNeededPointIdArrayList) {
                Map<String, String> primaryKeysMapKeeping = new HashMap<>();
                primaryKeysMapKeeping.put("POINT_ID", pointId);
                Cursor pointDataKeepingCursor = database.getWholeByPrimaryPartial("POINT_DATA", primaryKeysMapKeeping);
                for(int i = 0; i < pointDataKeepingCursor.getCount(); i++) {
                    pointDataKeepingCursor.moveToPosition(i);
                    String tempDataId = pointDataKeepingCursor.getString(pointDataKeepingCursor.getColumnIndex(DATA_ID));
                    if(!dataIdArrayList.contains(tempDataId))
                        dataIdArrayList.add(tempDataId);
                }
            }

            // Make note all points that use a piece of data that was used by the removed tour
            HashMap<String, ArrayList<String>> dataPointsMap = new HashMap<>();
            for(String dataId : dataIdArrayList) {
                Map<String, String> primaryKeysMapPointData = new HashMap<>();
                primaryKeysMapPointData.put("DATA_ID", dataId);
                Cursor tempPointDataCursor = database.getWholeByPrimaryPartial("POINT_DATA", primaryKeysMapPointData);
                ArrayList<String> tempPointIdArrayList = new ArrayList<>();
                for(int i = 0; i < tempPointDataCursor.getCount(); i++) {
                    tempPointDataCursor.moveToPosition(i);
                    String tempPointId = tempPointDataCursor.getString(tempPointDataCursor.getColumnIndex(POINT_ID));
                    tempPointIdArrayList.add(tempPointId);
                }
                dataPointsMap.put(dataId, tempPointIdArrayList);
            }

            // Make note of all tours that use each point
            Cursor pointCursor = database.getAll("POINT");
            Map<String, ArrayList<String>> pointToursMap = new HashMap<>();
            for(int i = 0; i < pointCursor.getCount(); i++) {
                pointCursor.moveToPosition(i);
                String pointId = pointCursor.getString(pointCursor.getColumnIndex(POINT_ID));
                ArrayList<String> tourIds = new ArrayList<>();

                Map<String, String> primaryKeysMapSpecificPoint = new HashMap<>();
                primaryKeysMapSpecificPoint.put("POINT_ID", pointId);
                Cursor pointTourCursor = database.getWholeByPrimaryPartial("POINT_TOUR", primaryKeysMapSpecificPoint);
                for(int j = 0; j < pointTourCursor.getCount(); j++) {
                    pointTourCursor.moveToPosition(j);
                    tourIds.add(pointTourCursor.getString(pointTourCursor.getColumnIndex(TOUR_ID)));
                }
                pointToursMap.put(pointId, tourIds);
            }

            // Make connection between TOUR_ID and the AUDIENCE_ID of that tour
            Cursor tourCursor = database.getAll("TOUR");
            Map<String, String> tourAudienceMap = new HashMap<>();
            for(int i = 0; i < tourCursor.getCount(); i++) {
                tourCursor.moveToPosition(i);
                String tourId = tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID));
                String audienceId = tourCursor.getString(tourCursor.getColumnIndex(AUDIENCE_ID));
                tourAudienceMap.put(tourId, audienceId);
            }

            // Make list of all audiences that a piece of data is available to
            Map<String, ArrayList<String>> dataAudiencesMap = new HashMap<>();
            for(int i = 0; i < dataIdArrayList.size(); i++) {
                String dataId = dataIdArrayList.get(i);
                Map<String, String> primaryKeysMapDataAudience = new HashMap<>();
                primaryKeysMapDataAudience.put("DATA_ID", dataId);
                ArrayList<String> audienceIdArrayList = new ArrayList<>();
                Cursor dataAudienceCursor = database.getWholeByPrimaryPartial("AUDIENCE_DATA", primaryKeysMapDataAudience);
                for(int j = 0; j < dataAudienceCursor.getCount(); j++) {
                    dataAudienceCursor.moveToPosition(j);
                    String audienceId = dataAudienceCursor.getString(dataAudienceCursor.getColumnIndex(AUDIENCE_ID));
                    audienceIdArrayList.add(audienceId);
                }
                dataAudiencesMap.put(dataId, audienceIdArrayList);
            }

            // Go through and remove any relevant entries from POINT_DATA, DATA, and DATA_AUDIENCE tables
            for(Map.Entry<String, ArrayList<String>> entry : dataPointsMap.entrySet()) {
                // If data not used by any points then remove it from POINT_DATA, DATA, and DATA_AUDIENCE tables
                ArrayList<String> points = entry.getValue();
                boolean dataUsed = false;
                for (int i = 0; i < points.size(); i++) {
                    String pointId = points.get(i);
                    ArrayList<String> tours = pointToursMap.get(pointId);
                    // The data can only be used by this point if a tour that uses it exists
                    if (tours.size() > 0) {
                        ArrayList<String> tourAudiences = new ArrayList<>();
                        for (int j = 0; j < tours.size(); j++) {
                            String tourId = tours.get(i);
                            tourAudiences.add(tourAudienceMap.get(tourId));
                        }
                        ArrayList<String> dataAudiences = dataAudiencesMap.get(entry.getKey());
                        dataAudiences.retainAll(tourAudiences);
                        // If they share at least one audience then the data is used
                        if (dataAudiences.size() > 0) {
                            dataUsed = true;
                            break;
                        }
                    }
                }
                // If no tour exists that uses the data then remove it
                if (!dataUsed) {
                    removeData(entry.getKey());
                }
            }

        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private void removeData(String dataId) throws NotInSchemaException {
        HashMap<String, String> columnsMap = new HashMap<>();
        HashMap<String, String> primaryKeysMap = new HashMap<>();
        primaryKeysMap.put(DATA_ID, dataId);
        Cursor dataCursor = database.getWholeByPrimary(DATA_TABLE, primaryKeysMap);
        dataCursor.moveToFirst();
        String url = dataCursor.getString(dataCursor.getColumnIndex(DatabaseConstants.URL));
        if(!usedByPoint(url)) {
            deleteDataFile(url);
        }
        database.delete(columnsMap, primaryKeysMap, DATA_TABLE);
        database.delete(columnsMap, primaryKeysMap, AUDIENCE_DATA_TABLE);
        database.delete(columnsMap, primaryKeysMap, POINT_DATA_TABLE);
    }

    private void deleteDataFile(String url) {
        String filename = Utilities.createFilename(url);
        filename = this.getFilesDir().toString() + "/" + filename;
        File file = new File(filename);
        file.delete();
    }

    private boolean usedByPoint(String dataUrl) throws NotInSchemaException {
        Cursor pointCursor = database.getAll(POINT_TABLE);
        for(int i = 0; i < pointCursor.getCount(); i++) {
            pointCursor.moveToPosition(i);
            String pointUrl = pointCursor.getString(pointCursor.getColumnIndex(DatabaseConstants.URL));
            if(dataUrl.equals(pointUrl))
                return true;
        }
        return false;
    }

}
