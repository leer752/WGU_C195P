package main.models;

/**
 * <h1>Country</h1>
 * The Country class is used as a data model for passing data from the MySQL countries table into the
 * table views in this program. This model is also used when building data for a country record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Country {
    private final int countryID;
    private final String countryName;
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * @return the country id; mySQL = "Country_ID" PK
     */
    public int getCountryID() { return countryID; }

    /**
     * @return the country name; mySQL = "Country_Name"
     */
    public String getCountryName() { return countryName; }
}
