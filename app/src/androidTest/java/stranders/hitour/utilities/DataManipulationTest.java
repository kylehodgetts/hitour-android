package stranders.hitour.utilities;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;


import java.util.ArrayList;

import stranders.hitour.activity.FeedActivity;
import stranders.hitour.database.DBWrap;
import stranders.hitour.database.NotInSchemaException;
import stranders.hitour.database.schema.HiSchema;
import stranders.hitour.model.Data;
import stranders.hitour.model.Point;
import stranders.hitour.model.Tour;
import stranders.hitour.model.TourSession;

import static stranders.hitour.database.schema.DatabaseConstants.*;

public class DataManipulationTest extends ActivityInstrumentationTestCase2<FeedActivity> {

    private TourSession tourSessionOne, tourSessionTwo, tourSessionThree;
    private Tour tourOne, tourTwo;
    private DBWrap database;

    public DataManipulationTest() {
        super(FeedActivity.class);
    }

    /**
     * Initialize and populate the database.
     */
    @Override
    public void setUp() {

        database = new DBWrap(getActivity(), new HiSchema(2));
        tourSessionOne = new TourSession();
        tourSessionOne.setId(1);
        tourSessionOne.setTourId(1);
        tourSessionOne.setStartDate("2016-03-16");
        tourSessionOne.setDuration(10);
        tourSessionOne.setPassphrase("TestingPassphrase1");
        tourSessionOne.setName("This is the first tour session of the test");

        tourSessionTwo = new TourSession();
        tourSessionTwo.setId(2);
        tourSessionTwo.setTourId(2);
        tourSessionTwo.setDuration(20);
        tourSessionTwo.setStartDate("2016-03-21");
        tourSessionTwo.setPassphrase("TestingPassphrase2");
        tourSessionTwo.setName("This is the second tour session of the test");

        tourSessionThree = new TourSession();
        tourSessionThree.setId(3);
        tourSessionThree.setTourId(2);
        tourSessionThree.setDuration(30);
        tourSessionThree.setStartDate("2016-03-26");
        tourSessionThree.setPassphrase("TestingPassphrase3");
        tourSessionThree.setName("This is the third tour session of the test");

        tourOne = new Tour();
        tourOne.setId(1);
        tourOne.setName("First tour of the test");
        tourOne.setAudienceId(1);
        tourOne.setQuizUrl("http://www.fakeaddress.com/test");

        tourTwo = new Tour();
        tourTwo.setId(2);
        tourTwo.setName("Second tour of the test");
        tourTwo.setAudienceId(2);
        tourTwo.setQuizUrl("http://www.anotherfakeaddress.com/test");

        Point pointOneTourOne = new Point();
        pointOneTourOne.setId(1);
        pointOneTourOne.setName("Point one");
        pointOneTourOne.setUrl("http://www.testdataurl.com/1");
        pointOneTourOne.setDescription("Description of point one");
        pointOneTourOne.setRank(1);

        Point pointTwoTourOne = new Point();
        pointTwoTourOne.setId(2);
        pointTwoTourOne.setName("Point two");
        pointTwoTourOne.setUrl("http://www.testdataurl.com/2");
        pointTwoTourOne.setDescription("Description of point two");
        pointTwoTourOne.setRank(2);

        Point pointThreeTourOne = new Point();
        pointThreeTourOne.setId(3);
        pointThreeTourOne.setName("Point three");
        pointThreeTourOne.setUrl("http://www.testdataurl.com/3");
        pointThreeTourOne.setDescription("Description of point three");
        pointThreeTourOne.setRank(3);

        Point pointOneTourTwo = new Point();
        pointOneTourTwo.setId(1);
        pointOneTourTwo.setName("Point one");
        pointOneTourTwo.setUrl("http://www.testdataurl.com/1");
        pointOneTourTwo.setDescription("Description of point one");
        pointOneTourTwo.setRank(1);

        Point pointThreeTourTwo = new Point();
        pointThreeTourTwo.setId(3);
        pointThreeTourTwo.setName("Point three");
        pointThreeTourTwo.setUrl("http://www.testdataurl.com/3");
        pointThreeTourTwo.setDescription("Description of point three");
        pointThreeTourTwo.setRank(2);

        Point pointFourTourTwo = new Point();
        pointFourTourTwo.setId(4);
        pointFourTourTwo.setName("Point four");
        pointFourTourTwo.setUrl("http://www.testdataurl.com/4");
        pointFourTourTwo.setDescription("Description of point four");
        pointFourTourTwo.setRank(3);

        Point pointFiveTourTwo = new Point();
        pointFiveTourTwo.setId(5);
        pointFiveTourTwo.setName("Point five");
        pointFiveTourTwo.setUrl("http://www.testdataurl.com/5");
        pointFiveTourTwo.setDescription("Description of point five");
        pointFiveTourTwo.setRank(4);

        Data dataOnePointOneTourOne = new Data();
        dataOnePointOneTourOne.setId(1);
        dataOnePointOneTourOne.setTitle("Data one title");
        dataOnePointOneTourOne.setDescription("Data one description");
        dataOnePointOneTourOne.setUrl("http://www.testdataurl.com/1");
        dataOnePointOneTourOne.setRank(1);

        Data dataTwoPointOneTourOne = new Data();
        dataTwoPointOneTourOne.setId(2);
        dataTwoPointOneTourOne.setTitle("Data two title");
        dataTwoPointOneTourOne.setDescription("Data two description");
        dataTwoPointOneTourOne.setUrl("http://www.testdataurl.com/6");
        dataTwoPointOneTourOne.setRank(2);

        Data dataThreePointTwoTourOne = new Data();
        dataThreePointTwoTourOne.setId(3);
        dataThreePointTwoTourOne.setTitle("Data three title");
        dataThreePointTwoTourOne.setDescription("Data three description");
        dataThreePointTwoTourOne.setUrl("http://www.testdataurl.com/7");
        dataThreePointTwoTourOne.setRank(1);

        Data dataFourPointTwoTourOne = new Data();
        dataFourPointTwoTourOne.setId(4);
        dataFourPointTwoTourOne.setTitle("Data four title");
        dataFourPointTwoTourOne.setDescription("Data four description");
        dataFourPointTwoTourOne.setUrl("http://www.testdataurl.com/8");
        dataFourPointTwoTourOne.setRank(2);

        Data dataTwoPointThreeTourOne = new Data();
        dataTwoPointThreeTourOne.setId(2);
        dataTwoPointThreeTourOne.setTitle("Data two title");
        dataTwoPointThreeTourOne.setDescription("Data two description");
        dataTwoPointThreeTourOne.setUrl("http://www.testdataurl.com/6");
        dataTwoPointThreeTourOne.setRank(1);

        Data dataFivePointThreeTourOne = new Data();
        dataFivePointThreeTourOne.setId(5);
        dataFivePointThreeTourOne.setTitle("Data five title");
        dataFivePointThreeTourOne.setDescription("Data five description");
        dataFivePointThreeTourOne.setUrl("http://www.testdataurl.com/4");
        dataFivePointThreeTourOne.setRank(2);

        Data dataSixPointThreeTourOne = new Data();
        dataSixPointThreeTourOne.setId(6);
        dataSixPointThreeTourOne.setTitle("Data six title");
        dataSixPointThreeTourOne.setDescription("Data six description");
        dataSixPointThreeTourOne.setUrl("http://www.testdataurl.com/9");
        dataSixPointThreeTourOne.setRank(3);

        Data dataOnePointOneTourTwo = new Data();
        dataOnePointOneTourTwo.setId(1);
        dataOnePointOneTourTwo.setTitle("Data one title");
        dataOnePointOneTourTwo.setDescription("Data one description");
        dataOnePointOneTourTwo.setUrl("http://www.testdataurl.com/1");
        dataOnePointOneTourTwo.setRank(1);

        Data dataFivePointThreeTourTwo = new Data();
        dataFivePointThreeTourTwo.setId(5);
        dataFivePointThreeTourTwo.setTitle("Data five title");
        dataFivePointThreeTourTwo.setDescription("Data five description");
        dataFivePointThreeTourTwo.setUrl("http://www.testdataurl.com/4");
        dataFivePointThreeTourTwo.setRank(1);

        Data dataThreePointFourTourTwo = new Data();
        dataThreePointFourTourTwo.setId(3);
        dataThreePointFourTourTwo.setTitle("Data three title");
        dataThreePointFourTourTwo.setDescription("Data three description");
        dataThreePointFourTourTwo.setUrl("http://www.testdataurl.com/7");
        dataThreePointFourTourTwo.setRank(1);

        Data dataSevenPointFiveTourTwo = new Data();
        dataSevenPointFiveTourTwo.setId(7);
        dataSevenPointFiveTourTwo.setTitle("Data seven title");
        dataSevenPointFiveTourTwo.setDescription("Data seven description");
        dataSevenPointFiveTourTwo.setUrl("http://www.testdataurl.com/2");
        dataSevenPointFiveTourTwo.setRank(1);

        Data dataEightPointFiveTourTwo = new Data();
        dataEightPointFiveTourTwo.setId(8);
        dataEightPointFiveTourTwo.setTitle("Data eight title");
        dataEightPointFiveTourTwo.setDescription("Data eight description");
        dataEightPointFiveTourTwo.setUrl("http://www.testdataurl.com/10");
        dataEightPointFiveTourTwo.setRank(2);

        ArrayList<Data> pointOneTourOneData = new ArrayList<>();
        pointOneTourOneData.add(dataOnePointOneTourOne);
        pointOneTourOneData.add(dataTwoPointOneTourOne);
        pointOneTourOne.setData(pointOneTourOneData);

        ArrayList<Data> pointTwoTourOneData = new ArrayList<>();
        pointTwoTourOneData.add(dataThreePointTwoTourOne);
        pointTwoTourOneData.add(dataFourPointTwoTourOne);
        pointTwoTourOne.setData(pointTwoTourOneData);

        ArrayList<Data> pointThreeTourOneData = new ArrayList<>();
        pointThreeTourOneData.add(dataTwoPointThreeTourOne);
        pointThreeTourOneData.add(dataFivePointThreeTourOne);
        pointThreeTourOneData.add(dataSixPointThreeTourOne);
        pointThreeTourOne.setData(pointThreeTourOneData);

        ArrayList<Data> pointOneTourTwoData = new ArrayList<>();
        pointOneTourTwoData.add(dataOnePointOneTourTwo);
        pointOneTourTwo.setData(pointOneTourTwoData);

        ArrayList<Data> pointThreeTourTwoData = new ArrayList<>();
        pointThreeTourTwoData.add(dataFivePointThreeTourTwo);
        pointThreeTourTwo.setData(pointThreeTourTwoData);

        ArrayList<Data> pointFourTourTwoData = new ArrayList<>();
        pointFourTourTwoData.add(dataThreePointFourTourTwo);
        pointFourTourTwo.setData(pointFourTourTwoData);

        ArrayList<Data> pointFiveTourTwoData = new ArrayList<>();
        pointFiveTourTwoData.add(dataSevenPointFiveTourTwo);
        pointFiveTourTwoData.add(dataEightPointFiveTourTwo);
        pointFiveTourTwo.setData(pointFiveTourTwoData);

        ArrayList<Point> tourOnePoints = new ArrayList<>();
        tourOnePoints.add(pointOneTourOne);
        tourOnePoints.add(pointTwoTourOne);
        tourOnePoints.add(pointThreeTourOne);
        tourOne.setPoints(tourOnePoints);

        ArrayList<Point> tourTwoPoints = new ArrayList<>();
        tourTwoPoints.add(pointOneTourTwo);
        tourTwoPoints.add(pointThreeTourTwo);
        tourTwoPoints.add(pointFourTourTwo);
        tourTwoPoints.add(pointFiveTourTwo);
        tourTwo.setPoints(tourTwoPoints);
        
    }

