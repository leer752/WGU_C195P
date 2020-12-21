package main.models;

import java.sql.Timestamp;

/**
 * <h1>Appointment</h1>
 * The Appointment class is used as a data model for passing data from the MySQL appointments table into the
 * table views in this program. This model is also used when building data for an appointment record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Appointment {
    private final int appointmentID;
    private final String title;
    private final String description;
    private final String location;
    private final String type;
    private final Timestamp start;
    private final Timestamp end;
    private final Timestamp createDate;
    private final String createdBy;
    private final Timestamp lastUpdate;
    private final String lastUpdatedBy;
    private final int customerID;
    private final int userID;
    private final String contactID;
    public Appointment(int appointmentID, String title, String description, String location, String type,
                       Timestamp start, Timestamp end, Timestamp createDate, String createdBy, Timestamp lastUpdate,
                       String lastUpdatedBy, int customerID, int userID, String contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * @return the appointment id; mySQL = "Appointment_ID" PK
     */
    public int getAppointmentID() { return appointmentID; }

    /**
     * @return the appointment title; mySQL = "Title"
     */
    public String getTitle() { return title; }

    /**
     * @return the appointment description; mySQL = "Description"
     */
    public String getDescription() { return description; }

    /**
     * @return the appointment location; mySQL = "Location"
     */
    public String getLocation() { return location; }

    /**
     * @return the appointment type; mySQL = "Type"
     */
    public String getType() { return type; }

    /**
     * @return the appointment start date and time; mySQL = "Start"
     */
    public Timestamp getStart() { return start; }

    /**
     * @return the appointment end date and time; mySQL = "End"
     */
    public Timestamp getEnd() { return end; }

    /**
     * @return the date that the record was created; mySQL = "Create_Date"
     */
    public Timestamp getCreateDate() { return createDate; }

    /**
     * @return the username of the user that created the record; mySQL = "Created_By"
     */
    public String getCreatedBy() { return createdBy; }

    /**
     * @return the date that the record was last updated; mySQL = "Last_Update"
     */
    public Timestamp getLastUpdate() { return lastUpdate; }

    /**
     * @return the username of the user that last updated the record; mySQL = "Last_Updated_By"
     */
    public String getLastUpdatedBy() { return lastUpdatedBy; }

    /**
     * @return the customer ID of the customer linked to the appointment; mySQL = "Customer_ID" FK
     */
    public int getCustomerID() { return customerID; }

    /**
     * @return the user ID of the user linked to the appointment; mySQL = "User_ID" FK
     */
    public int getUserID() { return userID; }

    /**
     * @return the contact ID of the contact linked to the appointment; mySQL = "Contact_ID" FK
     */
    public String getContactID() { return contactID; }
}
