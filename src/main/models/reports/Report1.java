package main.models.reports;

/**
 * <h1>Report1: Count number of appointments for each customer by month and type</h1>
 * The Report1 class is used for the first report which returns a total count of the appointments for a specified
 * customer by month and then by type.
 * <p>
 * Class is given a generic "Report1" name for brevity and to allow easy modification if reports are going to be
 * altered or added onto later.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Report1 {
    private final String month;
    private final String type;
    private final int count;
    public Report1(String month, String type, int count) {
        this.month = month;
        this.type = type;
        this.count = count;
    }

    /**
     * @return the name of the month; mySQL = "Month" from appointments
     */
    public String getMonth() { return month; }

    /**
     * @return the type; mySQL = "Type" from appointments
     */
    public String getType() { return type; }

    /**
     * @return the count of the appointments; mySQL = "Count" from appointments
     */
    public int getCount() { return count; }
}
