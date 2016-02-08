package uk.ac.kcl.stranders.hitour.database.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * An abstraction over the database schema for sqllite database
 */
public class DatabaseSchema {

    private Map<String, TableSchema> tables;
    private String databaseName;
    private int version;

    /**
     * Constructor for the database schema, gives the database a name and current verison
     * @param databaseName  The name of the database
     * @param version       The version of the database
     */
    public DatabaseSchema(String databaseName, int version) {
        this.databaseName = databaseName;
        this.version = version;
        this.tables = new HashMap<>();
    }

    /**
     * Adds a table to the database
     * @param table A table to be added
     */
    public void addTable(TableSchema table){
        tables.put(table.getTableName(),table);
    }

    /**
     * A getter for all the tables in this database
     * @return A collection of tables in this database
     */
    public Collection<TableSchema> getTables() {
        return tables.values();
    }

    /**
     * A getter for the name of the database
     * @return The name of the database
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * A getter for the version of the database
     * @return The version of the database
     */
    public int getVersion() {
        return version;
    }

    /**
     * A check whether a given table has a given column
     * @param tableName The name of the table that should have a given column
     * @param column    The name of the column that should be in the table
     * @return          Boolean whether the table contains the column
     */
    public boolean hasColumn(String tableName, String column) {
        TableSchema table = tables.get(tableName);
        return table != null && table.hasColumn(column);
    }

    /**
     * A check whether the database has a given table
     * @param tableName The name of the table that should be in the database
     * @return          Boolean whether the database has the table
     */
    public boolean hasTable(String tableName){
        return tables.containsKey(tableName);
    }

    /**
     * A getter for specific table in the database
     * @param tableName The name of the desired table
     * @return          The desired table or null if the table does not exist
     */
    public TableSchema getTable(String tableName){
        return tables.get(tableName);
    }
}
