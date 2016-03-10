package uk.ac.kcl.stranders.hitour.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;

import uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants;
import uk.ac.kcl.stranders.hitour.database.schema.DatabaseSchema;
import uk.ac.kcl.stranders.hitour.database.schema.TableSchema;

/**
 * @version 1.0
 * A controlling abstraction on the database, handles all inputs and outputs to the database,
 * while checks with the schema whether the query is valid
 */
public class  DBWrap {

    private DatabaseSchema schema;
    private DBHelper dbHelper;

    /**
     * Constructs this database and creates a schema for our database
     * @param context The context on which is this database opened
     */
    public DBWrap(Context context, DatabaseSchema schema) {
        this.schema = schema;
        this.dbHelper = new DBHelper(context);
    }

    /**
     * Gets a one row from the database table where primary keys match
     * @param tableName     The name of the table where to look for this row
     * @param primaryKeys   The map of primary keys for this table and their values
     * @return              A cursor of the records
     * @throws NotInSchemaException In case that the request was malformed
     */
    public Cursor getWholeByPrimary(String tableName,Map<String,String> primaryKeys) throws NotInSchemaException {
        TableSchema table = checkTable(tableName, primaryKeys, true);

        return searchCursor(tableName, null, table.wherePrimary(), getSortedPrimary(primaryKeys, table), "1");
    }

    /**
     * Gets a whole row in a table where the primary keys match, this is used with composite primary key,
     * where you want to supply only partial key
     * @param tableName     The name of a table to fetch from
     * @param primaryKeys   The map of primary keys for this table and their values
     * @return              A cursor of the records
     * @throws NotInSchemaException
     */
    public Cursor getWholeByPrimaryPartial(String tableName,Map<String,String> primaryKeys) throws NotInSchemaException{
        checkTable(tableName, primaryKeys, false);

        //Constructs a custom where
        String where = "";
        String[] args = new String[primaryKeys.size()];
        int i = 0;
        for (Map.Entry<String, String> stringStringEntry : primaryKeys.entrySet()) {
            where += stringStringEntry.getKey()+"=? AND ";
            args[i++] = stringStringEntry.getValue();
        }
        where = where.substring(0,where.length()-5);

        return searchCursor(tableName, null, where, args, null);
    }

    /**
     * Gets a one row and its given column from the database table where primary keys match
     * @param tableName     The name of the table where to look for this row
     * @param primaryKeys   The map of primary keys for this table and their values
     * @param column        The desired column to fetch
     * @return              A cursor that that has a one record with one column with the required column
     * @throws NotInSchemaException In case that the request was malformed
     */
    public Cursor getColumnByPrimary(String tableName,Map<String,String> primaryKeys, String column) throws NotInSchemaException {
        TableSchema table = checkTable(tableName, primaryKeys, true);

        return searchCursor(tableName, new String[]{column}, table.wherePrimary(), getSortedPrimary(primaryKeys, table), "1");
    }

    /**
     * Gets a given column for all rows in a given table
     * @param tableName The name of the table which to search
     * @param column    The name of the column that is to be returned
     * @return  A cursor of column values for all the entries in the given table
     * @throws NotInSchemaException In case that the request was malformed
     */
    public Cursor getAllColumn(String tableName, String column) throws NotInSchemaException {
        if(!schema.hasTable(tableName))
            throw new NotInSchemaException();

        return searchCursor(tableName, new String[]{column}, null, null, null);
    }

    /**
     * Gets all rows in given table
     * @param tableName The name of the table which to search
     * @return A cursor of the records
     * @throws NotInSchemaException In case that the request was malformed
     */
    public Cursor getAll(String tableName) throws NotInSchemaException {
        return searchCursor(tableName, null, null, null, null);
    }

    /**
     * A convenience function for every search, it checks all of the query values against schema
     * to see whether they are valid
     * @param tableName The name of the table to query
     * @param columns   The columns to select
     * @param selection The where clause
     * @param args      The arguments for where clause
     * @param limit     The limit of entries for the request
     * @return  A list of Map with all column->value that were returned
     * @throws NotInSchemaException In case that the request was malformed
     */
    private Cursor searchCursor(String tableName, String[] columns, String selection, String[] args, String limit) throws NotInSchemaException{
        //Table exists
        if (!schema.hasTable(tableName))
            throw new NotInSchemaException();

        //Columns exist
        if (columns!= null)
            for (String column : columns) {
                if (!schema.hasColumn(tableName, column))
                    throw new NotInSchemaException();
            }

        //Getting db
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //querying
        return db.query(true, tableName, columns, selection, args, null, null, null, limit);
    }

