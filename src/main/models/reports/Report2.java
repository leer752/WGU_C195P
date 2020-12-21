package main.models.reports;

/**
 * <h1>Report2: Schedule of appointment dates and times for each contact</h1>
 * The Report2 class is used for the second report which returns a schedule of the appointments for a specified contact
 * ordered by date.
 * <p>
 * Class is given a generic "Report2" name for brevity and to allow easy modification if reports are going to be
 * altered or added onto later.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Report2 {
    private final int contactID;
    private final String contactName;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final int appointmentID;
    private final String title;
    private final String desc;
    private final String type;
    public Report2(int contactID, String contactName, String date, String startTime, String endTime, int appointmentID,
                   String title, String desc, String type) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.appointmentID = appointmentID;
        this.title = title;
        this.desc = desc;
        this.type = type;
    }

    /**
     * @return the contact id; mySQL = "Contact_ID" PK from contacts
     */
    public int getContactID() { return contactID; }

    /**
     * @return the contact name; mySQL = "Contact_Name" from contacts
     */
    public String getContactName() { return contactName; }

    /**
     * @return the date; mySQL = "Start" from appointments
     */
    public String getDate() { return date; }

    /**
     * @return the start time; mySQL = "Start" from appointments
     */
    public String getStartTime() { return startTime; }

    /**
     * @return the end time; mySQL = "End" from appointments
     */
    public String getEndTime() { return endTime; }

    /**
     * @return the appointment id; mySQL = "Appointment_ID" PK from appointments
     */
    public int getAppointmentID() { return appointmentID; }

    /**
     * @return the appointment title; mySQL = "Title" from appointments
     */
    public String getTitle() { return title; }

    /**
     * @return the appointment description; mySQL = "Description" from appointments
     */
    public String getDesc() { return desc; }

    /**
     * @return the appointment type; mySQL = "Type" from appointments
     */
    public String getType() { return type; }
}