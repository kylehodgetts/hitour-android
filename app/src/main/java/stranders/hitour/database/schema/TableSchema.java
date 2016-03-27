package stranders.hitour.database.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * An abstraction over the schema for a table in the sql database
 */
public class TableSchema {

    private String tableName;
    private Map<String, DataType> columns;
    private List<String> primaryKeys;

    /**
     * Constructor for a schema in the sql database
     * @param primaryKey The primary key for this table
     * @param tableName  The name of this table
     */
    public TableSchema(String primaryKey, String tableName) {
        this.primaryKeys = new ArrayList<>();
        this.tableName = tableName;
        this.columns = new HashMap<>();
        addPrimaryKey(primaryKey);
    }

    /**
     * Adds a column with particular name and data type to the schema
     * @param name  The name of the column
     * @param type  The data type of the column
     */
    public void addValue(String name, DataType type) {
        columns.put(name, type);
    }

    /**
     * Adds additional primary key, creating a composite primary key, the data type of this key will be TEXT
     * @param primaryKey New primary key to be added, data type of this string will be text
     */
    public void addPrimaryKey(String primaryKey){
        primaryKeys.add(primaryKey);
    }

    /**
     * Gets a name of the table
     * @return The name of the table
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns a list of primary keys for this table
     * @return List of primary keys
     */
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Returns a list of all columns in this table, not counting the primary keys
     * @return Map of column name -> column data type
     */
    public Map<String, DataType> getColumns() {
        return columns;
    }

    /**
     * Checks whether the table has given column, not counting the primary keys
     * @param column The column to be checked
     * @return Boolean whether has the column
     */
    public boolean hasColumn(String column){
        return columns.containsKey(column);
    }

    /**
     * Checks whether the table has given primary key
     * @param key The key to be checked
     * @return Boolean whether the table has the key
     */
    public boolean hasPrimaryKey(String key){
        return primaryKeys.contains(key);
    }

    /**
     * Creates a create table sql statement for this table
     * @return SQL create table command
     */
    public String getSQLCreateTable(){
        String valueString = "";
        for (Map.Entry<String, DataType> stringDataTypeEntry : columns.entrySet()) {
           valueString += ", "+stringDataTypeEntry.getKey() + " " + stringDataTypeEntry.getValue().getType();
        }


        return "CREATE TABLE " + tableName + " (" + primaryList(" TEXT, ",2) + valueString + ", PRIMARY KEY("+ primaryList(", ",2)+"))";
    }

    /**
     * Creates a drop table sql statement for this table
     * @return SQL drop table command
     */
    public String dropTable(){
        return "DROP TABLE IF EXISTS " + tableName;
    }

    /**
     * Constructs a where query for this table, that matches against its primary keys
     * @return Where query that matches against primary keys
     */
    public String wherePrimary(){
        return primaryList("=? AND ",5);
    }

    /**
     * Constructs a String containing all primary keys that are inter-spaced with a given string,
     * last n number of characters is removed
     * @param spacing The string to be used while inter-spacing
     * @param remlast The number of characters to be removed at the end
     * @return correct String
     */
    private String primaryList(String spacing, int remlast) {
        String res = "";
        for (String primaryKey : primaryKeys) {
            res += primaryKey+spacing;
        }
        return res.substring(0,res.length()-(remlast));
    }
}