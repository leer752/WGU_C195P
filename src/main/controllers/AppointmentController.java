package main.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.util.*;
import main.models.*;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <h1>AppointmentController</h1>
 * The AppointmentController is responsible for the appointment form pane of the scheduling program.
 * <p>
 * This form allows a user to add or update an appointment depending on if they clicked "add" or "update" on the main view.
 * <p>
 * The fields for the appointment form include:
 * <ul>
 * <li>Integer ID: the appointment ID; disabled as it is set by MySQL; MySQL = "Appointment_ID" PK</li>
 * <li>String Title: the appointment title; MySQL = "Title"</li>
 * <li>String Desc: the appointment description; uses a TextArea; MySQL = "Description"</li>
 * <li>String Location: the appointment location; MySQL = "Location"</li>
 * <li>String Type: the appointment type; MySQL = "Type"</li>
 * <li>DateTime Date: the appointment date (NOT time); combined with Start Time and End Time to make timestamps for MySQL</li>
 * <li>String Start Time: the appointment start time in "hh:mm" format;
 * combined with a combo box that has the period (AM or PM) and then with the date; MySQL = "Start"</li>
 * <li>String End Time: the appointment end time in "hh:mm" format;
 * combined with a combo box that has the period (AM or PM) and then with the date; MySQL = "Start"</li>
 * <li>Integer Customer ID: the customer ID associated with the appointment, selected via a ComboBox;
 * MySQL = "Customer_ID" FK from customers</li>
 * <li>Integer Contact ID: the contact ID associated with the appointment, selected via a ComboBox;
 * MySQL = "Contact_ID" FK from contacts</li>
 * </ul>
 * <p>
 * Appointment data will be validated and UPDATE the MySQL database upon pressing the "Save" button.
 * <p>
 * Additional fields (Create_Date, Created_By, Last_Update, Last_Updated_By, and User_ID) are generated automatically.
 *
 * @author Lee Rhodes
 */
