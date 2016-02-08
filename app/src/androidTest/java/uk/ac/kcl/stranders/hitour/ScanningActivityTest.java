package uk.ac.kcl.stranders.hitour;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.BitSource;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

/**
 * Created by CBaker on 08/02/2016.
 */
public class ScanningActivityTest extends ActivityInstrumentationTestCase2<ScanningActivity> {

    public ScanningActivityTest() {
        super(ScanningActivity.class);
    }

    public void testActivityExists() {
        ScanningActivity scanningActivity = getActivity();
        assertNotNull(scanningActivity);
    }

    public void testBarcodeScannerRetrieval() {
        CompoundBarcodeView barcodeView = (CompoundBarcodeView) getActivity().findViewById(R.id.zxing_barcode_scanner);

    }
}
