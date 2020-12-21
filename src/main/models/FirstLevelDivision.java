package main.models;

/**
 * <h1>FirstLevelDivision</h1>
 * The FirstLevelDivision class is used as a data model for passing data from the MySQL first_level_divisions table
 * into the table views in this program. This model is also used when building data for a first level division record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class FirstLevelDivision {
    private final int divisionID;
    private final String divisionName;
    private final int countryID;
    public FirstLevelDivision(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * @return the division id; mySQL = "Division_ID" PK
     */
    public int getDivisionID() { return divisionID; }

    /**
     * @return the division name; mySQL = "Division_Name"
     */
    public String getDivisionName() { return divisionName; }

    /**
     * @return the country id associated with the division; mySQL = "Country_ID" FK
     */
    public int getCountryID() { return countryID; }
}