public class AppointmentController {
    private Session session;
    private final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "appointment_"+ Locale.getDefault().getLanguage());
    private String action;
    private Appointment existingAppointment;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private Label userLabel;
    @FXML
    private TextField idField;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descTextArea;
    @FXML
    private TextField locationField;
    @FXML
    private TextField typeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private ComboBox<String> startTimeComboBox;
    @FXML
    private TextField endTimeField;
    @FXML
    private ComboBox<String> endTimeComboBox;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    /**
     * initialize() is responsible for populating the appointment form pane with any existing data if it's an update,
     * and sets up all ComboBoxes and listeners.
     */
    public void initialize() {
        Platform.runLater(() -> {
            initializeDisplayText();
            buildComboBoxes();
            setCharLimitOnFields();
            if (action.equals(Constants.UPDATE)) {
                fillExistingAppointment();
            }
        });
    }

    /**
     * initializeDisplayText() pulls from properties to display all text in the system's language. Currently,
     * English (en) and French (fr) are the only two languages supported.
     */
    private void initializeDisplayText() {
        // Title and field labels
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.setTitle(rb.getString("windowTitle"));
        if (action.equals(Constants.ADD)) {
            formTitleLabel.setText(rb.getString("addTitleLabel"));
        } else {
            formTitleLabel.setText(rb.getString("updateTitleLabel"));
        }
        idLabel.setText(rb.getString("idLabel"));
        titleLabel.setText(rb.getString("titleLabel"));
        descLabel.setText(rb.getString("descLabel"));
        locationLabel.setText(rb.getString("locationLabel"));
        typeLabel.setText(rb.getString("typeLabel"));
        dateLabel.setText(rb.getString("dateLabel"));
        startLabel.setText(rb.getString("startLabel"));
        endLabel.setText(rb.getString("endLabel"));
        customerLabel.setText(rb.getString("customerLabel"));
        contactLabel.setText(rb.getString("contactLabel"));
        userLabel.setText(rb.getString("userLabel"));

        // Prompt text
        idField.setPromptText(rb.getString("idPrompt"));
        titleField.setPromptText(rb.getString("titlePrompt"));
        descTextArea.setPromptText(rb.getString("descPrompt"));
        locationField.setPromptText(rb.getString("locationPrompt"));
        typeField.setPromptText(rb.getString("typePrompt"));
        datePicker.setPromptText(rb.getString("datePrompt"));
        startTimeField.setPromptText(rb.getString("timePrompt"));
        startTimeComboBox.setPromptText(rb.getString("timeComboBoxPrompt"));
        endTimeField.setPromptText(rb.getString("timePrompt"));
        endTimeComboBox.setPromptText(rb.getString("timeComboBoxPrompt"));
        customerComboBox.setPromptText(rb.getString("customerPrompt"));
        contactComboBox.setPromptText(rb.getString("contactPrompt"));
        userComboBox.setPromptText(rb.getString("userPrompt"));

        // Button labels
        Common.scaleButton(saveBtn, rb.getString("saveBtn"));
        Common.scaleButton(cancelBtn, rb.getString("cancelBtn"));

        // Error label; initially blank
        errorLabel.setText("");

        // Convert date string that shows in the date picker to fit local format
        datePicker.setConverter(new StringConverter<>() {
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(rb.getString("datePattern"));
            @Override
            public String toString(LocalDate localDate) {
                if (localDate != null) {
                    return dtf.format(localDate);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if ( string != null && !string.isEmpty() ) {
                    return LocalDate.parse(string, dtf);
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * buildComboBoxes() calls each method responsible for setting up one of the ComboBoxes on the appointment form.
     */
    private void buildComboBoxes() {
        buildTimeComboBoxes();
        buildCustomerComboBox();
        buildContactComboBox();
        buildUserComboBox();
    }

    /**
     * buildTimeComboBoxes() sets up the ComboBox responsible for showing the two time periods, AM and PM.
     */
    private void buildTimeComboBoxes() {
        ObservableList<String> periods = FXCollections.observableArrayList("AM", "PM");
        startTimeComboBox.setItems(periods);
        endTimeComboBox.setItems(periods);
    }

    /**
     * buildCustomerComboBox() calls a query to build a list of all customers in the MySQL database. These customers
     * are used to populate the choices for the Customer ComboBox which is used for the user to select which customer
     * to associate with the appointment.
     * <p>
     * For display, the customer's name is shown.
     * <p>
     * This customer is used to limit appointments as a customer cannot have overlapping appointments.
     * <p>
     * Pulls from MySQL customers table.
     */
    private void buildCustomerComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCustomersByName.sql"),
                Collections.singletonList(Constants.WILDCARD));

        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try {
            if ( rs != null ) {
                while (rs.next()) {
                    int customerID = rs.getInt("Customer_ID");
                    String customerName = rs.getString("Customer_Name");
                    String address = rs.getString("Address");
                    String postalCode = rs.getString("Postal_Code");
                    String phone = rs.getString("Phone");
                    Timestamp createDate = rs.getTimestamp("Create_Date");
                    String createdBy = rs.getString("Created_By");
                    Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                    String lastUpdatedBy = rs.getString("Last_Updated_By");
                    int divisionID = rs.getInt("Division_ID");
                    customers.add(new Customer(customerID, customerName, address, postalCode, phone, createDate,
                            createdBy, lastUpdate, lastUpdatedBy, divisionID));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        customerComboBox.setItems(customers);

        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer item) {
                return item.getCustomerName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
    }

    /**
     * buildContactComboBox() calls a query to build a list of all contacts in the MySQL database. These contacts
     * are used to populate the choices for the Contact ComboBox which is used for the user to select which contact
     * to associate with the appointment.
     * <p>
     * For display, the contact's name is shown.
     * <p>
     * Pulls from MySQL contacts table.
     */
    private void buildContactComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectContactByID.sql"),
                Collections.singletonList(Constants.WILDCARD));

        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        try {
            if ( rs != null ) {
                while (rs.next()) {
                    int contactID = rs.getInt("Contact_ID");
                    String contactName = rs.getString("Contact_Name");
                    contacts.add(new Contact(contactID, contactName));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        contactComboBox.setItems(contacts);

        contactComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Contact item) {
                return item.getContactName();
            }

            @Override
            public Contact fromString(String string) {
                return null;
            }
        });
    }

    /**
     * buildUserComboBox() calls a query to build a list of all users in the MySQL database. These users
     * are used to populate the choices for the UserComboBox which is used for the user to select which user
     * to associate with the appointment.
     * <p>
     * For display, the username is shown.
     * <p>
     * Pulls from MySQL users table.
     */
    private void buildUserComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectUserByUsername.sql"),
                Collections.singletonList(Constants.WILDCARD));

        ObservableList<User> users = FXCollections.observableArrayList();
        try {
            if ( rs != null ) {
                while (rs.next()) {
                    int userID = rs.getInt("User_ID");
                    String username = rs.getString("User_Name");
                    users.add(new User(userID, username));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        userComboBox.setItems(users);

        userComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User item) {
                return item.getUsername();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    /**
     * setCharLimitOnFields() sends each TextField/TextArea and its maximum length to the Common method setCharLimit().
     * That method adds a listener to the field that limits how many characters the user can enter.
     * <p>
     * Prevents a value being entered that's longer than the MySQL field length.
     */
    private void setCharLimitOnFields() {
        Common.setCharLimit(titleField, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(descTextArea, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(locationField, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(typeField, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(startTimeField,Constants.CHAR_LIMIT_SMALL);
        Common.setCharLimit(endTimeField, Constants.CHAR_LIMIT_SMALL);
    }

    /**
     * fillExistingAppointment() takes any existing appointment data that was passed into the view and populates the
     * form with the data. This is only used when updating an existing appointment.
     */
    private void fillExistingAppointment() {
        idField.setText(String.valueOf(existingAppointment.getAppointmentID()));
        titleField.setText(existingAppointment.getTitle());
        descTextArea.setText(existingAppointment.getDescription());
        locationField.setText(existingAppointment.getLocation());
        typeField.setText(existingAppointment.getType());

        Timestamp start = existingAppointment.getStart();
        Timestamp end = existingAppointment.getEnd();

        datePicker.setValue(start.toLocalDateTime().toLocalDate());

        // Need to split up time as AM/PM is handled in a ComboBox and the text needs to be in "h:mm" format.
        String[] startSplit = DateTime.UTCTimestampToTimeDisplay(start).split("\\s+");
        String[] endSplit = DateTime.UTCTimestampToTimeDisplay(end).split("\\s+");
        startTimeField.setText(startSplit[0]);
        startTimeComboBox.getSelectionModel().select(startSplit[1]);
        endTimeField.setText(endSplit[0]);
        endTimeComboBox.getSelectionModel().select(endSplit[1]);

        customerComboBox.getSelectionModel().select(getCustomerByID(existingAppointment.getCustomerID()));
        contactComboBox.getSelectionModel().select(getContactByID(existingAppointment.getContactID()));
        userComboBox.getSelectionModel().select(getUserByID(existingAppointment.getUserID()));

    }

    /**
     * getCustomerByID() sends a query to the MySQL database to find and return data for a specific customer from the
     * customers table according to the passed in Customer_ID PK.
     * <p>
     * This customer is used to fill in the CustomerComboBox and is only used when updating an existing appointment.
     *
     * @param customerID the customer ID key to search for the desired customer in the MySQL database
     * @return the customer found by the query
     */
    private Customer getCustomerByID(int customerID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCustomersByID.sql"),
                Collections.singletonList(String.valueOf(customerID)));

        try {
            if ( rs != null ) {
                rs.next();

                int id = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                Timestamp createDate = rs.getTimestamp("Create_Date");
                String createdBy = rs.getString("Created_By");
                Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                String lastUpdatedBy = rs.getString("Last_Updated_By");
                int divisionID = rs.getInt("Division_ID");

                return new Customer(id, customerName, address, postalCode, phone, createDate,
                        createdBy, lastUpdate, lastUpdatedBy, divisionID);
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return null;

    }

    /**
     * getContactByID() sends a query to the MySQL database to find and return data for a specific contact from the
     * contacts table according to the passed-in Contact_ID PK.
     * <p>
     * This contact is used to fill in the ContactComboBox and is only used when updating an existing appointment.
     *
     * @param contactID the contact ID key to search for the desired contact in the MySQL database
     * @return the contact found by the query
     */
    private Contact getContactByID(String contactID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectContactByID.sql"),
                Collections.singletonList(contactID));

        try {
            if ( rs != null ) {
                rs.next();

                int id = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");

                return new Contact(id, contactName);
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return null;
    }

    /**
     * getUserByID() sends a query to the MySQL database to find and return data for a specific user from the
     * users table according to the passed-in User_ID PK.
     * <p>
     * This user is used to fill in the UserComboBox and is only used when updating an existing appointment.
     *
     * @param userID the user ID key to search for the desired user in the MySQL database
     * @return the user found by the query
     */
    private User getUserByID(int userID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectUserByID.sql"),
                Collections.singletonList(String.valueOf(userID)));

        try {
            if ( rs != null ) {
                rs.next();

                int id = rs.getInt("User_ID");
                String username = rs.getString("User_Name");

                return new User(id, username);
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return null;
    }

    /**
     * addUpdateBtn() is called whenever the "Save" button is pressed on the appointment form pane.
     * <p>
     * First, it uses isInputValid() to make sure all fields fit business rules and expected data types.
     * Next, it calls either addAppointment() or updateAppointment() depending on which action was selected
     * from the main menu. Finally, it exits the appointment form pane and returns to main menu,
     * which will show data updates.
     */
    public void addUpdateBtn() {
        if (isInputValid()) {
            if (action.equals(Constants.ADD)) {
                addAppointment(createAppointmentData());
            } else {
                updateAppointment(createAppointmentData());
            }
            cancel();
        }
    }

    /**
     * isInputValid() checks all input in the appointment form's text fields to ensure they are valid according to
     * business rules and expected data types.
     * <p>
     * These requirements include:
     * <ul>
     * <li>No field can be empty (Checked in noFieldsEmpty())</li>
     * <li>Date and time must be valid (Checked in isValidDateTime())</li>
     * </ul>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     *
     * @return whether or not the input is valid according to business rules and expected data types.
     */
    public boolean isInputValid() {
        if ( !noFieldsEmpty() ) {
            return false;
        }

        if ( !isValidDateTime() ) {
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    /**
     * noFieldsEmpty() checks to be sure that every single input field has data in it; nothing should be empty or null.
     * <p>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     * @return whether or not every field had data
     */
    private boolean noFieldsEmpty() {
        if ( titleField.getText().isEmpty()
                || descTextArea.getText().isEmpty()
                || locationField.getText().isEmpty()
                || typeField.getText().isEmpty()
                || datePicker.getConverter().toString().isEmpty()
                || startTimeField.getText().isEmpty()
                || startTimeComboBox.getSelectionModel().getSelectedItem() == null
                || endTimeField.getText().isEmpty()
                || endTimeComboBox.getSelectionModel().getSelectedItem() == null
                || contactComboBox.getSelectionModel().getSelectedItem() == null
                || customerComboBox.getSelectionModel().getSelectedItem() == null
                || userComboBox.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText(rb.getString("fieldBlank"));
            return false;
        }
        return true;
    }

    /**
     * isValidDateTime() checks the date and time fields to ensure they are valid according to business rules and
     * expected format.
     * <p>
     * These requirements include:
     * <ul>
     * <li>The date and time are in the necessary format (date format is controlled by date picker and time is
     * checked for "h:mm" format)</li>
     * <li>The start time is before the end time</li>
     * <li>The time is inbetween valid business hours (checked in betweenBusinessHours())</li>
     * <li>The date and time do not conflict with any other existing appointments in the MySQL database that
     * have the same customer associated with it</li>
     * </ul>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     *
     * @return whether or not the date and time was valid
     */
    private boolean isValidDateTime() {
        String dateStr = datePicker.getValue().format(DateTimeFormatter.ofPattern(rb.getString("datePattern")));
        String startStr = startTimeField.getText() + " " + startTimeComboBox.getSelectionModel().getSelectedItem();
        String endStr = endTimeField.getText() + " " + endTimeComboBox.getSelectionModel().getSelectedItem();
        ZonedDateTime start = DateTime.displayToZoned(dateStr, startStr);
        ZonedDateTime end = DateTime.displayToZoned(dateStr, endStr);

        if ( start == null || end == null ) {
            errorLabel.setText(rb.getString("incorrectTimeFormat"));
            return false;
        }

        if ( start.isAfter(end) ) {
            errorLabel.setText(rb.getString("startAfterEnd"));
            return false;
        }

        if (!betweenBusinessHours(dateStr, start, end)) {
            errorLabel.setText(rb.getString("outsideBusinessHours"));
            return false;
        }

        String customerID = String.valueOf(customerComboBox.getSelectionModel().getSelectedItem().getCustomerID());
        String contactID = String.valueOf(contactComboBox.getSelectionModel().getSelectedItem().getContactID());
        if (appointmentOverlaps(customerID, contactID, start, end)) {
            errorLabel.setText(rb.getString("overlappingAppointment"));
            return false;
        }

        return true;
    }

    /**
     * betweenBusinessHours() checks that the given date and time range for the appointment does not go
     * outside of business hours. For now, this is between 8 a.m. to 10 p.m. EST.
     *
     * @param dateStr the date of the appointment; used to determine whether daylight savings is needed
     * @param start the start time of the appointment
     * @param end the end time of the appointment
     * @return whether or not the time was between business hours
     */
    private boolean betweenBusinessHours(String dateStr, ZonedDateTime start, ZonedDateTime end) {
        ZonedDateTime businessStart = DateTime.localToBusinessTimeZone(start);
        ZonedDateTime businessEnd = DateTime.localToBusinessTimeZone(end);
        ZonedDateTime startBound = DateTime.displayToZoned(dateStr, Constants.START_TIME_BOUNDARY);
        ZonedDateTime endBound = DateTime.displayToZoned(dateStr, Constants.END_TIME_BOUNDARY);

        return (!businessStart.isBefore(startBound) && !businessStart.isAfter(endBound))
                && (!businessEnd.isBefore(startBound) && !businessEnd.isAfter(endBound));
    }

    /**
     * appointmentOverlaps() checks that the given date and time range for the appointment does not overlap
     * with any other existing appointments for the selected customer or contact.
     *
     * @param customerID the customer selected on the form
     * @param contactID the contact selected on the form
     * @param start the start date/time of the appointment
     * @param end the end date/time of the appointment
     * @return whether or not any appointments were found that overlap
     */
    private boolean appointmentOverlaps(String customerID, String contactID, ZonedDateTime start,
                                        ZonedDateTime end) {
        String startTS = Objects.requireNonNull(DateTime.localToUTCTimestamp(start)).toString();
        String endTS = Objects.requireNonNull(DateTime.localToUTCTimestamp(end)).toString();

        String appointmentID;
        if ( existingAppointment != null) {
            appointmentID = String.valueOf(existingAppointment.getAppointmentID());
        } else {
            appointmentID = "-1";
        }

        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectOverlappingAppointments.sql"),
                Arrays.asList(appointmentID, customerID, contactID, startTS, startTS, endTS, endTS, startTS, endTS, startTS, endTS)
        );

        try {
            if (rs != null) {
                return rs.next();
            }
            return false;
        } catch (Exception e) {
            Common.handleException(e);
            return true;
        }
    }

    /**
     * addAppointment() executes an update using the DatabaseConnection class that INSERTS a new appointment into the
     * appointments table in the MySQL database. All data will be validated by the time this is called.
     *
     * @param appointment the appointment data to insert
     */
    private void addAppointment(Appointment appointment) {
        List<String> args = Arrays.asList(
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getLocation(),
                appointment.getType(),
                appointment.getStart().toString(),
                appointment.getEnd().toString(),
                appointment.getCreateDate().toString(),
                appointment.getCreatedBy(),
                appointment.getLastUpdate().toString(),
                appointment.getLastUpdatedBy(),
                String.valueOf(appointment.getCustomerID()),
                String.valueOf(appointment.getUserID()),
                String.valueOf(appointment.getContactID())
        );
        DatabaseConnection.performUpdate(
                session.getConn(),
                Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "InsertAppointment.sql"),
                args
        );
    }

    /**
     * updateAppointment() executes an update using the DatabaseConnection class that UPDATES an existing appointment
     * in the appointments table in the MySQL database. All data will be validated by the time this is called.
     *
     * @param appointment the appointment data to update
     */
    private void updateAppointment(Appointment appointment) {
        List<String> args = Arrays.asList(
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getLocation(),
                appointment.getType(),
                appointment.getStart().toString(),
                appointment.getEnd().toString(),
                appointment.getLastUpdate().toString(),
                appointment.getLastUpdatedBy(),
                String.valueOf(appointment.getCustomerID()),
                String.valueOf(appointment.getContactID()),
                String.valueOf(appointment.getAppointmentID())
        );
        DatabaseConnection.performUpdate(
                session.getConn(),
                Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "UpdateAppointment.sql"),
                args);
    }

    /**
     * createAppointmentData() inserts all the validated data from the form's fields (and the existing appointment's
     * data in the case of an update) into an Appointment object that will be used to update the MySQL database.
     *
     * @return the Appointment object created from the fields' data
     */
    private Appointment createAppointmentData() {
        int appointmentID = -1;
        Timestamp createDate = DateTime.getUTCTimestampNow();
        String createdBy = session.getUsername();

        if (action.equals(Constants.UPDATE)) {
            appointmentID = existingAppointment.getAppointmentID();
            createDate = existingAppointment.getCreateDate();
            createdBy = existingAppointment.getCreatedBy();
        }

        Timestamp lastUpdate = DateTime.getUTCTimestampNow();
        String lastUpdatedBy = session.getUsername();

        String title = titleField.getText();
        String desc = descTextArea.getText();
        String location = locationField.getText();
        String type = typeField.getText();

        String dateStr = datePicker.getValue().format(DateTimeFormatter.ofPattern(rb.getString("datePattern")));
        String startStr = startTimeField.getText() + " " + startTimeComboBox.getSelectionModel().getSelectedItem();
        String endStr = endTimeField.getText() + " " + endTimeComboBox.getSelectionModel().getSelectedItem();
        ZonedDateTime startZoned = DateTime.displayToZoned(dateStr, startStr);
        ZonedDateTime endZoned = DateTime.displayToZoned(dateStr, endStr);
        Timestamp start = DateTime.localToUTCTimestamp(startZoned);
        Timestamp end = DateTime.localToUTCTimestamp(endZoned);

        int customerID = customerComboBox.getSelectionModel().getSelectedItem().getCustomerID();
        String contactID = String.valueOf(contactComboBox.getSelectionModel().getSelectedItem().getContactID());
        int userID = userComboBox.getSelectionModel().getSelectedItem().getUserID();

        return new Appointment(appointmentID, title, desc, location, type, start, end, createDate, createdBy,
                lastUpdate, lastUpdatedBy, customerID, userID, contactID);
    }

    /**
     * setSession() is used to pass in session data from the main view. This session data includes the open connection
     * to the database so that updates are possible, current user information, and the log-in timestamp.
     *
     * @param session the session to set
     */
    public void setSession(Session session) { this.session = session; }

    /**
     * setAction() is used to pass in the selected action from the main view. This will either be an ADD or UPDATE.
     *
     * @param action the action to set
     */
    public void setAction(String action) { this.action = action; }

    /**
     * setExistingAppointment() is used to pass in existing appointment data from the main view in the case of an
     * update.
     *
     * @param existingAppointment the existing appointment to set
     */
    public void setExistingAppointment(Appointment existingAppointment) { this.existingAppointment = existingAppointment; }

    /**
     * cancel() listens for when the "Cancel" button is pressed or is called to close the form after updates.
     * If this is called from the button, then no changes are made to the database.
     */
    public void cancel() { Common.exitWindow(cancelBtn); }
}
