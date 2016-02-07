package uk.ac.kcl.stranders.hitour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class ScanningActivity extends AppCompatActivity {

    private CompoundBarcodeView barcodeScannerView;
    private EditText etCodePinEntry;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result != null) {
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

    public void submit() {
        EditText etCodePinEntry = (EditText) findViewById(R.id.etCodePinEntry);
        // TODO: Needs to be changed when DB ready to search QR code data with DB and display relevant DetailActivity Page
        if (etCodePinEntry.getText().toString().matches("\\d+") && PrototypeData.containsId(Integer.parseInt(etCodePinEntry.getText().toString()))) {
            Intent intent = new Intent(ScanningActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_BUNDLE, Integer.parseInt(etCodePinEntry.getText().toString()));

            barcodeScannerView.setStatusText("");
            etCodePinEntry.setText("");

            startActivity(intent);
        }
        else {
            Log.d("FeedActivity", "Point for " + etCodePinEntry.getText() + " not found!");
            Toast.makeText(ScanningActivity.this, "Point Not Found, Please try again.", Toast.LENGTH_LONG).show();
            etCodePinEntry.setText("");
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
