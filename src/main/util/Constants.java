package main.util;

import java.sql.Timestamp;
import java.time.ZoneId;


/**
 * <h1>Constants</h1>
 * Constants class is used to hold constants for the project. Ideally, these are to avoid magic variables that
 * would otherwise be littered in the code and provide a single point of editing.
 *
 * @author Lee Rhodes
 */
public final class Constants {
    private Constants() { }

    // Path constants; concatenated with the file name
    public static final String SCRIPT_PATH_BASE = "src/main/scripts/";
    public static final String QUERY_SCRIPT_PATH_BASE = SCRIPT_PATH_BASE + "queries/";
    public static final String UPDATE_SCRIPT_PATH_BASE = SCRIPT_PATH_BASE + "updates/";
    public static final String REPORT_SCRIPT_PATH_BASE = SCRIPT_PATH_BASE + "reports/";
    public static final String FXML_PATH_BASE = "/main/resources/fxml/";
    public static final String PROPERTIES_PATH_BASE = "main.resources.properties.";
    public static final String LOGIN_ACTIVITY_PATH= "login_activity.txt";

    // Constants used when logging in and logging activity
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String LOGIN_SUCCESS_MSG = "Logged in successfully.";
    public static final String USER_NOT_FOUND_MSG = "User not found.";
    public static final String WRONG_PASSWORD_MSG = "Password incorrect.";

    // Character limits for mySQL fields that have been observed
    public static final int CHAR_LIMIT_SMALL = 5; // Specifically for time input fields "hh:mm"
    public static final int CHAR_LIMIT_NORMAL = 50;
    public static final int CHAR_LIMIT_LARGE = 100;

    // Variables used for default mySQL inputs
    public static final String MIN_DATE = Timestamp.valueOf("1700-01-01 00:00:00").toString();
    public static final String MAX_DATE = Timestamp.valueOf("4000-12-31 00:00:00").toString();
    public static final String WILDCARD = "%";

    // Defines processes to take depending on how the user wants to update the record
    public static final String ADD = "add";
    public static final String UPDATE = "update";

    // Extra constants for commonly-used words
    public static final String NAME = "Name";
    public static final String ID = "ID";

    // Variables for formatting and decisions regarding date and time variables
    public static final int APPOINTMENT_WITHIN_MINUTES = 15;
    public static final int DATE_PAGING_INCREMENT = 1;
    public static final String START_TIME_BOUNDARY = "08:00 AM";
    public static final String END_TIME_BOUNDARY = "10:00 PM";
    public static final String TIME_PATTERN = "h:mm a";
    public static final String ZONE_PATTERN = "z";
    public static final ZoneId BUSINESS_ZONE_ID = ZoneId.of("America/New_York");
    public static final ZoneId UNIVERSAL_ZONE_ID = ZoneId.of("Universal");

    // Variable used for "SELECT ALL" choices in reports
    public static final int REPORT_SELECT_ALL_INT = -1;
}
