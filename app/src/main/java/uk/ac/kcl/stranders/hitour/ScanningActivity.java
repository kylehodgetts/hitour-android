package uk.ac.kcl.stranders.hitour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class ScanningActivity extends AppCompatActivity {
    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        setSubmitButtonListener();
    }

    private void setSubmitButtonListener() {
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);
                // TODO: Needs to be changed when DB ready to search QR code data with DB and display relevant DetailActivity Page
                if(etCodePinEntry.getText().toString().matches("\\d+")  && PrototypeData.containsId(Integer.parseInt(etCodePinEntry.getText().toString()))) {
                    Intent intent = new Intent(ScanningActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_BUNDLE, Integer.parseInt(etCodePinEntry.getText().toString()));
                    startActivity(intent);
                }
                else {
                    Log.d("FeedActivity", "Point for " + etCodePinEntry.getText() + " not found!");
                    Toast.makeText(ScanningActivity.this, "Point Not Found, Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


}
