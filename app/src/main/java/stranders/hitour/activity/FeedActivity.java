package stranders.hitour.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import stranders.hitour.FeedAdapter;
import stranders.hitour.R;
import stranders.hitour.database.DBWrap;
import stranders.hitour.database.NotInSchemaException;
import stranders.hitour.database.schema.HiSchema;
import stranders.hitour.fragment.AppInfoFragment;
import stranders.hitour.fragment.DetailFragment;
import stranders.hitour.model.Tour;
import stranders.hitour.model.TourSession;
import stranders.hitour.retrofit.HiTourRetrofit;
import stranders.hitour.utilities.CustomTypefaceSpan;
import stranders.hitour.utilities.DataManipulation;
import stranders.hitour.utilities.SessionValidationOffline;
import stranders.hitour.utilities.SessionValidationOnline;
import stranders.hitour.utilities.Utilities;

import static stranders.hitour.database.schema.DatabaseConstants.DURATION;
import static stranders.hitour.database.schema.DatabaseConstants.NAME;
import static stranders.hitour.database.schema.DatabaseConstants.PASSPHRASE;
import static stranders.hitour.database.schema.DatabaseConstants.POINT_TOUR_TABLE;
import static stranders.hitour.database.schema.DatabaseConstants.RANK;
import static stranders.hitour.database.schema.DatabaseConstants.SESSION_ID;
import static stranders.hitour.database.schema.DatabaseConstants.SESSION_TABLE;
import static stranders.hitour.database.schema.DatabaseConstants.START_DATE;
import static stranders.hitour.database.schema.DatabaseConstants.TOUR_ID;
import static stranders.hitour.database.schema.DatabaseConstants.TOUR_TABLE;

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

    /**
     * Stores a reference to the {@link HiTourRetrofit} object in which the tour info is downloaded
     */
    private static HiTourRetrofit hiTourRetrofit;

    /**
     * Stores a reference to the {@link FeedAdapter} that is currently being utilised by the app
     */
    private static FeedAdapter currentFeedAdapter;

    /**
     * Initializes the UI and sets an adapter for the {@link FeedActivity#mFeed}
     *
     * @param savedInstanceState {@link Bundle} with all the saved state variables.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the currentTourId if there was a previous tour before orientation changes
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(CURRENT_TOUR_ID)) {
                currentTourId = savedInstanceState.getString(CURRENT_TOUR_ID);
            }
        }

        // Set up the database
        database = new DBWrap(this, new HiSchema(1));

        //Set up the main layout
        setContentView(R.layout.activity_feed);

        // Find the toolbar and implement its behaviour and look correctly
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleFont();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Check that all sessions on device are still valid
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

        // Correctly set up the feed with the current tour
        // or if not tour currently, the one from the top of the app drawer list
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

        // Set the behaviour of the items in the navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
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

        // Set the behaviour of the floating action button to open the ScanningActivity
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

    /**
     * Add the current tour's id to the bundle when orientation changes
     * @param savedInstanceState the bundle which will be saved
     */
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

    /**
     * Add items to the action bar if it visible
     * @param menu the menu to add
     * @return if a menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    /**
     * Sets the hamburger icon to open the app drawer
     * @param item item in menu that was selected
     * @return if an option was selected
     */
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

        try {
            TourSession tourSession = hiTourRetrofit.getTourSession();
            Tour tour = hiTourRetrofit.getTour();
            ArrayList<String> urlArrayList = DataManipulation.addSession(tourSession, tour, this, database);

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
                    if(sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)).equals(currentTourId)) {
                        mMenu.getItem(i).setChecked(true);
                        updateHeader(sessionCursor, i);
                        break;
                    }
                }
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
            
        } catch (Exception e) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            progressDialog.dismiss();
            Snackbar.make(mFeed, "Tour could not be downloaded, please try again.", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Changes a font of the app title.
     */
    private void setTitleFont() {
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

        SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdfFinish = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        String name = sessionCursor.getString(sessionCursor.getColumnIndex(NAME));
        String startDate = sessionCursor.getString(sessionCursor.getColumnIndex(START_DATE));
        String duration = sessionCursor.getString(sessionCursor.getColumnIndex(DURATION));

        nameTextView.setText(name);

        Calendar expirationDateCalendar = Utilities.getFinishDate(startDate, duration);
        String expirationDate = sdfFinish.format(expirationDateCalendar.getTime());
        expirationDateTextView.setText(Html.fromHtml("<b>" + FeedActivity.this.getString(R.string.expiration_date) + "</b><br/>" + expirationDate));

        try {
            startDate = sdfFinish.format(sdfStart.parse(startDate));
        } catch (ParseException e) {
            Log.e("PARSE_FAIL", Log.getStackTraceString(e));
        }
        startDateTextView.setText(Html.fromHtml("<b>" + FeedActivity.this.getString(R.string.start_date) + "</b><br/>" + startDate));
    }

    /**
     * Provide all necessary updates to update the feed adapter for the specified tour
     */
    private void populateFeedAdapter(String tourId) {
        Map<String,String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", tourId);
        try {
            Cursor tourCursor = database.getWholeByPrimary(TOUR_TABLE, partialPrimaryMap);
            // Make sure point from previous tour does not show on tablet
            CardView cardView = (CardView) findViewById(R.id.point_detail_container);
            if(cardView != null) {
                cardView.removeAllViews();
            }

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

    /**
     * Class that implements OkHttps to allow download of all types of data
     */
    private class DownloadToStorage {

        private final OkHttpClient client = new OkHttpClient();
        private String url;

        /**
         * Creates a DownloadToStorage object and sets the url of data to be downloaded
         * @param url url of website to download from
         */
        private DownloadToStorage(String url) {
            this.url = url;
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            client.setWriteTimeout(5, TimeUnit.SECONDS);
            client.setReadTimeout(5, TimeUnit.SECONDS);
        }

        /**
         * Attempts to download data from url and store in internal storage of device
         */
        public void run() {
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

    /**
     * Updates that should happen when a piece of data finishes downloading
     */
    private void onDownloadFinish() {
        // Further position in download queue
        downloadPosition++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Downloading data: " + downloadPosition + " of " + downloadItemCount + " files downloaded");
            }
        });
        // If end of download queue reached set the feed adapter and remove the ProgressDialog
        if(downloadPosition == downloadItemCount) {
            progressDialog.dismiss();
            downloadItemCount = 0;
            downloadPosition = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CardView cardView = (CardView) findViewById(R.id.point_detail_container);
                    if (cardView != null) {
                        cardView.removeAllViews();
                    }
                    populateFeedAdapter(currentTourId);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                    progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }
    }

    /**
     * Check the dates of all sessions in database to make sure that they are still valid
     */
    private void checkSessionDates() {
        try {
            Cursor sessionCursor = database.getAll(SESSION_TABLE);
            Map<String, String> sessionIdPassphraseMap = new HashMap<>();
            for(int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                String passphrase = sessionCursor.getString(sessionCursor.getColumnIndex(PASSPHRASE));
                String sessionId = sessionCursor.getString(sessionCursor.getColumnIndex(SESSION_ID));
                sessionIdPassphraseMap.put(passphrase, sessionId);
            }
            if(Utilities.isNetworkAvailable(this)) {
                SessionValidationOnline sessionValidationOnline = new SessionValidationOnline(this);
                sessionValidationOnline.execute(sessionIdPassphraseMap);
            } else {
                SessionValidationOffline sessionValidationOffline = new SessionValidationOffline(this);
                sessionValidationOffline.execute(sessionIdPassphraseMap);
            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    /**
     * Set the current adapter of the feed
     * @param adapter the FeedAdapter
     */
    private void setCurrentFeedAdapter(FeedAdapter adapter){
        currentFeedAdapter = adapter;
    }

    /**
     * Method used by testing to get the database
     * @return database
     */
    public DBWrap getDatabase() {
        return database;
    }

    /**
     * Method to get current feed adapter
     * @return FeedAdapter
     */
    public static FeedAdapter getCurrentFeedAdapter(){
        return currentFeedAdapter;
    }

}
