package main.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.models.Contact;
import main.models.Customer;
import main.models.Session;
import main.models.reports.*;
import main.util.Common;
import main.util.Constants;
import main.util.DatabaseConnection;
import main.util.DateTime;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * <h1>ReportsController</h1>
 * The ReportsController is responsible for generating reports based on the MySQL database.
 * <p>
 * The supported reports include:
 * <ul>
 * <li>Report #1: The total number of customer appointments by type and month.
 * Pulls data from MySQL tables - customers and appointments.</li>
 * <li>Report #2: The The appointment schedule for each contact in the database.
 * Pulls data from MySQL tables - contacts and appointments.</li>
 * <li>Report #3: The current user's session history for record changes, provided their changes haven't been completely overwritten by another user since logging in.
 * Pulls data from MySQL tables - users, customers, and appointments</li>
 * </ul>
 *
 * @author Lee Rhodes
 */
public class ReportsController {
    private Session session;
    private final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "reports_" + Locale.getDefault().getLanguage());
    @FXML
    private Label titleLabel;
    @FXML
    private Tab report1Tab;
    @FXML
    private Tab report2Tab;
    @FXML
    private Tab report3Tab;
    @FXML
    private Label report1DescTitleLabel;
    @FXML
    private Label report1DescLabel;
    @FXML
    private ComboBox<Customer> report1ComboBox;
    @FXML
    private Button report1GenerateReportBtn;
    @FXML
    private TableView<Report1> report1TableView;
    @FXML
    private TableColumn<Report1, String> report1MonthCol;
    @FXML
    private TableColumn<Report1, String> report1TypeCol;
    @FXML
    private TableColumn<Report1, Integer> report1CountCol;
    @FXML
    private Label report1ErrorLabel;
    @FXML
    private Label report2DescTitleLabel;
    @FXML
    private Label report2DescLabel;
    @FXML
    private ComboBox<Contact> report2ComboBox;
    @FXML
    private Button report2GenerateReportBtn;
    @FXML
    private TableView<Report2> report2TableView;
    @FXML
    private TableColumn<Report2, Integer> report2ContactIDCol;
    @FXML
    private TableColumn<Report2, String> report2ContactNameCol;
    @FXML
    private TableColumn<Report2, Date> report2DateCol;
    @FXML
    private TableColumn<Report2, Time> report2StartTimeCol;
    @FXML
    private TableColumn<Report2, Time> report2EndTimeCol;
    @FXML
    private TableColumn<Report2, Integer> report2AppointmentIDCol;
    @FXML
    private TableColumn<Report2, String> report2TitleCol;
    @FXML
    private TableColumn<Report2, String> report2DescCol;
    @FXML
    private TableColumn<Report2, String> report2TypeCol;
    @FXML
    private Label report2ErrorLabel;
    @FXML
    private Label report3DescTitleLabel;
    @FXML
    private Label report3DescLabel;
    @FXML
    private Button report3GenerateReportBtn;
    @FXML
    private TableView<Report3> report3TableView;
    @FXML
    private TableColumn<Report3, String> report3UsernameCol;
    @FXML
    private TableColumn<Report3, String> report3RecordTypeCol;
    @FXML
    private TableColumn<Report3, Integer> report3RecordIDCol;
    @FXML
    private TableColumn<Report3, Timestamp> report3TimeCol;
    @FXML
    private Label report3ErrorLabel;
    @FXML
    private Button backBtn;

    /**
     * initialize() is responsible for populating the reports pane with display text and sets up all ComboBoxes that
     * will be used to filter report data with.
     */
    public void initialize() {
        Platform.runLater(() -> {
            initializeDisplayText();
            buildReport1ComboBox();
            buildReport2ComboBox();
        });
    }

    /**
     * initializeDisplayText() pulls from properties to display all text in the system's language. Currently,
     * English (en) and French (fr) are the only two languages supported.
     */
    private void initializeDisplayText() {
        // Text outside of reports pane
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.setTitle(rb.getString("windowTitle"));
        titleLabel.setText(rb.getString("windowTitle"));

        // Report 1 display text
        report1Tab.setText(rb.getString("report1Tab"));
        report1DescTitleLabel.setText(rb.getString("description"));
        report1DescLabel.setText(rb.getString("report1Desc"));
        report1ComboBox.setPromptText(rb.getString("report1ComboBoxPrompt"));
        report1MonthCol.setText(rb.getString("report1Month"));
        report1TypeCol.setText(rb.getString("report1Type"));
        report1CountCol.setText(rb.getString("report1Count"));

        // Report 2 display text
        report2Tab.setText(rb.getString("report2Tab"));
        report2DescTitleLabel.setText(rb.getString("description"));
        report2DescLabel.setText(rb.getString("report2Desc"));
        report2ComboBox.setPromptText(rb.getString("report2ComboBoxPrompt"));
        report2ContactIDCol.setText(rb.getString("report2ContactID"));
        report2ContactNameCol.setText(rb.getString("report2ContactName"));
        report2DateCol.setText(rb.getString("report2Date"));
        report2StartTimeCol.setText(rb.getString("report2StartTime"));
        report2EndTimeCol.setText(rb.getString("report2EndTime"));
        report2AppointmentIDCol.setText(rb.getString("report2AppointmentID"));
        report2TitleCol.setText(rb.getString("report2Title"));
        report2DescCol.setText(rb.getString("report2AppointmentDesc"));
        report2TypeCol.setText(rb.getString("report2Type"));

        // Report 3 display text
        report3Tab.setText(rb.getString("report3Tab"));
        report3DescTitleLabel.setText(rb.getString("description"));
        report3DescLabel.setText(rb.getString("report3Desc"));
        report3UsernameCol.setText(rb.getString("report3Username"));
        report3RecordTypeCol.setText(rb.getString("report3RecordType"));
        report3RecordIDCol.setText(rb.getString("report3RecordID"));
        report3TimeCol.setText(rb.getString("report3Time"));

        // Button labels
        Common.scaleButton(backBtn, rb.getString("back"));
        Common.scaleButton(report1GenerateReportBtn, rb.getString("generateReport"));
        Common.scaleButton(report2GenerateReportBtn, rb.getString("generateReport"));
        Common.scaleButton(report3GenerateReportBtn, rb.getString("generateReport"));
    }

    /**
     * buildReport1ComboBox() calls a query to build a list of all customers in the MySQL database. These customers
     * are used to populate the choices for the Report1ComboBox which is used for the user to select which customer
     * to run the report for.
     * <p>
     * For display, the customer's name is shown.
     * <p>
     * Pulls from MySQL customers table.
     * <p>
     * An initial field is added to the ComboBox with a Customer_ID of -1 and a Customer_Name of "ALL" so the user
     * has the choice to select ALL customers to run the report for.
     */
    private void buildReport1ComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCustomersByName.sql"),
                Collections.singletonList(Constants.WILDCARD));

        ObservableList<Customer> customers = FXCollections.observableArrayList();
        customers.add(new Customer(-1,"ALL",null,null,null,null,null,null,null,-1));
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

        report1ComboBox.setItems(customers);

        report1ComboBox.setConverter(new StringConverter<>() {
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
     * buildReport2ComboBox() calls a query to build a list of all contacts in the MySQL database. These contacts
     * are used to populate the choices for the Report2ComboBox which is used for the user to select which contact
     * to run the report for.
     * <p>
     * For display, the contact's name is shown.
     * <p>
     * Pulls from MySQL contacts table.
     * <p>
     * An initial field is added to the ComboBox with a Contact_ID of -1 and a Contact_Name of "ALL" so the user
     * has the choice to select ALL contacts to run the report for.
     */
    private void buildReport2ComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectContactByID.sql"),
                Collections.singletonList(Constants.WILDCARD));

        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        contacts.add(new Contact(-1,"ALL"));
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

        report2ComboBox.setItems(contacts);

        report2ComboBox.setConverter(new StringConverter<>() {
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
     * buildReport1Data() executes an SQL query to find the total count of appointments by month and type for either all
     * customers or a specific customer. This data is set in the Report1TableView in setReport1TableColumns().
     * <p>
     * The fields in Report #1 include:
     * <ul>
     * <li>Month: The name of the month in which the appointment takes place</li>
     * <li>Type: The type of the appointment</li>
     * <li>Count: The total count of all appointments that have the same specified month and type</li>
     * </ul>
     */
    public void buildReport1Data() {
        String searchID;
        if ( report1ComboBox.getSelectionModel().getSelectedItem() == null ) {
            report1ErrorLabel.setText(rb.getString("report1EmptyComboBox"));
            return;
        } else if ( report1ComboBox.getSelectionModel().getSelectedItem().getCustomerID() == Constants.REPORT_SELECT_ALL_INT ){
            searchID = Constants.WILDCARD;
        } else {
            searchID = String.valueOf(report1ComboBox.getSelectionModel().getSelectedItem().getCustomerID());
        }

        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.REPORT_SCRIPT_PATH_BASE + "CountAppointmentsByCustomerMonthType.sql"),
                Collections.singletonList(searchID)
        );

        ObservableList<Report1> data = FXCollections.observableArrayList();
        try {
            if (rs != null) {
                while (rs.next()) {
                    String month = rs.getString("Month");
                    String type = rs.getString("Type");
                    int count = rs.getInt("Count");
                    data.add(new Report1(month, type, count));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        if ( data.isEmpty() ) {
            report1ErrorLabel.setText(rb.getString("noRecordsFound"));
        } else {
            report1ErrorLabel.setText("");
        }

        setReport1TableColumns(data);
    }

    /**
     * setReport1TableColumns() tells the Report1TableView what type of data to expect and sets the data in the
     * table according to the list that is passed in from buildReport1Data().
     *
     * @param data the data to put in the Report1TableView
     */
    private void setReport1TableColumns(ObservableList<Report1> data) {
        report1MonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        report1TypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        report1CountCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        report1TableView.setItems(data);
    }

    /**
     * buildReport2Data() executes an SQL query to find a schedule of appointments for either all contacts or a specific
     * contact. This data is set in the Report2TableView in setReport2TableColumns().
     * <p>
     * The fields in Report #2 include:
     * <ul>
     * <li>Contact ID: The ID of the contact for the given schedule</li>
     * <li>Contact Name: The name of the contact for the given schedule</li>
     * <li>Date: The date of an appointment in local date format</li>
     * <li>Start: The start time of an appointment in local system time (h:mm a)</li>
     * <li>End: The end time of an appointment in local system time (h:mm a)</li>
     * <li>Appointment ID: The ID of an appointment</li>
     * <li>Title: The title of an appointment</li>
     * <li>Type: The type of an appointment</li>
     * <li>Desc: The description of an appointment</li>
     * </ul>
     */
    public void buildReport2Data() {
        String searchID;
        if ( report2ComboBox.getSelectionModel().getSelectedItem() == null ) {
            report2ErrorLabel.setText(rb.getString("report2EmptyComboBox"));
            return;
        } else if ( report2ComboBox.getSelectionModel().getSelectedItem().getContactID() == Constants.REPORT_SELECT_ALL_INT ){
            searchID = Constants.WILDCARD;
        } else {
            searchID = String.valueOf(report2ComboBox.getSelectionModel().getSelectedItem().getContactID());
        }
        report2ErrorLabel.setText("");

        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.REPORT_SCRIPT_PATH_BASE + "ContactSchedule.sql"),
                Collections.singletonList(searchID)
        );

        ObservableList<Report2> data = FXCollections.observableArrayList();
        try {
            if (rs != null) {
                report2ErrorLabel.setText("");
                while (rs.next()) {
                    int contactID = rs.getInt("Contact_ID");
                    String contactName = rs.getString("Contact_Name");
                    String date = DateTime.UTCTimestampToDateDisplay(rs.getTimestamp("Start"));
                    String start = DateTime.UTCTimestampToTimeDisplay(rs.getTimestamp("Start"));
                    String end = DateTime.UTCTimestampToTimeDisplay(rs.getTimestamp("End"));
                    int appointmentID = rs.getInt("Appointment_ID");
                    String title = rs.getString("Title");
                    String desc = rs.getString("Description");
                    String type = rs.getString("Type");
                    data.add(new Report2(contactID, contactName, date, start, end, appointmentID, title, desc, type));
                }
            } else {
                report2ErrorLabel.setText(rb.getString("noRecordsFound"));
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        if ( data.isEmpty() ) {
            report2ErrorLabel.setText(rb.getString("noRecordsFound"));
        } else {
            report2ErrorLabel.setText("");
        }

        setReport2TableColumns(data);
    }

    /**
     * setReport2TableColumns() tells the Report2TableView what type of data to expect and sets the data in the
     * table according to the list that is passed in from buildReport2Data().
     *
     * @param data the data to put in the Report2TableView
     */
    private void setReport2TableColumns(ObservableList<Report2> data) {
        report2ContactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        report2ContactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        report2DateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        report2StartTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        report2EndTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        report2AppointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        report2TitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        report2DescCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
        report2TypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        report2TableView.setItems(data);
    }

    /**
     * buildReport3Data() executes an SQL query to find a log of record updates made by the current logged-in user
     * for the current session. This data is set in the Report3TableView in setReport3TableColumns().
     * <p>
     * The fields in Report #3 include:
     * <ul>
     * <li>Username: The username of the current logged-in user</li>
     * <li>Record Type: The type of record that was updated (appointment or customer)</li>
     * <li>Record ID: The ID Of the record that was updated (appointment ID or customer ID)</li>
     * <li>Time: The timestamp in local time that the record was updated by the user</li>
     * </ul>
     * <p>
     * It's important to note that if another user completely overwrites the current user's changes and the current user
     * is no longer on the record, then that record will not show up in the report.
     */
    public void buildReport3Data() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.REPORT_SCRIPT_PATH_BASE + "CurrentSessionUpdateHistory.sql"),
                Arrays.asList(session.getLoginTime().toString(), session.getUsername(), session.getLoginTime().toString(), session.getUsername())
        );

        ObservableList<Report3> data = FXCollections.observableArrayList();
        try {
            if ( rs != null ) {
                report3ErrorLabel.setText("");
                while (rs.next()) {
                    String username = rs.getString("User_Name");
                    String recordType = rs.getString("File_Type");
                    int recordID = rs.getInt("File_ID");
                    Timestamp time = rs.getTimestamp("Update_Time");
                    data.add(new Report3(username, recordType, recordID, time));
                }
            } else {
                report3ErrorLabel.setText(rb.getString("noRecordsFound"));
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        if ( data.isEmpty() ) {
            report3ErrorLabel.setText(rb.getString("noRecordsFound"));
        } else {
            report3ErrorLabel.setText("");
        }

        setReport3TableColumns(data);
    }

    /**
     * setReport3TableColumns() tells the Report2TableView what type of data to expect and sets the data in the
     * table according to the list that is passed in from buildReport3Data().
     *
     * @param data the data to put in the Report3TableView
     */
    private void setReport3TableColumns(ObservableList<Report3> data) {
        report3UsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        report3RecordTypeCol.setCellValueFactory(new PropertyValueFactory<>("recordType"));
        report3RecordIDCol.setCellValueFactory(new PropertyValueFactory<>("recordID"));
        report3TimeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        report3TimeCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp time, boolean empty) {
                super.updateItem(time, empty);
                if ( empty ) { setText(null); }
                else { setText(DateTime.UTCTimestampToDateTimeDisplay(time)); }
            }
        });

        report3TableView.setItems(data);
    }

    /**
     * setSession() is used to pass in session data from the main view. This session data includes the open connection
     * to the database so that updates are possible, current user information, and the log-in timestamp.
     *
     * @param session the session to set
     */
    public void setSession(Session session) { this.session = session; }

    /**
     * back() listens for when the "Back" button is pressed. Once it is, the user is returned to the main menu.
     */
    public void back() { Common.exitWindow(backBtn); }
}


