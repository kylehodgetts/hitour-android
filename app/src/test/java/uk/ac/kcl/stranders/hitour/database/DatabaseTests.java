package stranders.hitour.database;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stranders.hitour.database.schema.DataType;
import stranders.hitour.database.schema.DatabaseSchema;
import stranders.hitour.database.schema.TableSchema;

/**
 * @version 1.0
 *  Tests the abstraction over the sqllite database
 */
public class DatabaseTests {

    private DatabaseSchema schema;

    /**
     * Sets up a basic schema for a fictional database used for testing
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        schema = new DatabaseSchema("ANIMALS",1);
        TableSchema catTable = new TableSchema("CAT_ID","CATS");
        TableSchema dogTable = new TableSchema("DOG_ID","DOGS");

        catTable.addPrimaryKey("MOTHER_CAT_ID");
        catTable.addValue("age", DataType.Text);

        dogTable.addValue("age", DataType.Text);
        dogTable.addValue("colour", DataType.Text);

        schema.addTable(catTable);
        schema.addTable(dogTable);

    }

    /**
     * Tests whether the create statements are created in desired way
     */
    @Test
    public void testCreate(){
        TableSchema cats = schema.getTable("CATS");
        TableSchema dogs = schema.getTable("DOGS");

        Assert.assertEquals("CREATE TABLE CATS (CAT_ID TEXT, MOTHER_CAT_ID TEXT, age TEXT, PRIMARY KEY(CAT_ID, MOTHER_CAT_ID))", cats.getSQLCreateTable());
        Assert.assertEquals("CREATE TABLE DOGS (DOG_ID TEXT, colour TEXT, age TEXT, PRIMARY KEY(DOG_ID))", dogs.getSQLCreateTable());
    }


    /**
     * Tests whether the drop statements are created in desired way
     */
    @Test
    public void testDrop(){
        TableSchema cats = schema.getTable("CATS");
        TableSchema dogs = schema.getTable("DOGS");

        Assert.assertEquals("DROP TABLE IF EXISTS CATS", cats.dropTable());
        Assert.assertEquals("DROP TABLE IF EXISTS DOGS", dogs.dropTable());
    }

    /**
     * Tests whether the where query statements are created in desired way
     */
    @Test
    public void testWhere(){
        TableSchema cats = schema.getTable("CATS");
        TableSchema dogs = schema.getTable("DOGS");

        Assert.assertEquals("CAT_ID=? AND MOTHER_CAT_ID=?", cats.wherePrimary());
        Assert.assertEquals("DOG_ID=?", dogs.wherePrimary());
    }


    /**
     * Test whether the tables have all of the required columns, and the database has all the tables
     */
    @Test
    public void testHas(){
        Assert.assertTrue(schema.hasTable("CATS"));
        Assert.assertTrue(schema.hasTable("DOGS"));

        Assert.assertTrue(schema.hasColumn("CATS", "age"));
        Assert.assertTrue(schema.hasColumn("DOGS", "age"));
        Assert.assertTrue(schema.hasColumn("DOGS", "colour"));

        Assert.assertEquals("ANIMALS", schema.getDatabaseName());
        Assert.assertEquals(1, schema.getVersion());

        Assert.assertEquals(2, schema.getTables().size());
    }

    /**
     * Tests whether the modifications to table schema work in desired way
     */
    @Test
    public void testAdd(){
        TableSchema dummy = new TableSchema("DUMMY_ID","DUMMIES");
        Assert.assertEquals("DUMMIES", dummy.getTableName());
        Assert.assertTrue(dummy.hasPrimaryKey("DUMMY_ID"));

        dummy.addValue("DUMMY_NAME", DataType.Text);
        Assert.assertTrue(dummy.hasColumn("DUMMY_NAME"));

        dummy.addPrimaryKey("DUMMY_SERIAL");
        Assert.assertTrue(dummy.hasPrimaryKey("DUMMY_SERIAL"));

        schema.addTable(dummy);
        Assert.assertTrue(schema.hasTable("DUMMIES"));
    }
}
