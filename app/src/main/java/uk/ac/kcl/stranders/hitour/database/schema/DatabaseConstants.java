package uk.ac.kcl.stranders.hitour.database.schema;

/**
 * @version 1.0
 * Constants for our database schema
 */
public class DatabaseConstants {

    public static final String HI_TOUR = "HI_TOUR";
    public static final String NAME = "NAME";
    public static final String TOUR_ID = "TOUR_ID";
    public static final String AUDIENCE_ID = "AUDIENCE_ID";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String SESSION_CODE = "SESSION_CODE";
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String POINT_ID = "POINT_ID";
    public static final String DATA_ID = "DATA_ID";
    public static final String URL = "URL";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String TITLE = "TITLE";
    public static final String RANK = "RANK";


    public static final String TOUR_TABLE = "TOUR";
    public static final String SESSION_TABLE = "SESSION";
    public static final String AUDIENCE_TABLE = "AUDIENCE";
    public static final String POINT_TABLE = "POINT";
    public static final String DATA_TABLE = "DATA";
    public static final String POINT_TOUR_TABLE = "POINT_TOUR";
    public static final String AUDIENCE_DATA_TABLE = "AUDIENCE_DATA";
    public static final String POINT_DATA_TABLE = "POINT_DATA";

    public static final int AUDIENCE_DATA_COLUMN_AUDIENCE_ID = 1;

    public static final int DATA_COLUMN_NAME = 1;
    public static final int DATA_COLUMN_DESCRIPTION = 2;
    public static final int DATA_COLUMN_URL = 3;

    public static final int POINT_COLUMN_NAME = 1;
    public static final int POINT_COLUMN_DESCRIPTION = 2;
    public static final int POINT_COLUMN_URL = 3;

    public static final int POINT_TOUR_COLUMN_POINT_ID = 1;

    public static final int POINT_DATA_COLUMN_DATA_ID = 1;

    public static final int TOUR_COLUMN_TOUR_ID = 0;
    public static final int TOUR_COLUMN_AUDIENCE_ID = 1;
    public static final int TOUR_COLUMN_NAME = 2;

}
