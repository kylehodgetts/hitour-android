package stranders.hitour.database.schema;

/**
 * A data type in the database
 */
public enum DataType {
    Text("TEXT");

    private String type;

    /**
     * Constructor for this enum, takes the text of how the given datatype is declared in the database
     * @param type The text how the datatype is presented in the database
     */
    DataType(String type) {
        this.type = type;
    }

    /**
     * Getter for the text how the datatype is presented in the database
     * @return The text how the datatype is presented in the database
     */
    public String getType() {
        return type;
    }
}
