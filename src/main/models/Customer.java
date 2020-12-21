package main.models;

import java.sql.Timestamp;

/**
 * <h1>Customer</h1>
 * The Customer class is used as a data model for passing data from the MySQL customers table into the
 * table views in this program. This model is also used when building data for a customer record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Customer {
    private final int customerID;
    private final String customerName;
    private final String address;
    private final String postalCode;
    private final String phone;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int divisionID;
    public Customer(int customerID, String customerName, String address, String postalCode, String phone,
                    Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
    }

    /**
     * @return the customer id; mySQL = "Customer_ID" PK
     */
    public int getCustomerID() { return customerID; }

    /**
     * @return the customer name; mySQL = "Customer_Name"
     */
    public String getCustomerName() { return customerName; }

    /**
     * @return the address; mySQL = "Address"
     */
    public String getAddress() { return address; }

    /**
     * @return the postal code; mySQL = "Postal_Code"
     */
    public String getPostalCode() { return postalCode; }

    /**
     * @return the phone number; mySQL = "Phone"
     */
    public String getPhone() { return phone; }

    /**
     * @return the division ID; mySQL = "Division_ID" FK
     */
    public int getDivisionID() { return divisionID; }

    /**
     * @return the create date of the record; mySQL = "Create_Date"
     */
    public Timestamp getCreateDate() { return createDate; }

    /**
     * @return the user that created the record; mySQL = "Created_By"
     */
    public String getCreatedBy() { return createdBy; }

    /**
     * @return the last updated date of the record; mySQL = "Last_Update"
     */
    public Timestamp getLastUpdate() { return lastUpdate; }

    /**
     * @return the user that last updated the record; mySQL = "Last_Updated_By"
     */
    public String getLastUpdatedBy() { return lastUpdatedBy; }
}
