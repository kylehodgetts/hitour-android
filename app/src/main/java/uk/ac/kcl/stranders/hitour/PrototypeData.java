package uk.ac.kcl.stranders.hitour;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Matrix;

public class PrototypeData {
    public static final int _ID = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int IMAGE = 3;
    public static final int VIDEO = 4;

    public static Cursor getCursor() {
        String[] columns = new String[] { "_id", "title", "description", "image", "video"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[] { 0, "Magnetic Resonance Imaging Scanner", "Magnetic resonance imaging (MRI) is a noninvasive medical test that physicians use to diagnose and treat medical conditions. MRI uses a powerful magnetic field, radio frequency pulses and a computer to produce detailed pictures of organs, soft tissues, bone and virtually all other internal body structures. MRI does not use ionizing radiation (x-rays). \nDetailed MR images allow physicians to evaluate various parts of the body and determine the presence of certain diseases. The images can then be examined on a computer monitor, transmitted electronically, printed or copied to a CD.", R.drawable.mri, R.raw.mriscan});
        cursor.addRow(new Object[] { 1, "Fluoroscopy Suite", "fluoroscopy is a radiology technique that takes a real time \"movie\" of the body. A continuous X-ray beam is passed through the body part being examined and is transmitted to a TV-like monitor so that the body part and its motion can be seen in detail.", R.drawable.fluoroscopy, R.raw.fluoroscopy});
        cursor.addRow(new Object[] { 2, "Computed tomography", "Computed tomography (CT) is a diagnostic imaging test used to create detailed images of internal organs, bones, soft tissue and blood vessels. \n\nThe cross-sectional images generated during a CT scan can be reformatted in multiple planes, and can even generate three-dimensional images which can be viewed on a computer monitor, printed on film or transferred to electronic media. \n\nCT scanning is often the best method for detecting many different cancers since the images allow your doctor to confirm the presence of a tumor and determine its size and location. CT is fast, painless, noninvasive and accurate. In emergency cases, it can reveal internal injuries and bleeding quickly enough to help save lives.", R.drawable.ctscan, R.raw.ctscan});
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
        dataTable.addRow(new Object[] {"D001", "Data Item 1", "Image 1 Description", R.drawable.fluoroscopy});
        dataTable.addRow(new Object[] {"D002", "Data Item 2", "Video 2 Description", R.raw.ultrasound });

        String[] pointDataColumns = new String[] {"point_id", "data_id", "rank"};
        MatrixCursor pointDataTable = new MatrixCursor(pointDataColumns);
        pointDataTable.addRow(new Object[] {2, "D002", 0});
        pointDataTable.addRow(new Object[] {2, "D001", 1});

        pointDataTable.moveToFirst();

        MatrixCursor toReturn = new MatrixCursor(dataTableColumns);
        for(int i = 0; i < pointDataTable.getCount(); ++i) {
            for(int j = 0; j < pointDataTable.getCount(); ++j) {
                if(pointDataTable.getInt(0) == id && pointDataTable.getInt(2) == i) {
                    dataTable.moveToFirst();
                    for(int k = 0; k < dataTable.getCount(); ++k) {
                        if(dataTable.getString(0).equals(pointDataTable.getString(1))) {
                            toReturn.addRow(new Object[] {dataTable.getString(0), dataTable.getString(1), dataTable.getString(2), dataTable.getString(3)});
                            break;
                        }
                        dataTable.moveToNext();
                    }
                }
            }
            pointDataTable.moveToNext();
        }
        return toReturn;
    }

}
