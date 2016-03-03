package uk.ac.kcl.stranders.hitour.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

import uk.ac.kcl.stranders.hitour.R;

/**
 * {@link AppCompatActivity} class that is used to retrieve input by means of scanning a QR Code or
 * manually entering a pin or point reference.
 *
 * This Activity contains a Barcode Scanner as well as an {@link EditText} to receive input
 *
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


    /**
     * Field to store a {@link BarcodeCallback} which handles what the barcode scanner should accept
     * and what to do when it has detected an accepting barcode type.
     *
     * Where it will update the {@link EditText} field with the data it captured and execute the submit method
     */
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result != null && result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
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
     * @param savedInstanceState - Saved Instance Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.decodeContinuous(callback);
        etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    /**
     * Method that checks if the data input to the {@link EditText} field matches a valid format
     * and that it matches a value stored for a point.
     * Then navigates the use to the point data scanned.
     *
     * Otherwise an error message is shown to the user and the input is cleared ready for the next input.
     */
    public void submit() {
        EditText etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);
        // TODO: Needs to be changed when DB ready to search QR code data with DB and display relevant DetailActivity Page
        if (etCodePinEntry.getText().toString().matches("\\d{1,9}")) {
            // TODO: check whether the pin exists
            Intent data = new Intent();
            data.putExtra("pin", Integer.parseInt(etCodePinEntry.getText().toString()));
            setResult(RESULT_OK, data);
            finish();
        }
        else {
            Log.d("FeedActivity", "Point for " + etCodePinEntry.getText() + " not found!");
            Snackbar.make(barcodeScannerView, "Point Not Found, Please try again.", Snackbar.LENGTH_LONG).show();
            barcodeScannerView.resume();
            clearInput();
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

}
