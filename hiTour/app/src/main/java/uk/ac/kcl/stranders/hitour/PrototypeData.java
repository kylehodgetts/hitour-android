package uk.ac.kcl.stranders.hitour;

import android.database.Cursor;
import android.database.MatrixCursor;

public class PrototypeData {
    public static final int _ID = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int IMAGE = 3;

    public static Cursor getCursor() {
        String[] columns = new String[] { "_id", "title", "description", "image"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[] { 0, "Magnetic Resonance Imaging Scanner", "Magnetic resonance imaging (MRI) is a noninvasive medical test that physicians use to diagnose and treat medical conditions.\n" +
                "\n" +
                "MRI uses a powerful magnetic field, radio frequency pulses and a computer to produce detailed pictures of organs, soft tissues, bone and virtually all other internal body structures. MRI does not use ionizing radiation (x-rays).\n" +
                "\n" +
                "Detailed MR images allow physicians to evaluate various parts of the body and determine the presence of certain diseases. The images can then be examined on a computer monitor, transmitted electronically, printed or copied to a CD.", R.drawable.mri});
        cursor.addRow(new Object[] { 1, "Fluoroscopy Suite", "Fluoroscopy is a radiology technique that takes a real time \"movie\" of the body. A continuous X-ray beam is passed through the body part being examined and is transmitted to a TV-like monitor so that the body part and its motion can be seen in detail.", R.drawable.fluoroscopy});
        cursor.addRow(new Object[] { 2, "Computed tomography", "Computed tomography (CT) is a diagnostic imaging test used to create detailed images of internal organs, bones, soft tissue and blood vessels.", R.drawable.ctscan});
        cursor.addRow(new Object[] { 3, "Ultrasound scan", "An ultrasound scan, sometimes called a sonogram, is a procedure that uses high-frequency sound waves to create an image of part of the inside of the body.", R.drawable.ultrasound});

        return cursor;
    }

}
