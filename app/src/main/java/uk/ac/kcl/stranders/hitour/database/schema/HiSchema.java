package uk.ac.kcl.stranders.hitour.database.schema;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.*;

/**
 * @version 1.0
 * Schema for the hiTour Android database.
 */
public class HiSchema extends DatabaseSchema {

    /**
     * Constructor for the database schema, gives the database a name and current version
     *
     * @param version      The version of the database
     */
    public HiSchema(int version) {
        super(HI_TOUR, version);

        TableSchema tour = new TableSchema(TOUR_ID, TOUR_TABLE);
        tour.addValue(NAME, DataType.Text);
        tour.addValue(AUDIENCE_ID, DataType.Text);
        tour.addValue(QUIZ_URL, DataType.Text);
        addTable(tour);

        TableSchema session = new TableSchema(SESSION_ID, SESSION_TABLE);
        session.addValue(PASSPHRASE, DataType.Text);
        session.addValue(START_DATE, DataType.Text);
        session.addValue(DURATION, DataType.Text);
        session.addValue(TOUR_ID, DataType.Text);
        session.addValue(NAME, DataType.Text);
        addTable(session);

        TableSchema audience = new TableSchema(AUDIENCE_ID,AUDIENCE_TABLE);
        addTable(audience);

        TableSchema point = new TableSchema(POINT_ID,POINT_TABLE);
        point.addValue(NAME, DataType.Text);
        point.addValue(URL, DataType.Text);
        point.addValue(DESCRIPTION, DataType.Text);
        addTable(point);

        TableSchema data = new TableSchema(DATA_ID,DATA_TABLE);
        data.addValue(URL, DataType.Text);
        data.addValue(DESCRIPTION, DataType.Text);
        data.addValue(TITLE, DataType.Text);
        addTable(data);

        TableSchema pointTour = new TableSchema(TOUR_ID, POINT_TOUR_TABLE);
        pointTour.addPrimaryKey(POINT_ID);
        pointTour.addValue(RANK, DataType.Text);
        pointTour.addValue(UNLOCK, DataType.Text);
        addTable(pointTour);

        TableSchema audienceData = new TableSchema(DATA_ID, AUDIENCE_DATA_TABLE);
        audienceData.addPrimaryKey(AUDIENCE_ID);
        addTable(audienceData);

        TableSchema pointData = new TableSchema(POINT_ID, POINT_DATA_TABLE);
        pointData.addPrimaryKey(DATA_ID);
        pointData.addValue(RANK, DataType.Text);
        addTable(pointData);

    }
}
