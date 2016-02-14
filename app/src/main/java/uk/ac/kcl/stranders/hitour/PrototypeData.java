package uk.ac.kcl.stranders.hitour;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

/**
 * Class used for testing. Will be removed when the web database is implemented.
 */
public class PrototypeData {
    public static final int _ID = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int IMAGE = 3;
    public static final int VIDEO = 4;

    public static final int DATA_TITLE = 1;
    public static final int DATA_DESCRIPTION = 2;
    public static final int URL = 3;

    public static final int POINT_ID = 0;
    public static final int DATA_ID = 1;
    public static final int RANK = 2;



    public static Cursor getCursor() {
        String[] columns = new String[] { "_id", "title", "description", "image"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[] { 0, "Computed tomography", "Computed tomography (CT) is a diagnostic imaging test used to create detailed images of internal organs, bones, soft tissue and blood vessels. \n\nThe cross-sectional images generated during a CT scan can be reformatted in multiple planes, and can even generate three-dimensional images which can be viewed on a computer monitor, printed on film or transferred to electronic media. \n\nCT scanning is often the best method for detecting many different cancers since the images allow your doctor to confirm the presence of a tumor and determine its size and location. CT is fast, painless, noninvasive and accurate. In emergency cases, it can reveal internal injuries and bleeding quickly enough to help save lives.", R.drawable.ctscan, R.raw.ctscan});
        cursor.addRow(new Object[] { 1, "Fluoroscopy Suite", "Fluoroscopy is a radiology technique that takes a real time \"movie\" of the body. A continuous X-ray beam is passed through the body part being examined and is transmitted to a TV-like monitor so that the body part and its motion can be seen in detail.", R.drawable.fluoroscopy, R.raw.fluoroscopy});
        cursor.addRow(new Object[] { 2, "Magnetic Resonance Imaging Scanner", "Magnetic resonance imaging (MRI) is a noninvasive medical test that physicians use to diagnose and treat medical conditions. MRI uses a powerful magnetic field, radio frequency pulses and a computer to produce detailed pictures of organs, soft tissues, bone and virtually all other internal body structures. MRI does not use ionizing radiation (x-rays). \n\nDetailed MR images allow physicians to evaluate various parts of the body and determine the presence of certain diseases. The images can then be examined on a computer monitor, transmitted electronically, printed or copied to a CD.", R.drawable.mri, R.raw.mriscan});
        cursor.addRow(new Object[] { 3, "Ultrasound scan", "An ultrasound scan, sometimes called a sonogram, is a procedure that uses high-frequency sound waves to create an image of part of the inside of the body.", R.drawable.ultrasound, R.raw.ultrasound});
        
        return cursor;
    }


    public static boolean containsId(int toSearch) {
        Cursor cursor = getCursor();
        for(int i=0; i < cursor.getCount(); ++i) {
            cursor.moveToPosition(i);
            if(cursor.getInt(PrototypeData._ID) == toSearch) {
                return true;
            }
        }
        return false;
    }

    public static Cursor getContentCursor(int id) {
        String[] dataTableColumns  = new String[] {"data_id", "title", "description", "url"};
        MatrixCursor dataTable = new MatrixCursor(dataTableColumns);
        dataTable.addRow(new Object[] {"D001", "CT Brain Scan Result", "Image that shows the CT Scan result of a patients brain", R.drawable.ctscanresult1});
        dataTable.addRow(new Object[] {"D002", "What Happens In A CT Scan", "Video that shows and explains how a CT Scan works breifly as an introduction", R.raw.ctscan });
        dataTable.addRow(new Object[] {"D003", "CT Lung Scan Result", "Image that shows the results for a lung that can be performed on a patient by a CT Scan", R.drawable.ctscanresult2});
        dataTable.addRow(new Object[] {"D004", "What Happens In An MRI Scan", "A Video introduction to MRI and the equipment used including a quick demonstration of a patient using the scanner.", R.raw.mriscan});
        dataTable.addRow(new Object[] {"D005", "Demonstration of Fluoroscopy", "Video demonstration of a patient having fluoroscopy", R.raw.fluoroscopy});
        dataTable.addRow(new Object[] {"D006", "What Happens In An Ultrasound Scan", "Video introduction to ultrasound scans including demonstration of an ultrasound being performed on a patient", R.raw.ultrasound});
        dataTable.addRow(new Object[] {"D007", "MRI Brain Scan Result", "Image shows the results produced by the MRI scanner for a brain scan", R.drawable.mriscanresult1});


        String[] pointDataColumns = new String[] {"point_id", "data_id", "rank"};
        MatrixCursor pointDataTable = new MatrixCursor(pointDataColumns);
        pointDataTable.addRow(new Object[] {2, "D002", 0});
        pointDataTable.addRow(new Object[] {2, "D001", 1});
        pointDataTable.addRow(new Object[] {2, "D003", 2});
        pointDataTable.addRow(new Object[] {0, "D004", 0});
        pointDataTable.addRow(new Object[] {1, "D005", 0});
        pointDataTable.addRow(new Object[] {3, "D006", 0});
        pointDataTable.addRow(new Object[] {0, "D007", 0});



        pointDataTable.moveToFirst();

        MatrixCursor toReturn = new MatrixCursor(dataTableColumns);
            for(int j = 0; j < pointDataTable.getCount(); ++j) {
                if(pointDataTable.getInt(0) == id) {
                    dataTable.moveToFirst();
                    for(int k = 0; k < dataTable.getCount(); ++k) {
                        if(dataTable.getString(0).equals(pointDataTable.getString(1))) {
                            toReturn.addRow(new Object[]{dataTable.getString(0), dataTable.getString(1), dataTable.getString(2), dataTable.getString(3)});
                            Log.d("Prototype Data", "row added to results");
                            break;
                        }
                        dataTable.moveToNext();
                    }
            }
            pointDataTable.moveToNext();
        }

        return toReturn;
    }

}
