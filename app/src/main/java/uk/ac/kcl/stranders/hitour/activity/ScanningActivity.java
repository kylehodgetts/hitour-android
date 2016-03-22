package uk.ac.kcl.stranders.hitour.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import uk.ac.kcl.stranders.hitour.utilities.CustomTypefaceSpan;
import uk.ac.kcl.stranders.hitour.R;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.utilities.Utilities;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.*;

/**
 * {@link AppCompatActivity} class that is used to retrieve input by means of scanning a QR Code or
 * manually entering a pin or point reference.
 *
 * This Activity contains a Barcode Scanner as well as an {@link EditText} to receive input
 */
public class ScanningActivity extends AppCompatActivity {

    /**
     * Field to store a reference to the BarcodeScanner embedded on the Activity
     */
    private CompoundBarcodeView barcodeScannerView;

    /**
     * Field that stores a reference to the {@link EditText} field on the Activity
     */
    private EditText etCodePinEntry;

    private String entry;

    /**
     * Field to store a {@link BarcodeCallback} which handles what the barcode scanner should accept
     * and what to do when it has detected an accepting barcode type.
     *
     * Where it will update the {@link EditText} field with the data it captured and execute the submit method
     */
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null && result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
                barcodeScannerView.setStatusText(result.getText());
                etCodePinEntry.setText(result.getText());
                barcodeScannerView.pause();
                submit();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    /**
     * Creates an instance of the activity by telling barcode scanner to scan using the {@link BarcodeCallback}
     * above and adding an {@link android.view.View.OnClickListener} to the Submit {@link Button}
     *
     * @param savedInstanceState - Saved Instance Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        barcodeScannerView = (CompoundBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.decodeContinuous(callback);
        etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setContentDescription(btnSubmit.getResources().getString(R.string.content_description_submits));
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });


        ActionBar actionbar = getSupportActionBar();

        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/ubuntu_l.ttf");
        SpannableString s = new SpannableString("hiTour");
        s.setSpan(new CustomTypefaceSpan("", font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        if (actionbar != null) {
            actionbar.setTitle(s);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(barcodeScannerView, "Camera permission needed to scan points.", Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Method that checks if the data input to the {@link EditText} field matches a valid format
     * and that it matches a value stored for a point.
     * Then navigates the use to the point data scanned.
     *
     * Otherwise an error message is shown to the user and the input is cleared ready for the next input.
     */
    private void submit() {

        // Hide keyboard when submit is pressed so Snackbar can be seen
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        EditText etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);
        String result = etCodePinEntry.getText().toString();
        // Check if the point came from being scanned

        // Check if a point or a tour was submitted
        boolean isTour = false;
        if (result.length() > 6 && result.substring(0, 6).equals("POINT-")) {
            // Takes identification part of point id
            result = result.substring(6);
        } else if (result.length() > 2 && result.substring(0, 2).equals("SN")) {
            // Takes identification part of session passphrase
            result = result.substring(2);
            // Sets to identify as a tour
            isTour = true;
        }

        entry = result;