    /**
     * Inserts a given entry to a given table in the database
     * @param columns       The values for the columns in this table
     * @param primaryKeys   The primary keys in this table for this entry
     * @param tableName     The name of the table to which this entry is inserted
     * @throws NotInSchemaException In case that the request was malformed, ie does not match the schema
     */
    public void insert(Map<String,String> columns,Map<String,String> primaryKeys, String tableName) throws NotInSchemaException{
        TableSchema table;
        ContentValues content = new ContentValues();
        if ((table = schema.getTable(tableName))==null || columns.size() != table.getColumns().size())
            throw new NotInSchemaException();

        for (Map.Entry<String,String> entry : columns.entrySet()) {
            if (!table.hasColumn(entry.getKey()))
                throw new NotInSchemaException();
            content.put(entry.getKey(),entry.getValue());
        }

        checkPrimary(primaryKeys,table,content,true);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insertWithOnConflict(tableName, null, content, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /**
     * Convenience function that checks whether the given primary keys are in the table
     * @param primaryKeys   The primary keys to be checked
     * @param table         The table in which the keys are supposed to be
     * @param content       The content to which write these keys, can be null if we dont want to write any
     * @param exactSize     Whether the keys have to be exact or only subset it enough
     * @throws NotInSchemaException In case that the request was malformed, ie does not match the schema
     */
    private void checkPrimary(Map<String,String> primaryKeys,TableSchema table,ContentValues content, boolean exactSize) throws NotInSchemaException{
        if (exactSize && primaryKeys.size() != table.getPrimaryKeys().size())
            throw new NotInSchemaException();

        for (Map.Entry<String,String> entry : primaryKeys.entrySet()) {
            if (!table.hasPrimaryKey(entry.getKey()))
                throw new NotInSchemaException();
            if (content != null)
                content.put(entry.getKey(),entry.getValue());
        }
    }

    /**
     * Convenience function to sort the primary keys in the same order as they are sorted in the where query
     * @param primaryKeys   The primary keys to be sorted
     * @param table         The table which is used for query
     * @return  A string array with sorted primary keys
     */
    private String[] getSortedPrimary(Map<String,String> primaryKeys,TableSchema table) {
        String[] res = new String[table.getPrimaryKeys().size()];
        int i = 0;
        for (String primary : table.getPrimaryKeys()) {
            res[i++] = primaryKeys.get(primary);
        }
        return res;
    }

    /**
     * A convenience function to check whether given table exists and the primary keys are those that
     * are required by this table
     * @param tableName     The name of the table
     * @param primaryKeys   The primary keys to be checked
     * @param exactSize     Whether the keys have to be exact or only subset it enough
     * @return  A table schema if the table exists
     * @throws NotInSchemaException In case that the request was malformed, ie does not match the schema
     */
    private TableSchema checkTable(String tableName, Map<String,String> primaryKeys, boolean exactSize) throws NotInSchemaException {
        TableSchema table = schema.getTable(tableName);
        if(table == null)
            throw new NotInSchemaException();

        checkPrimary(primaryKeys, table, null,exactSize);
        return table;
    }

    /**
     * A helper to opening and accessing the database
     */
    private class DBHelper extends SQLiteOpenHelper{

        /**
         * Constructs the table with respects to the current schema
         * @param context The current context where the database is being opened
         */
        public DBHelper(Context context) {
            super(context, schema.getDatabaseName(), null, schema.getVersion());
        }

        /**
         * Called when the database is created for the first time. This is where the
         * creation of tables and the initial population of the tables should happen.
         *
         * @param db The database.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            for (TableSchema tableSchema : schema.getTables()) {
                db.execSQL(tableSchema.getSQLCreateTable());
            }
        }

        /**
         * Called when the database needs to be upgraded. The implementation
         * should use this method to drop tables, add tables, or do anything else it
         * needs to upgrade to the new schema version.
         *
         * <p>
         * The SQLite ALTER TABLE documentation can be found
         * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
         * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
         * you can use ALTER TABLE to rename the old table, then create the new table and then
         * populate the new table with the contents of the old table.
         * </p><p>
         * This method executes within a transaction.  If an exception is thrown, all changes
         * will automatically be rolled back.
         * </p>
         *
         * @param db The database.
         * @param oldVersion The old database version.
         * @param newVersion The new database version.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (TableSchema tableSchema : schema.getTables()) {
                db.execSQL(tableSchema.dropTable());
            }
            for (TableSchema tableSchema : schema.getTables()) {
                db.execSQL(tableSchema.getSQLCreateTable());
            }
        }

        /**
         * Called when the database needs to be downgraded. This is strictly similar to
         * {@link #onUpgrade} method, but is called whenever current version is newer than requested one.
         * However, this method is not abstract, so it is not mandatory for a customer to
         * implement it. If not overridden, default implementation will reject downgrade and
         * throws SQLiteException
         *
         * <p>
         * This method executes within a transaction.  If an exception is thrown, all changes
         * will automatically be rolled back.
         * </p>
         *
         * @param db The database.
         * @param oldVersion The old database version.
         * @param newVersion The new database version.
         */
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db,oldVersion,newVersion);
        }
    }

    /**
     * Get all rows for a given tourID with the UNLOCK value being set to isUnlocked.
     *
     * @param isUnlocked the value of the point lock state
     * @param tourId
     * @return Cursor with filtered rows from the POINT_TOUR table
     * @throws NotInSchemaException
     */
    public Cursor getUnlocked(String isUnlocked, String tourId) throws NotInSchemaException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseConstants.POINT_ID,
                DatabaseConstants.RANK,
                DatabaseConstants.UNLOCK
        };

        String selection = DatabaseConstants.UNLOCK + " = ? AND " + DatabaseConstants.TOUR_ID + "= ?" ;
        String[] selectionArgs = { isUnlocked, tourId };

        // TODO: THE SORT ORDER MAY BE NOT CONSISTENT WITH THE CURRENT IMPLEMENTATION (is it sorted by id?)
        String sortOrder = DatabaseConstants.RANK + " ASC";

        Cursor cursor = db.query(
                DatabaseConstants.POINT_TOUR_TABLE,       // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }
}
