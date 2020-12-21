package main.models.reports;

import java.sql.Timestamp;

/**
 * <h1>Report3: Record updates performed by the user in the current session</h1>
 * The Report3 class is used for the third report which returns a list of record updates performed by the user since
 * they logged in for the current session.
 * <p>
 * Note: If the user's changes are completely overwritten on the record (i.e. Last_Updated_By is overwritten), then it
 * won't show up in this report.
 * <p>
 * Class is given a generic "Report3" name for brevity and to allow easy modification if reports are going to be
 * altered or added onto later.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Report3 {
    private final String username;
    private final String recordType;
    private final int recordID;
    private final Timestamp time;
    public Report3(String username, String recordType, int recordID, Timestamp time) {
        this.username = username;
        this.recordType = recordType;
        this.recordID = recordID;
        this.time = time;
    }

    /**
     * @return the username of the current user; mySQL = "User_Name" UNIQUE from users
     */
    public String getUsername() { return username; }

    /**
     * @return the type of the record that was updated; mySQL = "File_Type" from appointments or customers
     */
    public String getRecordType() { return recordType; }

    /**
     * @return the id of the record that was updated; mySQL = "File_ID" from appointments or customers
     */
    public int getRecordID() { return recordID; }

    /**
     * @return the timestamp that the record was updated; mySQL = "Time" from appointments or customers
     */
    public Timestamp getTime() { return time; }
}