        // Check if user wants to add a session or a point
        if (isTour) {
            // For when the user attempts to add a session
            try {
                // Checks to see if tour session is already on device
                Cursor sessionCursor = FeedActivity.database.getAll(SESSION_TABLE);
                boolean alreadyExists = false;
                for (int i = 0; i < sessionCursor.getCount(); i++) {
                    sessionCursor.moveToPosition(i);
                    if (result.equals(sessionCursor.getString(sessionCursor.getColumnIndex(PASSPHRASE)))) {
                        alreadyExists = true;
                    }
                }
                if (alreadyExists) {
                    Log.d("FeedActivity", "Tour for " + entry + " already exists!");
                    Snackbar.make(barcodeScannerView, "Tour already downloaded on this device.", Snackbar.LENGTH_LONG).show();
                    barcodeScannerView.resume();
                    clearInput();
                } else {
                    // Attempts to download tour if the passphrase is valid
                    TourSubmit tourSubmit = new TourSubmit();
                    tourSubmit.execute(result);
                }
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
        } else {
            // Calls FeedActivity#onActivityResult if the point exists.
            // Displays a message otherwise.
            if (pointExistsInTour(result)) {
                Intent data = new Intent();
                data.putExtra("mode", "point");
                data.putExtra(DetailActivity.EXTRA_PIN, result);

                //Replace unlock with a value of 1
                Map<String, String> tourPointColumnsMap = new HashMap<>();
                tourPointColumnsMap.put(UNLOCK, UNLOCK_STATE_UNLOCKED);
                Map<String, String> tourPointPrimaryKeysMap = new HashMap<>();
                Log.i("inScanning", "" + FeedActivity.currentTourId);
                tourPointPrimaryKeysMap.put(TOUR_ID, "" + FeedActivity.currentTourId);
                tourPointPrimaryKeysMap.put(POINT_ID, result);
                try {
                    Cursor cursorGetRank = FeedActivity.database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, tourPointPrimaryKeysMap);
                    cursorGetRank.moveToFirst();
                    String pointRank = cursorGetRank.getString(cursorGetRank.getColumnIndex(RANK));
                    tourPointColumnsMap.put(RANK, pointRank);
                    FeedActivity.database.insert(tourPointColumnsMap, tourPointPrimaryKeysMap, POINT_TOUR_TABLE);
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
                //Notify the feedAdapter that a viewHolder's view has to update.
                ObservableLock observableLock = new ObservableLock();
                observableLock.addObserver(FeedActivity.getCurrentFeedAdapter());
                observableLock.setChange(result, FeedActivity.currentTourId);
                setResult(RESULT_OK, data);
                finish();
            } else {
                Log.d("FeedActivity", "Point for " + entry + " not found!");
                Snackbar.make(barcodeScannerView, "Point not found, please try again.", Snackbar.LENGTH_LONG).show();
                barcodeScannerView.resume();
                clearInput();
            }
        }
    }

    /***
     * Checks in a local database whether a point exists for a selected tour.
     *
     * @param passphrase id of a point
     * @return true if the point is valid for a selected tour
     */
    private boolean pointExistsInTour(String passphrase) {
        if (FeedActivity.currentTourId == null) {
            return false;
        }
        Map<String, String> partialPrimaryMapTour = new HashMap<>();
        partialPrimaryMapTour.put(TOUR_ID, FeedActivity.currentTourId);
        Cursor pointTourCursor;
        try {
            pointTourCursor = FeedActivity.database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, partialPrimaryMapTour);
            pointTourCursor.moveToPosition(0);
            do {
                String id = pointTourCursor.getString(pointTourCursor.getColumnIndex(POINT_ID));
                if (id.equals(passphrase)) {
                    return true;
                }
            } while (pointTourCursor.moveToNext());
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
        return false;
    }

        private class TourSubmit extends AsyncTask<String, Double, Boolean> {

            ProgressDialog progressDialog = new ProgressDialog(ScanningActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setRequestedOrientation(getResources().getConfiguration().orientation);
                progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                progressDialog.setMessage("Checking if tour exists");
                progressDialog.show();
                progressDialog.setCancelable(false);
            }

            protected Boolean doInBackground(String... params) {
                return Utilities.sessionExistsOnline(params[0]);
            }

            protected void onPostExecute(Boolean result) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                progressDialog.dismiss();
                if (result) {
                    Intent data = new Intent();
                    data.putExtra("mode", "tour");
                    data.putExtra("pin", entry);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Log.d("FeedActivity", "Tour for " + entry + " not found!");
                    Snackbar.make(barcodeScannerView, "Tour not found, please try again.", Snackbar.LENGTH_LONG).show();
                    barcodeScannerView.resume();
                    clearInput();
                }
            }
        }


    /**
     * Clears input received in the {@link EditText} field and the Barcode Scanner's status bar text
     */
    private void clearInput() {
        barcodeScannerView.setStatusText("");
        etCodePinEntry.setText("");
    }

    /**
     * Resumes the {@link AppCompatActivity} and resumes the Barcode Scanner
     */
    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    /**
     * Pauses the {@link AppCompatActivity} and pauses the barcode scanner and camera input
     */
    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    /**
     * Destroys the instance of the {@link AppCompatActivity}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Saves the state of the {@link AppCompatActivity}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Deals with when it is navigated up of the {@link AppCompatActivity}
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Deals with certain key presses when using the Barcode Scanner
     *
     * @param keyCode Keys pressed
     * @param event Event
     * @return boolean if keys are pressed
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Observable class that allows us to notify a change related to lock for the ViewAdapter
     */
    public class ObservableLock extends Observable {

        public void setChange(String point_id, String tour_id) {
            setChanged();
            notifyObservers(new Pair<>(Integer.valueOf(point_id), Integer.valueOf(tour_id)));
        }
    }

}
