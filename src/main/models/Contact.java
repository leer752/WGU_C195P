package main.models;

/**
 * <h1>Contact</h1>
 * The Contact class is used as a data model for passing data from the MySQL contacts table into the
 * table views in this program. This model is also used when building data for a contact record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Contact {
    private final int contactID;
    private final String contactName;
    public Contact(int contactID, String contactName) {
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * @return the contact id; mySQL = "Contact_ID" PK
     */
    public int getContactID() { return contactID; }

    /**
     * @return the contact name; mySQL = "Contact_Name"
     */
    public String getContactName() { return contactName; }
}