    /**
     * Test that sessions get added and removed correctly in this order
     */
    public void testOrderOne() {

        try {
            FeedActivity feedActivity = getActivity();
            // Test adding first tour session
            DataManipulation.addSession(tourSessionOne, tourOne, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorOne = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorOne.getCount(), 1);
            Cursor tourCursorOne = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorOne.getCount(), 1);
            Cursor pointCursorOne = database.getAll(POINT_TABLE);
            assertEquals(pointCursorOne.getCount(), 3);
            Cursor dataCursorOne = database.getAll(DATA_TABLE);
            assertEquals(dataCursorOne.getCount(), 6);

            // Test adding second tour session
            DataManipulation.addSession(tourSessionTwo, tourTwo, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorTwo = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorTwo.getCount(), 2);
            Cursor tourCursorTwo = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorTwo.getCount(), 2);
            Cursor pointCursorTwo = database.getAll(POINT_TABLE);
            assertEquals(pointCursorTwo.getCount(), 5);
            Cursor dataCursorTwo = database.getAll(DATA_TABLE);
            assertEquals(dataCursorTwo.getCount(), 8);

            // Test adding third tour session
            DataManipulation.addSession(tourSessionThree, tourTwo, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorThree = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorThree.getCount(), 3);
            Cursor tourCursorThree = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorThree.getCount(), 2);
            Cursor pointCursorThree = database.getAll(POINT_TABLE);
            assertEquals(pointCursorThree.getCount(), 5);
            Cursor dataCursorThree = database.getAll(DATA_TABLE);
            assertEquals(dataCursorThree.getCount(), 8);
            
            // Test removing third tour session
            DataManipulation.removeSession("3", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorFour = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorFour.getCount(), 2);
            Cursor tourCursorFour = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorFour.getCount(), 2);
            Cursor pointCursorFour = database.getAll(POINT_TABLE);
            assertEquals(pointCursorFour.getCount(), 5);
            Cursor dataCursorFour = database.getAll(DATA_TABLE);
            assertEquals(dataCursorFour.getCount(), 8);

            // Test removing first tour session
            DataManipulation.removeSession("1", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorFive = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorFive.getCount(), 1);
            Cursor tourCursorFive = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorFive.getCount(), 1);
            Cursor pointCursorFive = database.getAll(POINT_TABLE);
            assertEquals(pointCursorFive.getCount(), 4);
            Cursor dataCursorFive = database.getAll(DATA_TABLE);
            assertEquals(dataCursorFive.getCount(), 5);

            // Test removing second tour session
            DataManipulation.removeSession("2", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorSix = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorSix.getCount(), 0);
            Cursor tourCursorSix = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorSix.getCount(), 0);
            Cursor pointCursorSix = database.getAll(POINT_TABLE);
            assertEquals(pointCursorSix.getCount(), 0);
            Cursor dataCursorSix = database.getAll(DATA_TABLE);
            assertEquals(dataCursorSix.getCount(), 0);

        } catch (NotInSchemaException e) {
            fail("Database threw a NotInSchemaException");
        }

    }

    /**
     * Test that sessions get added and removed correctly in this order
     */
    public void testOrderTwo() {

        try {
            FeedActivity feedActivity = getActivity();
            // Test adding second tour session
            DataManipulation.addSession(tourSessionTwo, tourTwo, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorTwo = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorTwo.getCount(), 1);
            Cursor tourCursorTwo = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorTwo.getCount(), 1);
            Cursor pointCursorTwo = database.getAll(POINT_TABLE);
            assertEquals(pointCursorTwo.getCount(), 4);
            Cursor dataCursorTwo = database.getAll(DATA_TABLE);
            assertEquals(dataCursorTwo.getCount(), 5);

            // Test adding third tour session
            DataManipulation.addSession(tourSessionThree, tourTwo, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorThree = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorThree.getCount(), 2);
            Cursor tourCursorThree = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorThree.getCount(), 1);
            Cursor pointCursorThree = database.getAll(POINT_TABLE);
            assertEquals(pointCursorThree.getCount(), 4);
            Cursor dataCursorThree = database.getAll(DATA_TABLE);
            assertEquals(dataCursorThree.getCount(), 5);

            // Test adding first tour session
            DataManipulation.addSession(tourSessionOne, tourOne, feedActivity.getApplicationContext(), database);
            Cursor sessionCursorOne = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorOne.getCount(), 3);
            Cursor tourCursorOne = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorOne.getCount(),2);
            Cursor pointCursorOne = database.getAll(POINT_TABLE);
            assertEquals(pointCursorOne.getCount(), 5);
            Cursor dataCursorOne = database.getAll(DATA_TABLE);
            assertEquals(dataCursorOne.getCount(), 8);

            // Test removing second tour session
            DataManipulation.removeSession("2", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorSix = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorSix.getCount(), 2);
            Cursor tourCursorSix = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorSix.getCount(), 2);
            Cursor pointCursorSix = database.getAll(POINT_TABLE);
            assertEquals(pointCursorSix.getCount(), 5);
            Cursor dataCursorSix = database.getAll(DATA_TABLE);
            assertEquals(dataCursorSix.getCount(), 8);

            // Test removing third tour session
            DataManipulation.removeSession("3", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorFour = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorFour.getCount(), 1);
            Cursor tourCursorFour = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorFour.getCount(), 1);
            Cursor pointCursorFour = database.getAll(POINT_TABLE);
            assertEquals(pointCursorFour.getCount(), 3);
            Cursor dataCursorFour = database.getAll(DATA_TABLE);
            assertEquals(dataCursorFour.getCount(), 6);

            // Test removing first tour session
            DataManipulation.removeSession("1", feedActivity.getApplicationContext(), database);
            Cursor sessionCursorFive = database.getAll(SESSION_TABLE);
            assertEquals(sessionCursorFive.getCount(), 0);
            Cursor tourCursorFive = database.getAll(TOUR_TABLE);
            assertEquals(tourCursorFive.getCount(), 0);
            Cursor pointCursorFive = database.getAll(POINT_TABLE);
            assertEquals(pointCursorFive.getCount(), 0);
            Cursor dataCursorFive = database.getAll(DATA_TABLE);
            assertEquals(dataCursorFive.getCount(), 0);

        } catch (NotInSchemaException e) {
            fail("Database threw a NotInSchemaException");
        }

    }
    
}
