package uk.ac.kcl.stranders.hitour.retrofit;


import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import uk.ac.kcl.stranders.hitour.Utilities;
import uk.ac.kcl.stranders.hitour.model.DataType;

/**
 * Tests for the {@link HiTourRetrofit} class.
 */
public class HiTourRetrofitTest extends AndroidTestCase {

    private HiTourRetrofit hiTourRetrofit;

    /**
     * Initialize the {@link HiTourRetrofitTest#hiTourRetrofit}
     */
    @Override
    public void setUp() {
        hiTourRetrofit = new HiTourRetrofit(new HiTourRetrofit.CallbackRetrofit() {
            @Override
            public void onAllRequestsFinished() {
                // ignore
            }
        }, "Penguins123");
        if(!Utilities.isNetworkAvailable(mContext)) {
            fail("There is no network connection. Could not test the class.");
        } else {
            hiTourRetrofit.fetchTour();
            try {
                Thread.sleep(10000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

//    /**
//     * Test whether all requests to fetch data and create lists of model objects are successful.
//     */
//    public void testRequestsFinish() {
//        assertTrue("All requests has been finished", hiTourRetrofit.onRequestFinished());
//    }
//
//    /**
//     * Test whether the data is fetched correctly i.e. if all lists are initialised and nonempty.
//     */
//    public void testDataRetrieved() {
//        ArrayList<Boolean> isDataFetched = new ArrayList<>();
//        for(DataType dt : DataType.values()) {
//            List list = hiTourRetrofit.getList(dt);
//            if(list != null && !list.isEmpty()) {
//                isDataFetched.add(true);
//            }
//        }
//        assertEquals("The data is retrieved correctly", DataType.values().length, isDataFetched.size());
//    }

}
