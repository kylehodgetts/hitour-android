package uk.ac.kcl.stranders.hitour.activity;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.NAME;
import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.PASSPHRASE;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.FeedAdapter;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.Utilities;
import uk.ac.kcl.stranders.hitour.database.DBWrap;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.database.schema.HiSchema;
import uk.ac.kcl.stranders.hitour.fragment.DetailFragment;
import uk.ac.kcl.stranders.hitour.model.Data;
import uk.ac.kcl.stranders.hitour.model.Point;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.TourSession;
import uk.ac.kcl.stranders.hitour.retrofit.HiTourRetrofit;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.TOUR_ID;

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

    public static String currentTourId;

    private Menu mMenu;

    private static HiTourRetrofit hiTourRetrofit;

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

        checkSessionDates();

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

        try {
            Cursor tourCursor = database.getAll("TOUR");
            if(tourCursor.getCount() > 0) {
                tourCursor.moveToFirst();
                populateFeedAdapter(tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID)));
            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL",Log.getStackTraceString(e));
        }

        mMenu = navigationView.getMenu();
        updateMenu();

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        try {
                            Cursor tourCursor = database.getAll("TOUR");
                            if(tourCursor.getCount() > 0) {
                                tourCursor.moveToFirst();
                                tourCursor.move(item.getItemId());
                                if(!tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID)).equals(FeedActivity.this.currentTourId)) {
                                    populateFeedAdapter(tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID)));
                                }
                            }
                        } catch (NotInSchemaException e) {
                            Log.e("DATABASE_FAIL",Log.getStackTraceString(e));
                        }
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

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(data.getExtras().getString("mode").equals("point")) {
                // Instructions for when a point is entered
                if (!(getResources().getBoolean(R.bool.isTablet))) {
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
            } else {
                // Instructions for when a tour is entered
                if(Utilities.isNetworkAvailable(this)) {
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

     /**
     * Invoked when the data has been successfully fetched from the web API.
     */
    public void onAllRequestsFinished() {
        // Add the tour session to the local database
        TourSession tourSession = hiTourRetrofit.getTourSession();
        Map<String,String> tourSessionColumnsMap = new HashMap<>();
        tourSessionColumnsMap.put("TOUR_ID", tourSession.getTourId().toString());
        tourSessionColumnsMap.put("START_DATE", tourSession.getStartDate());
        tourSessionColumnsMap.put("DURATION", tourSession.getDuration().toString());
        tourSessionColumnsMap.put("PASSPHRASE", tourSession.getPassphrase());
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
            // Download the header image to local storage
            try {
                String filename = createFilename(point.getUrl());
                String localPath = this.getFilesDir().toString();
                File tempFile = new File(localPath + "/" + filename);
                if(!tempFile.exists()) {
                    DownloadToStorage downloadToStorage = new DownloadToStorage(point.getUrl());
                    downloadToStorage.run();
                }
            } catch(Exception e) {
                Log.e("STORAGE_FAIL", Log.getStackTraceString(e));
            }
            // Add data to the local database
            List<Data> data = point.getData();
            for (Data datum : data) {
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
                // Download the physical data to storage
                try {
                    String filename = createFilename(datum.getUrl());
                    String localPath = this.getFilesDir().toString();
                    File tempFile = new File(localPath + "/" + filename);
                    if (!tempFile.exists()) {
                        DownloadToStorage downloadToStorage = new DownloadToStorage(datum.getUrl());
                        downloadToStorage.run();
                    }
                } catch (Exception e) {
                    Log.e("STORAGE_FAIL", Log.getStackTraceString(e));
                }
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

        updateMenu();
        try {
            Cursor tourCursor = database.getAll("TOUR");
            tourCursor.moveToFirst();
            populateFeedAdapter(tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID)));
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    /**
     * Invoked to fill drawer with list of tours saved on the device's database.
     */
    private void updateMenu() {
        mMenu.clear();
        try {
            Cursor tourCursor = database.getAll("TOUR");
            tourCursor.moveToFirst();
            for(int i = 0; i < tourCursor.getCount(); i++) {
                tourCursor.moveToPosition(i);
                mMenu.add(0, i, Menu.NONE, tourCursor.getString(tourCursor.getColumnIndex(NAME))).setIcon(R.drawable.ic_action_local_hospital);
            }
            mMenu.setGroupCheckable(0, true, true);
            if(mMenu.size() > 0) {
                mMenu.getItem(0).setChecked(true);
            }
        } catch(NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private void populateFeedAdapter(String tourId) {
        Map<String,String> partialPrimaryMap = new HashMap<>();
        partialPrimaryMap.put("TOUR_ID", tourId);
        try {
            Cursor feedCursor = database.getWholeByPrimaryPartial("POINT_TOUR", partialPrimaryMap);
            FeedAdapter adapter = new FeedAdapter(feedCursor, this);
            mFeed.setAdapter(adapter);
            currentTourId = tourId;
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private class DownloadToStorage {

        private final OkHttpClient client = new OkHttpClient();
        private String url;

        private DownloadToStorage(String url) {
            this.url = url;
        }

        public void run() throws Exception {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override public void onFailure(Request request, IOException throwable) {
                    throwable.printStackTrace();
                }

                @Override public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    InputStream inputStream = response.body().byteStream();
                    url = createFilename(url);

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
                }
            });
        }

    }

    public static String createFilename(String url) {
        url = url.replace("/","");
        url = url.replace(":","");
        String filename = url.substring(0,url.lastIndexOf("."));
        String extension = url.substring(url.lastIndexOf("."));
        filename = filename.replace(".","");
        url = filename + extension;
        return url;
    }

    private void checkSessionDates() {
        try {
            Cursor sessionCursor = database.getAll("SESSION");
            String[] passphraseArray = new String[sessionCursor.getCount()];
            for(int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                String passphrase = sessionCursor.getString(sessionCursor.getColumnIndex(PASSPHRASE));
                passphraseArray[i] = passphrase;
            }
            if(Utilities.isNetworkAvailable(this)) {
                SessionValidation sessionValidation = new SessionValidation();
                sessionValidation.execute(passphraseArray);
            } else {
                //TODO: use date on phone to check if session is still valid
            }
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    private class SessionValidation extends AsyncTask<String[],Double,Boolean[]> {
        private String[] passphraseArray;
        protected Boolean[] doInBackground(String[]... params) {
            passphraseArray = params[0];
            Boolean[] exists = new Boolean[passphraseArray.length];
            for(int i = 0; i < passphraseArray.length; i++) {
                if(sessionExists(passphraseArray[i])) {
                    exists[i] = true;
                } else {
                    exists[i] = false;
                }
            }
            return exists;
        }
        protected void onPostExecute(Boolean[] result) {
            for (int i = 0; i < result.length; i++) {
                if (result[i] == false) {
                    removeSession(passphraseArray[i]);
                }
            }
        }
    }

    public static boolean sessionExists(String sessionCode) {
        try {
            InputStream inputStream = new URL("https://hitour.herokuapp.com/api/A7DE6825FD96CCC79E63C89B55F88/" + sessionCode).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
            }
            String result = text.toString();
            if(result.equals("Passprase Invalid"))
                return false;
        }
        catch (IOException e) {
            Log.e("IO_FAIL", Log.getStackTraceString(e));
        }
        return true;
    }

    private void removeSession(String passphrase) {
        //TODO: add algorithm to remove tour session without removing any shared elements

    }

}
