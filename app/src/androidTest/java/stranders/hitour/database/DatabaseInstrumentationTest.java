package stranders.hitour.database;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;


import java.util.HashMap;
import java.util.Map;

import stranders.hitour.activity.FeedActivity;
import stranders.hitour.database.schema.DataType;
import stranders.hitour.database.schema.DatabaseSchema;
import stranders.hitour.database.schema.TableSchema;

/**
 * @version 1.0
 * Test for the database sqllite abstraction and storing
 */
public class DatabaseInstrumentationTest extends ActivityInstrumentationTestCase2<FeedActivity> {
    public DatabaseInstrumentationTest() {
        super(FeedActivity.class);
    }

    private DBWrap db;

    /**
     * Sets up the class, creates a new database, drops the old one and populates the new database
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DatabaseSchema schema = new DatabaseSchema("ANIMALS",Math.abs((int)System.currentTimeMillis()));
        TableSchema catTable = new TableSchema("CAT_ID","CATS");

        catTable.addPrimaryKey("MOTHER_CAT_ID");

        catTable.addValue("age", DataType.Text);
        catTable.addValue("colour", DataType.Text);

        schema.addTable(catTable);

        db = new DBWrap(getActivity(),schema);

        for (int i = 0; i < 5; i++) {
            Map<String,String> values = new HashMap<>();
            values.put("age", i+"a");
            values.put("colour", i+"c");

            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", i+"id");
            primary.put("MOTHER_CAT_ID",i+"m");
            db.insert(values, primary, "CATS");
        }

        for (int i = 5; i < 10; i++) {
            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", "1id");
            primary.put("MOTHER_CAT_ID", i+"m");

            Map<String,String> values = new HashMap<>();
            values.put("age", i+"a");
            values.put("colour", i+"c");
            db.insert(values,primary,"CATS");

        }
    }

    /**
     * Tests inserting to the database
     */
    public void testInsert(){
        Map<String,String> values = new HashMap<>();
        values.put("age", "12");
        values.put("colour", "blackish");

        Map<String,String> primary = new HashMap<>();
        primary.put("CAT_ID", "123ADF3");
        primary.put("MOTHER_CAT_ID", "123ADF2");
        try {
            db.insert(values, primary, "CATS");
            Cursor rets = db.getWholeByPrimary("CATS",primary);
            rets.moveToFirst();
            assertEquals("blackish", rets.getString(rets.getColumnIndex("colour")));
            assertEquals("12", rets.getString(rets.getColumnIndex("age")));
            assertEquals("123ADF3", rets.getString(rets.getColumnIndex("CAT_ID")));
            assertEquals("123ADF2", rets.getString(rets.getColumnIndex("MOTHER_CAT_ID")));
            assertTrue(rets.isLast());
        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Tests getting a value for column where primary
     */
    public void testColumnByPrimary(){
        try {
            Map<String,String> primary = new HashMap<>();
            primary.put("CAT_ID", "1id");
            primary.put("MOTHER_CAT_ID", "1m");
            Cursor ret = db.getColumnByPrimary("CATS", primary, "age");
            ret.moveToFirst();

            assertEquals("1a", ret.getString(ret.getColumnIndex("age")));
        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Getting a columns for all rows in a table
     */
    public void testGetAllColumn(){
        try {
            Cursor rets = db.getAllColumn("CATS", "age");
            assertEquals(10,rets.getCount());
            rets.moveToFirst();
            for (int i = 0; i < rets.getCount(); i++) {
                assertEquals(i+"a",rets.getString(rets.getColumnIndex("age")));
                rets.moveToNext();
            }

        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }

    /**
     * Tests getting all rows in a table
     */
    public void testGetAll(){
        try {
            Cursor rets = db.getAll("CATS");
            rets.moveToFirst();
            assertEquals(10, rets.getCount());
            for (int i = 0; i < rets.getCount(); i++) {
                if (i>1 && i < 7)
                    assertEquals("1id", rets.getString(rets.getColumnIndex("CAT_ID")));
                else if(i >= 7)
                    assertEquals((i-5)+"id",rets.getString(rets.getColumnIndex("CAT_ID")));
                else
                    assertEquals(i+"id",rets.getString(rets.getColumnIndex("CAT_ID")));
                rets.moveToNext();
            }

        } catch (NotInSchemaException e) {
            e.printStackTrace();
            fail("Schema not valid");
        }
    }
}
