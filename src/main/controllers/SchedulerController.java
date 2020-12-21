package main.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.models.Session;
import main.util.Common;
import main.models.Appointment;
import main.models.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.util.Constants;
import main.util.DatabaseConnection;
import main.util.DateTime;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * <h1>SchedulerController</h1>
 * The SchedulerController is the main menu of the scheduling program. It provides an overview of all customers and
 * appointments in the MySQL database. SQL queries are used to filter the data.
 * <p>
 * The actions that can be taken from the main menu include:
 * <ul>
 * <li>Add, update, or delete a customer</li>
 * <li>Filter customers by ID or name via a TextField</li>
 * <li>Add, update, or delete an appointment</li>
 * <li>Filter appointments by associated customer ID, by date, by week, or by month.</li>
 * <li>Navigate to reports pane to generate a new report</li>
 * <li>Clear current filter selections</li>
 * <li>Reset filter date to current date</li>
 * <li>Logout</li>
 * </ul>
 * <p>
 *
 * @author Lee Rhodes
 */
public class SchedulerController {
    private Session session;
    private final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "scheduler_" + Locale.getDefault().getLanguage());
    private String currSelectedCustomer = Constants.WILDCARD;
    private String currStartTS = Constants.MIN_DATE;
    private String currEndTS = Constants.MAX_DATE;
    private Timestamp startDateBound;
    private Timestamp endDateBound;
    private LocalDate pagedDate;
    @FXML
    private Label customersTitleLabel;
    @FXML
    private TextField customerSearchField;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> customerIDCol;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String>  customerAddressCol;
    @FXML
    private TableColumn<Customer, String>  customerPostalCol;
    @FXML
    private TableColumn<Customer, String>  customerPhoneCol;
    @FXML
    private Label customerErrorLabel;
    @FXML
    private Button customerAddBtn;
    @FXML
    private Button customerUpdateBtn;
    @FXML
    private Button customerDeleteBtn;
    @FXML
    private Label appointmentsTitleLabel;
    @FXML
    private ToggleGroup byDateToggleGrp;
    @FXML
    private RadioButton showAllRadioBtn;
    @FXML
    private RadioButton byWeekRadioBtn;
    @FXML
    private RadioButton byMonthRadioBtn;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private Label appointmentFilterLabel;
    @FXML
    private Button appointmentPrevBtn;
    @FXML
    private Button appointmentNextBtn;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML
    private TableColumn<Appointment, String> appointmentDescCol;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationCol;
    @FXML
    private TableColumn<Appointment, String> appointmentContactCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeCol;
    @FXML
    private TableColumn<Appointment, Timestamp> appointmentStartCol;
    @FXML
    private TableColumn<Appointment, Timestamp> appointmentEndCol;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIDCol;
    @FXML
    private Label appointmentErrorLabel;
    @FXML
    private Button appointmentAddBtn;
    @FXML
    private Button appointmentUpdateBtn;
    @FXML
    private Button appointmentDeleteBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button reportsBtn;
    @FXML
    private Button clearSelectionsBtn;
    @FXML
    private Button resetDateBtn;

    /**
     * initialize() is responsible for making sure the initial data retrieved from the MySQL database when the program starts.
     * Afterwards, it populates the customer and appointment table views with the data retrieved,
     * along with any other start-up methods.
     * <p>
     * Additionally, it checks for any upcoming appointments within 15 minutes of log-in.
     */
    public void initialize() {
        Platform.runLater(() -> {
            initializeDisplayText();
            buildInitialData();
            setDatePagingBtns(false);
            addListeners();
            checkForUpcomingAppointments();
        });
    }

    /**
     * initializeDisplayText() pulls from properties to display all text in the system's language. Currently,
     * English (en) and French (fr) are the only two languages supported.
     */
    private void initializeDisplayText() {
        // Title and field labels
        Stage stage = (Stage) customersTitleLabel.getScene().getWindow();
        stage.setTitle(rb.getString("windowTitle"));

        // Display text for customers section
        customersTitleLabel.setText(rb.getString("customersTitleLabel"));
        customerSearchField.setPromptText(rb.getString("customerSearchPrompt"));
        customerIDCol.setText(rb.getString("customerIDCol"));
        customerNameCol.setText(rb.getString("customerNameCol"));
        customerAddressCol.setText(rb.getString("customerAddressCol"));
        customerPostalCol.setText(rb.getString("customerPostalCol"));
        customerPhoneCol.setText(rb.getString("customerPhoneCol"));

        // Display text for appointments section
        appointmentsTitleLabel.setText(rb.getString("appointmentsTitleLabel"));
        byWeekRadioBtn.setText(rb.getString("appointmentByWeekRadio"));
        byMonthRadioBtn.setText(rb.getString("appointmentByMonthRadio"));
        showAllRadioBtn.setText(rb.getString("appointmentShowAllRadio"));
        appointmentDatePicker.setPromptText(rb.getString("appointmentDatePickerPrompt"));
        appointmentFilterLabel.setText(rb.getString("allAppointments"));
        appointmentIDCol.setText(rb.getString("appointmentIDCol"));
        appointmentTitleCol.setText(rb.getString("appointmentTitleCol"));
        appointmentDescCol.setText(rb.getString("appointmentDescCol"));
        appointmentLocationCol.setText(rb.getString("appointmentLocationCol"));
        appointmentContactCol.setText(rb.getString("appointmentContactCol"));
        appointmentTypeCol.setText(rb.getString("appointmentTypeCol"));
        appointmentStartCol.setText(rb.getString("appointmentStart"));
        appointmentEndCol.setText(rb.getString("appointmentEnd"));
        appointmentCustomerIDCol.setText(rb.getString("appointmentCustomerID"));

        // Button labels
        Common.scaleButton(customerAddBtn, rb.getString("add"));
        Common.scaleButton(customerUpdateBtn, rb.getString("update"));
        Common.scaleButton(customerDeleteBtn, rb.getString("delete"));
        Common.scaleButton(appointmentPrevBtn, rb.getString("previous"));
        Common.scaleButton(appointmentNextBtn, rb.getString("next"));
        Common.scaleButton(appointmentAddBtn, rb.getString("add"));
        Common.scaleButton(appointmentUpdateBtn, rb.getString("update"));
        Common.scaleButton(appointmentDeleteBtn, rb.getString("delete"));
        Common.scaleButton(logoutBtn, rb.getString("logout"));
        Common.scaleButton(reportsBtn, rb.getString("goToReports"));
        Common.scaleButton(clearSelectionsBtn, rb.getString("clearSelections"));
        Common.scaleButton(resetDateBtn, rb.getString("resetDate"));

        // Error label; initially blank
        customerErrorLabel.setText("");
        appointmentErrorLabel.setText("");


        // Convert date string that shows in the date picker to fit local format
        appointmentDatePicker.setConverter(new StringConverter<>() {
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
     * buildInitialData() executes an initial query to the MySQL database that returns all customers and appointments
     * and sets the initial filter date to today's date.
     */
    private void buildInitialData() {
        buildCustomerData(Constants.NAME, Collections.singletonList(Constants.WILDCARD));
        buildAppointmentData(buildAppointmentFilters());
        resetPagedDate();
        buildDateToggleGroup();
    }

    /**
     * addListeners() adds listeners to fields that are responsible for filtering data in the TableViews according
     * to user inputs.
     */
    private void addListeners() {
        customerSearchListener();
        appointmentFilterListener();
    }

    /**
     * checkForUpcomingAppointments() executes a query to the MySQL database that checks to see if there are any
     * upcoming appointments within 15 minutes. If there are any appointments, it displays the appointment ID
     * and start time in an alert to the user. This is only called right after logging in.
     */
    private void checkForUpcomingAppointments() {
        List<String> args = Arrays.asList(
                session.getLoginTime().toString(),
                Timestamp.from(session.getLoginTime().toInstant().plus(Constants.APPOINTMENT_WITHIN_MINUTES, ChronoUnit.MINUTES)).toString()
        );

        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectUpcomingAppointments.sql"),
                args
        );

        ButtonType localOK = new ButtonType(rb.getString("ok"), ButtonBar.ButtonData.OK_DONE);
        Alert upcomingAlert = new Alert(Alert.AlertType.INFORMATION);
        upcomingAlert.setTitle(rb.getString("appointmentAlertTitle"));

        StringBuilder appointments = new StringBuilder();
        try {
            if ( rs != null ) {
                while ( rs.next() ) {
                    int appointmentID = rs.getInt("Appointment_ID");
                    String date = DateTime.UTCTimestampToDateTimeDisplay(rs.getTimestamp("Start"));
                    if (appointments.length() > 0) {
                        appointments.append("\n");
                    }
                    appointments.append("<ID: ").append(appointmentID).append("> ").append(date);
                }

            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        if ( appointments.toString().isEmpty() ) {
            upcomingAlert.setHeaderText(rb.getString("appointmentAlertNoneHeader"));
        } else {
            upcomingAlert.setHeaderText(rb.getString("appointmentAlertExistsHeader"));
            upcomingAlert.setContentText(appointments.toString());
        }
        upcomingAlert.getButtonTypes().setAll(localOK);
        upcomingAlert.showAndWait();
    }

    /**
     * buildCustomerData() executes an SQL query to find all customers that match the given arguments and put that
     * data in the CustomerTableView. The data is set in the CustomerTableView in setCustomerTableColumns().
     *
     * @param type the type that the customers will be filtered by (ID or Name)
     * @param args the arguments that will be passed to the query to select customers with (the value of the name or id)
     */
    private void buildCustomerData(String type, List<String> args) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCustomersBy" + type + ".sql"),
                args
        );

        ObservableList<Customer> data = FXCollections.observableArrayList();
        try {
            if (rs != null) {
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
                    data.add(new Customer(customerID, customerName, address, postalCode, phone, createDate,
                            createdBy, lastUpdate, lastUpdatedBy, divisionID));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

    setCustomerTableColumns(data);
    }

    /**
     * setCustomerTableColumns() tells the CustomerTableView what type of data to expect and sets the data in the table
     * according to the list that is passed in from buildCustomerData().
     *
     * @param data the data to put in the CustomerTableView
     */
    private void setCustomerTableColumns(ObservableList<Customer> data) {
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        customerTableView.setItems(data);
    }

    /**
     * customerSearchListener() listens for when the search text field input changes and filters the list of customers
     * based on what matches the input.
     * <p>
     * An SQL query is executed to find matching customer records to replace the tableview data.
     * <p>
     * Digit-only input will search for customers with a matching ID; otherwise, it searches for customers with a name
     * that begins with or equals the input.
     */
    public void customerSearchListener() {
        /* Adding a listener to watch whenever the search text field input is changed & react
           NOTE: Could simplify if statement to just return boolean value of matches, but decided to separate it
                 out into a long if-statement for readability */
        customerSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String col = Constants.NAME;
            String val = Constants.WILDCARD;
            if (!newValue.isEmpty() && newValue.matches("[0-9]*")) {
                col = Constants.ID;
                val = newValue + Constants.WILDCARD;
            } else if (!newValue.isEmpty()){
                val = newValue + Constants.WILDCARD;
            }
            buildCustomerData(col, Collections.singletonList(val));

        });
    }

    /**
     * buildAppointmentData() executes an SQL query to find all appointments that match the given arguments and put that
     * data in the AppointmentTableView. The data is set in the AppointmentTableView in setAppointmentTableColumns().

     * @param args the arguments that will be passed to the query to select appointments with (customer ID or date range)
     */
    private void buildAppointmentData(List<String> args) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectAppointmentsByFilter.sql"),
                args
        );

        ObservableList<Appointment> data = FXCollections.observableArrayList();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int appointmentID = rs.getInt("Appointment_ID");
                    String title = rs.getString("Title");
                    String description = rs.getString("Description");
                    String location = rs.getString("Location");
                    String type = rs.getString("Type");
                    Timestamp start = rs.getTimestamp("Start");
                    Timestamp end = rs.getTimestamp("End");
                    Timestamp createDate = rs.getTimestamp("Create_Date");
                    String createdBy = rs.getString("Created_By");
                    Timestamp lastUpdate = rs.getTimestamp("Last_Update");
                    String lastUpdatedBy = rs.getString("Last_Updated_By");
                    int customerID = rs.getInt("Customer_ID");
                    int userID = rs.getInt("User_ID");
                    String contactID = String.valueOf(rs.getInt("Contact_ID"));
                    data.add(new Appointment(appointmentID, title, description, location, type, start, end,
                            createDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        setAppointmentTableColumns(data);
    }

    /**
     * setAppointmentTableColumns() tells the AppointmentTableView what type of data to expect and sets the data in the
     * table according to the list that is passed in from buildAppointmentData().
     * <p>
     * LAMBDA EXPRESSION REASONING: A few lambdas are used here to alter how table cells are displayed before the user
     * gets the chance to see them. This way, a column such as contactID is able to receive a contact's ID but display
     * the contact's name instead for the user by overriding the updateItem() method. For dates, the timestamp
     * can be replaced with a user-friendly date shown in the local date format. These lambdas are primarily used
     * for display decisions for user-friendliness.
     *
     * @param data the data to put in the AppointmentTableView
     */
    private void setAppointmentTableColumns(ObservableList<Appointment> data) {
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        // Query for a contact's name and display the name instead of the ID.
        appointmentContactCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String contactID, boolean empty) {
                super.updateItem(contactID, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(getContactNameByID(contactID));
                }
            }
        });

        // Convert date and time to local display format.
        appointmentStartCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp start, boolean empty) {
                super.updateItem(start, empty);
                if ( empty ) { setText(null); }
                else { setText(DateTime.UTCTimestampToDateTimeDisplay(start)); }
            }
        });
        appointmentEndCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp end, boolean empty) {
                super.updateItem(end, empty);
                if ( empty ) { setText(null); }
                else { setText(DateTime.UTCTimestampToDateTimeDisplay(end)); }
            }
        });

        appointmentTableView.setItems(data);
    }

    /**
     * getContactNameByID() sends a query to the MySQL database to find and return the name for a specific contact from the
     * contacts table according to the passed-in Contact_ID PK.
     * <p>
     * This contact name is used to display the contact's name in the AppointmentTableView as the MySQL appointments
     * table only holds the contact ID.
     *
     * @param contactID the contact ID key to search for the desired contact in the MySQL database
     * @return the contact name found by the query
     */
    private String getContactNameByID(String contactID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectContactByID.sql"),
                Collections.singletonList(contactID)
        );

        String contactName = "";
        try {
            if (rs != null) {
                while (rs.next()) {
                    contactName = rs.getString("Contact_Name");
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return contactName;
    }

    /**
     * appointmentFilterListener() listens for any changes in the CustomerTableView or appointment DatePicker.
     * <p>
     * An SQL query is executed to find matching appointment records to replace the tableview data.
     */
    public void appointmentFilterListener() {
        customerTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currSelectedCustomer = String.valueOf(newSelection.getCustomerID());
            } else {
                currSelectedCustomer = Constants.WILDCARD;
            }
            buildAppointmentData(buildAppointmentFilters());
        });

        appointmentDatePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            this.pagedDate = newDate;
            if (byMonthRadioBtn.isSelected()) {
                this.appointmentFilterLabel.setText(DateTime.localToMonthDisplay(pagedDate));
                this.startDateBound = DateTime.getStartOfMonth(pagedDate);
                this.endDateBound = DateTime.getEndOfMonth(pagedDate);
                currStartTS = startDateBound.toString();
                currEndTS = endDateBound.toString();
            } else if (byWeekRadioBtn.isSelected()) {
                this.appointmentFilterLabel.setText(DateTime.localToWeekDisplay(pagedDate));
                this.startDateBound = DateTime.getStartOfWeek(pagedDate);
                this.endDateBound = DateTime.getEndOfWeek(pagedDate);
                currStartTS = startDateBound.toString();
                currEndTS = endDateBound.toString();
            } else if (showAllRadioBtn.isSelected()) {
                appointmentFilterLabel.setText(rb.getString("allAppointments"));
                currStartTS = Constants.MIN_DATE;
                currEndTS = Constants.MAX_DATE;
            }
            buildAppointmentData(buildAppointmentFilters());
        });
    }

    /**
     * buildDateToggleGroup() sets up the toggle group that allows the user to select how they want to filter the
     * AppointmentTableView when it comes to the date range. The user can view the appointments all at once, or filter
     * them by week or by month. Next and previous buttons are used to page between weeks and months.
     */
    public void buildDateToggleGroup() {
        byDateToggleGrp.selectedToggleProperty().addListener((observable, t1, t2) -> {
            /*  Check which radio button is selected & change toggle and date filter to match selection; */
            if ( showAllRadioBtn.isSelected() ) {
                appointmentFilterLabel.setText(rb.getString("allAppointments"));

            } else if ( byMonthRadioBtn.isSelected() ) {
                appointmentFilterLabel.setText(DateTime.localToMonthDisplay(pagedDate));
                this.startDateBound = DateTime.getStartOfMonth(pagedDate);
                this.endDateBound = DateTime.getEndOfMonth(pagedDate);

            } else if ( byWeekRadioBtn.isSelected() ) {
                appointmentFilterLabel.setText(DateTime.localToWeekDisplay(pagedDate));
                this.startDateBound = DateTime.getStartOfWeek(pagedDate);
                this.endDateBound = DateTime.getEndOfWeek(pagedDate);
                setDatePagingBtns(true);
            }

            DateToggleGroupUtil();
            buildAppointmentData(buildAppointmentFilters());
        });
    }

    /**
     * DateToggleGroupUtil() handles setting up the date paging button visibility, arming or disabling the date picker,
     * and setting the start and end bounds for filtering the appointment records.
     * <p>
     * This is used in buildDateToggleGroup() and was extracted for code reusability.
     */
    private void DateToggleGroupUtil() {
        if ( showAllRadioBtn.isSelected() ) {
            setDatePagingBtns(false);
            currStartTS = Constants.MIN_DATE;
            currEndTS = Constants.MAX_DATE;
        } else {
            setDatePagingBtns(true);
            currStartTS = startDateBound.toString();
            currEndTS = endDateBound.toString();
        }
    }

    /**
     * setDatePagingBtns() makes the previous and next paging buttons invisible or visible. If all dates are being
     * shown and not being filtered by week or month, then the buttons do not need to be shown. Similarly, the
     * DatePicker is disabled when showing all dates and armed when filtered by week or month.
     *
     * @param active whether or not the paging buttons and DatePicker should be active or not
     */
    private void setDatePagingBtns(boolean active) {
        appointmentPrevBtn.setVisible(active);
        appointmentNextBtn.setVisible(active);
        if ( active ) {
            appointmentDatePicker.isArmed();
        }  else {
            appointmentDatePicker.isDisabled();
        }
    }

    /**
     * incrementDate() adds 1 week or month (depending on which filter is selected) to the paging date. This change
     * will cause the AppointmentTableView to update with new matches.
     */
    public void incrementDate() {
        if (byMonthRadioBtn.isSelected()) {
            this.pagedDate = pagedDate.plusMonths(Constants.DATE_PAGING_INCREMENT);
        } else {
            this.pagedDate = pagedDate.plusWeeks(Constants.DATE_PAGING_INCREMENT);
        }
        appointmentDatePicker.setValue(pagedDate);
    }

    /**
     * decrementDate() subtracts 1 week or month (depending on which filter is selected) to the paging date. This change
     * will cause the AppointmentTableView to update with new matches.
     */
    public void decrementDate() {
        if (byMonthRadioBtn.isSelected()) {
            this.pagedDate = pagedDate.minusMonths(Constants.DATE_PAGING_INCREMENT);
        } else {
            this.pagedDate = pagedDate.minusWeeks(Constants.DATE_PAGING_INCREMENT);
        }
        appointmentDatePicker.setValue(pagedDate);
    }

    /**
     * addCustomer() calls child view CustomerController which is the form to add or modify customers.
     * This method can only be called by pressing the "Add" button on the main menu underneath the CustomerTableView.
     * <p>
     * Session information is passed to the child view and the action is set to "add".
     */
    public void addCustomer() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Customer.fxml"));
            Parent parent = loader.load();
            CustomerController controller = loader.getController();
            controller.setSession(session);
            controller.setAction(Constants.ADD);

            Scene scene = new Scene(parent);

            stage.setMinWidth(602);
            stage.setMinHeight(483);
            stage.setScene(scene);
            stage.showAndWait();
            buildCustomerData(Constants.NAME, Collections.singletonList(Constants.WILDCARD));
        } catch (Exception e) {
            Common.handleException(e);
        }
    }

    /**
     * updateCustomer() calls child view CustomerController which is the form to add or modify customers.
     * This method can only be called by pressing the "Update" button on the main menu underneath the CustomerTableView.
     * <p>
     * Session information is passed to the child view and the action is set to "update". The selected customer is
     * passed to the child view for editing.
     */
    public void updateCustomer() {
        String errors = "";
        Customer existingCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (existingCustomer != null) {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Customer.fxml"));
                Parent parent = loader.load();
                CustomerController controller = loader.getController();
                controller.setSession(session);
                controller.setAction(Constants.UPDATE);
                controller.setExistingCustomer(existingCustomer);

                Scene scene = new Scene(parent);

                stage.setMinWidth(602);
                stage.setMinHeight(483);
                stage.setScene(scene);
                stage.showAndWait();
                buildCustomerData(Constants.NAME, Collections.singletonList(Constants.WILDCARD));
            } catch (Exception e) {
                Common.handleException(e);
            }
        } else {
            errors = rb.getString("customerNullSelection");
        }

        customerErrorLabel.setText(errors);
    }

    /**
     * deleteCustomer() executes an SQL update to DELETE the selected customer from the MySQL database customers table.
     * <p>
     * An alert appears to receive user confirmation first before deleting the customer.
     * <p>
     * If no customer is selected or the user declines confirmation, nothing happens and method is exited.
     * <p>
     * If the selected customer has associated appointments, the user will receive a warning that it cannot be deleted,
     * then the method is exited.
     */
    public void deleteCustomer() {
        String errors = "";
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();

        // Can only delete customer if selecting a row in customerTableView AND that customer has no associated appointments
        if ( customer != null && !customerHasAppointments(customer.getCustomerID())) {
            ButtonType localYes = new ButtonType(rb.getString("yes"), ButtonBar.ButtonData.YES);
            ButtonType localNo = new ButtonType(rb.getString("no"), ButtonBar.ButtonData.NO);
            // Must confirm that user wants to delete before calling method & making changes
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle(rb.getString("confirmationTitle"));
            confirmDelete.setHeaderText(rb.getString("confirmCustomerDeleteHeader")
                    + " " + customer.getCustomerName() + ", ID " + customer.getCustomerID());
            confirmDelete.setContentText(rb.getString("confirmCustomerDelete"));

            // Using buttons "YES" and "NO" instead of the default buttons for user-friendliness
            confirmDelete.getButtonTypes().setAll(localYes, localNo);

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.isPresent() && result.get() == localYes) {
                DatabaseConnection.performUpdate(
                        session.getConn(),
                        Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "DeleteCustomer.sql"),
                        Collections.singletonList(String.valueOf(customer.getCustomerID()))
                );
                buildCustomerData(Constants.NAME, Collections.singletonList(Constants.WILDCARD));
            }
        } else if (customer != null && customerHasAppointments(customer.getCustomerID())) {
            // Let user know that customer cannot be deleted due to associated appointments & exit func without deleting
            ButtonType localOK = new ButtonType(rb.getString("ok"), ButtonBar.ButtonData.OK_DONE);
            Alert deleteError = new Alert(Alert.AlertType.ERROR);
            deleteError.setTitle(rb.getString("errorTitle"));
            deleteError.setHeaderText(rb.getString("customerNoDeleteHasAppointmentsHeader"));
            deleteError.setContentText(rb.getString("customerNoDeleteHasAppointments"));

            deleteError.getButtonTypes().setAll(localOK);

            deleteError.showAndWait();
        } else {
            errors = rb.getString("customerNullSelection");
        }
        customerErrorLabel.setText(errors);
    }

    /**
     * customerHasAppointments() sends an SQL query to the MySQL database appointments table to see if the given
     * customer ID is a foreign key in any existing appointment records. If it is, then it won't be able to be
     * deleted.
     *
     * @param customerID the ID to be used as a key to search for appointment records
     * @return whether or not appointments were found
     */
    private boolean customerHasAppointments(int customerID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectAppointmentsByFilter.sql"),
                Arrays.asList(String.valueOf(customerID), Constants.MIN_DATE, Constants.MAX_DATE)
        );

        try {
            assert rs != null;
            return rs.next();
        } catch (Exception e) {
            Common.handleException(e);
        }
        return true;
    }

    /**
     * addAppointment() calls child view AppointmentController which is the form to add or modify appointments.
     * This method can only be called by pressing the "Add" button on the main menu underneath the AppointmentTableView.
     * <p>
     * Session information is passed to the child view and the action is set to "add".
     */
    public void addAppointment() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Appointment.fxml"));
            Parent parent = loader.load();
            AppointmentController controller = loader.getController();
            controller.setSession(session);
            controller.setAction(Constants.ADD);

            Scene scene = new Scene(parent);

            stage.setMinWidth(638);
            stage.setMinHeight(675);
            stage.setScene(scene);
            stage.showAndWait();
            buildAppointmentData(buildAppointmentFilters());
        } catch (Exception e) {
            Common.handleException(e);
        }
    }

    /**
     * updateAppointment() calls child view AppointmentController which is the form to add or modify appointments.
     * This method can only be called by pressing the "Update" button on the main menu underneath the AppointmentTableView.
     * <p>
     * Session information is passed to the child view and the action is set to "update". The selected appointment is
     * passed to the child view for editing.
     */
    public void updateAppointment() {
        String errors = "";
        Appointment existingAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if (existingAppointment != null) {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Appointment.fxml"));
                Parent parent = loader.load();
                AppointmentController controller = loader.getController();
                controller.setSession(session);
                controller.setAction(Constants.UPDATE);
                controller.setExistingAppointment(existingAppointment);

                Scene scene = new Scene(parent);

                stage.setMinWidth(638);
                stage.setMinHeight(675);
                stage.setScene(scene);
                stage.showAndWait();
                buildAppointmentData(buildAppointmentFilters());
            } catch (Exception e) {
                Common.handleException(e);
            }
        } else {
            errors = rb.getString("appointmentNullSelection");
        }

        appointmentErrorLabel.setText(errors);
    }

    /**
     * deleteAppointment() executes an SQL update to DELETE the selected appointment from the MySQL database
     * appointments table.
     * <p>
     * An alert appears to receive user confirmation first before deleting the appointment.
     * <p>
     * If no appointment is selected or the user declines confirmation, nothing happens and method is exited.
     */
    public void deleteAppointment() {
        String errors = "";
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if ( appointment != null ) {
            ButtonType localYes = new ButtonType(rb.getString("yes"), ButtonBar.ButtonData.YES);
            ButtonType localNo = new ButtonType(rb.getString("no"), ButtonBar.ButtonData.NO);
            // Must confirm that user wants to delete before calling method & making changes
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle(rb.getString("confirmationTitle"));
            confirmDelete.setHeaderText(rb.getString("confirmAppointmentDeleteHeader")
                    + " " + appointment.getType() + ", ID " + appointment.getAppointmentID());
            confirmDelete.setContentText(rb.getString("confirmAppointmentDelete"));

            // Using buttons "YES" and "NO" instead of the default buttons for user-friendliness
            confirmDelete.getButtonTypes().setAll(localYes, localNo);

            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.isPresent() && result.get() == localYes) {
                DatabaseConnection.performUpdate(
                        session.getConn(),
                        Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "DeleteAppointment.sql"),
                        Collections.singletonList(String.valueOf(appointment.getAppointmentID()))
                );
                buildAppointmentData(buildAppointmentFilters());
            }
        } else {
            errors = rb.getString("appointmentNullSelection");
        }
        customerErrorLabel.setText(errors);
    }

    /**
     * buildAppointmentFilters() is used to combine the selected customer data, paging Start timestamp, and paging End
     * timestamp together in a list so that it can be used to filter the AppointmentTableView as passable arguments.
     *
     * @return the list of strings that will be used to filter an appointment query
     */
    private List<String> buildAppointmentFilters() {
        return Arrays.asList(currSelectedCustomer, currStartTS, currEndTS);
    }

    /**
     * resetPagedDate() resets the paging date to the current local system date. Afterwards, it updates the
     * AppointmentTableView with new filtered appointments based on the current date.
     */
    public void resetPagedDate() {
        Date date = new Date();
        this.pagedDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        appointmentDatePicker.setValue(pagedDate);
    }

    /**
     * clearSelections() removes any selections from the CustomerTableView and AppointmentTableView.
     */
    public void clearSelections() {
        customerTableView.getSelectionModel().clearSelection();
        appointmentTableView.getSelectionModel().clearSelection();
    }

    /**
     * goToReports() calls child view ReportsController which is the form to generate any of the reports that have
     * been made for the program. Currently these are 3 reports.
     * <p>
     * The supported reports include:
     * <ul>
     * <li>Report #1: The total number of customer appointments by type and month.
     * Pulls data from MySQL tables - customers and appointments.</li>
     * <li>Report #2: The The appointment schedule for each contact in the database.
     * Pulls data from MySQL tables - contacts and appointments.</li>
     * <li>Report #3: The current user's session history for record changes, provided their changes haven't been
     * completely overwritten by another user since logging in.
     * Pulls data from MySQL tables - users, customers, and appointments</li>
     * </ul>
     * This method can only be called by pressing the "Generate Reports" button on the main menu at the very bottom.
     */
    public void goToReports() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Reports.fxml"));
            Parent parent = loader.load();
            ReportsController controller = loader.getController();
            controller.setSession(session);

            Scene scene = new Scene(parent);

            stage.setMinWidth(1103);
            stage.setMinHeight(800);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            Common.handleException(e);
        }
    }


    /**
     * setSession() is used to pass in session data from the login view. This session data includes the open connection
     * to the database so that updates are possible, current user information, and the log-in timestamp.
     *
     * @param session the session to set
     */
    public void setSession(Session session) { this.session = session; }


    /**
     * logout() listens for when the "Logout" button is pressed. Once it is, the user is returned to the login form
     * and must enter their credentials again to return to the main menu.
     */
    public void logout() { Common.exitWindow(logoutBtn); }
}
