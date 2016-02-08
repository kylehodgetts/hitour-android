package uk.ac.kcl.stranders.hitour.database;

/**
 * @version 1.0
 * An exception that is thrown when a request is being made to database, but it does not correspond
 * to the schema that have been defined on the database creation
 */
public class NotInSchemaException extends Exception {

    public NotInSchemaException() {
        super("Malformed request with respect to databaseSchema");
    }
}
