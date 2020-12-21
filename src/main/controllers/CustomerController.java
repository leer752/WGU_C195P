package main.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.models.Session;
import main.util.Common;
import main.util.Constants;
import main.models.Country;
import main.models.Customer;
import main.models.FirstLevelDivision;
import main.util.DatabaseConnection;
import main.util.DateTime;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * <h1>CustomerController</h1>
 * The CustomerController is responsible for the customer form pane of the scheduling program.
 * <p>
 * This form allows a user to add or update a customer depending on if they clicked "add" or "update" on the main view.
 * <p>
 * The fields for the customer form include:
 * <ul>
 * <li>Integer ID: the customer's ID; disabled as it is set by MySQL; MySQL = "Customer_ID" PK</li>
 * <li>String Name: the customer's name; MySQL = "Customer_Name"</li>
 * <li>String Street Address: the customer's street address; combined with city field to create a full address;
 * MySQL = "Address"</li>
 * <li>String City: the customer's city; combined with street address field to create a full address;
 * MySQL = "Address"</li>
 * <li>String Postal Code: the customer's postal code; MySQL = "Type"</li>
 * <li>String Phone: the customer's phone number; must be all digits; MySQL = "Phone"</li>
 * </ul>
 * <p>
 * Customer data will be validated and UPDATE the MySQL database upon pressing the "Save" button.
 * <p>
 * Additional fields (Create_Date, Created_By, Last_Update, Last_Updated_By, and Division_ID) are generated automatically.
 *
 * @author Lee Rhodes
 */
public class CustomerController {
    private Session session;
    private final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "customer_"+ Locale.getDefault().getLanguage());
    private String action;
    private Customer existingCustomer;
    @FXML
    private Label titleLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label divisionLabel;
    @FXML
    private Label postalLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private ComboBox<FirstLevelDivision> divisionComboBox;
    @FXML
    private TextField postalField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    /**
     * initialize() is responsible for populating the customer form pane with any existing data if it's an update,
     * and sets up all ComboBoxes and listeners.
     */
    public void initialize() {
        Platform.runLater(() -> {
            initializeDisplayText();
            buildComboBoxes();
            countryComboBoxListener();
            setCharLimitOnFields();
            if (action.equals(Constants.UPDATE)) {
                fillExistingCustomer();
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
            titleLabel.setText(rb.getString("addTitleLabel"));
        } else if (action.equals(Constants.UPDATE)) {
            titleLabel.setText(rb.getString("updateTitleLabel"));
        }
        idLabel.setText(rb.getString("idLabel"));
        nameLabel.setText(rb.getString("nameLabel"));
        addressLabel.setText(rb.getString("addressLabel"));
        cityLabel.setText(rb.getString("cityLabel"));
        countryLabel.setText(rb.getString("countryLabel"));
        divisionLabel.setText(rb.getString("divisionLabel"));
        postalLabel.setText(rb.getString("postalLabel"));
        phoneLabel.setText(rb.getString("phoneLabel"));

        // Prompt text
        idField.setPromptText(rb.getString("idPrompt"));
        nameField.setPromptText(rb.getString("namePrompt"));
        addressField.setPromptText(rb.getString("addressPrompt"));
        cityField.setPromptText(rb.getString("cityPrompt"));
        countryComboBox.setPromptText(rb.getString("countryPrompt"));
        divisionComboBox.setPromptText(rb.getString("divisionPrompt"));
        postalField.setPromptText(rb.getString("postalPrompt"));
        phoneField.setPromptText(rb.getString("phonePrompt"));

        // Button labels
        Common.scaleButton(saveBtn, rb.getString("saveBtn"));
        Common.scaleButton(cancelBtn, rb.getString("cancelBtn"));

        // Error label; initially blank
        errorLabel.setText("");
    }

    /**
     * buildComboBoxes() calls each method responsible for setting up one of the ComboBoxes on the customer form.
     */
    private void buildComboBoxes() {
        buildCountryComboBox();
        buildDivisionComboBox();
    }

    /**
     * buildCountryComboBox() calls a query to build a list of all countries in the MySQL database. These countries
     * are used to populate the choices for the Country ComboBox which is used for the user to select which country
     * to associate with the customer.
     * <p>
     * For display, the country's name is shown.
     * <p>
     * This country is used to limit the following DivisionComboBox to only list divisions associated with the
     * selected country.
     * <p>
     * Pulls from MySQL countries table.
     */
    private void buildCountryComboBox() {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCountryByID.sql"),
                Collections.singletonList(Constants.WILDCARD)
        );

        ObservableList<Country> countries = FXCollections.observableArrayList();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("Country_ID");
                    String name = rs.getString("Country");
                    countries.add(new Country(id, name));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        countryComboBox.setItems(countries);

        countryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country item) {
                return item.getCountryName();
            }

            @Override
            public Country fromString(String string) {
                return null;
            }
        });
    }

    /**
     * buildDivisionComboBox() calls a query to build a list of all divisions in the MySQL database that have a matching
     * Country_ID (FK) to the selected country in the CountryComboBox. These divisions are used to populate the choices
     * for the DivisionComboBox which is used for the user to select which division to associate with the customer.
     * <p>
     * For display, the division's name is shown.
     * <p>
     * Pulls from MySQL first_level_divisions table.
     */
    private void buildDivisionComboBox() {
        List<String> args = Collections.singletonList(Constants.WILDCARD);
        Country selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCountry != null) {
            args = Collections.singletonList(String.valueOf(selectedCountry.getCountryID()));
        }

        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectDivisionsByCountry.sql"),
                args
        );

        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("Division_ID");
                    String name = rs.getString("Division");
                    int countryID = rs.getInt("Country_ID");
                    divisions.add(new FirstLevelDivision(id, name, countryID));
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        divisionComboBox.setItems(divisions);

        divisionComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(FirstLevelDivision item) {
                return item.getDivisionName();
            }

            @Override
            public FirstLevelDivision fromString(String string) {
                return null;
            }
        });
    }

    /**
     * countryComboBoxListener() watches for when the CountryComboBox has a new selection made. Once a new selection is
     * made, it rebuilds the DivisionComboBox to limit its options to match the selected country.
     */
    public void countryComboBoxListener() {
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> buildDivisionComboBox());
    }

    /**
     * setCharLimitOnFields() sends each TextField/TextArea and its maximum length to the Common method setCharLimit().
     * That method adds a listener to the field that limits how many characters the user can enter.
     * <p>
     * Prevents a value being entered that's longer than the MySQL field length.
     */
    private void setCharLimitOnFields() {
        Common.setCharLimit(nameField, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(addressField, Constants.CHAR_LIMIT_LARGE);
        Common.setCharLimit(postalField, Constants.CHAR_LIMIT_NORMAL);
        Common.setCharLimit(phoneField, Constants.CHAR_LIMIT_NORMAL);
    }

    /**
     * fillExistingCustomer() takes any existing customer data that was passed into the view and populates the
     * form with the data. This is only used when updating an existing customer.
     */
    private void fillExistingCustomer() {
        idField.setText(String.valueOf(existingCustomer.getCustomerID()));
        nameField.setText(existingCustomer.getCustomerName());
        String[] splitAddress = existingCustomer.getAddress().split(", ");
        addressField.setText(splitAddress[0]);
        if ( splitAddress.length > 1 ) {
            cityField.setText(splitAddress[1]);
        }
        postalField.setText(existingCustomer.getPostalCode());
        phoneField.setText(existingCustomer.getPhone());

        FirstLevelDivision division = getDivisionByID(existingCustomer.getDivisionID());
        divisionComboBox.getSelectionModel().select(division);
        countryComboBox.getSelectionModel().select(getCountryByID(division.getCountryID()));
    }

    /**
     * getDivisionByID() sends a query to the MySQL database to find and return data for a specific division from the
     * first_level_divisions table according to the passed-in Division_ID PK.
     * <p>
     * This division is used to fill in the DivisionComboBox and is only used when updating an existing customer.
     *
     * @param divisionID the division ID key to search for the desired division in the MySQL database
     * @return the division found by the query
     */
    private FirstLevelDivision getDivisionByID(int divisionID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectDivisionByID.sql"),
                Collections.singletonList(String.valueOf(divisionID)));

        FirstLevelDivision division = null;
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("Division_ID");
                    String name = rs.getString("Division");
                    int countryID = rs.getInt("Country_ID");
                    division = new FirstLevelDivision(id, name, countryID);
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return division;
    }

    /**
     * getCountryByID() sends a query to the MySQL database to find and return data for a specific country from the
     * countries table according to the passed-in Country_ID PK.
     * <p>
     * This country is used to fill in the CountryComboBox and is only used when updating an existing customer.
     *
     * @param countryID the country ID key to search for the desired country in the MySQL database
     * @return the country found by the query
     */
    private Country getCountryByID(int countryID) {
        ResultSet rs = DatabaseConnection.performQuery(
                session.getConn(),
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectCountryByID.sql"),
                Collections.singletonList(String.valueOf(countryID)));

        Country country = null;
        try {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("Country_ID");
                    String name = rs.getString("Country");
                    country = new Country(id, name);
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }

        return country;
    }

    /**
     * addUpdateBtn() is called whenever the "Save" button is pressed on the customer form pane.
     * <p>
     * First, it uses isInputValid() to make sure all fields fit business rules and expected data types.
     * Next, it calls either addCustomer() or updateCustomer() depending on which action was selected
     * from the main menu. Finally, it exits the customer form pane and returns to main menu,
     * which will show data updates.
     */
    public void addUpdateBtn() {
        if (isInputValid()) {
            if ( action.equals(Constants.ADD) ) {
                addCustomer(createCustomerData());
            } else if ( action.equals(Constants.UPDATE) ){
                updateCustomer(createCustomerData());
            }
            cancel();
        }
    }

    /**
     * isInputValid() checks all input in the customer form's text fields to ensure they are valid according to
     * business rules and expected data types.
     * <p>
     * These requirements include:
     * <ul>
     * <li>No field can be empty (Checked in noFieldsEmpty())</li>
     * <li>Phone number must be all digits once the "-" delimiter is taken out</li>
     * </ul>
     * If any errors are found, they are collected and displayed to the user in a label below the fields.
     *
     * @return whether or not the input is valid according to business rules and expected data types.
     */
    public boolean isInputValid() {
        if ( !noFieldsEmpty() ) {
            return false;
        }

        String rawPhone = phoneField.getText().replaceAll("-","");
        if (!rawPhone.matches("[0-9]*")) {
            errorLabel.setText(rb.getString("phoneIncorrect"));
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    /**
     * noFieldsEmpty() checks to be sure that every single input field has data in it; nothing should be empty or null.
     *
     * @return whether or not every field had data
     */
    private boolean noFieldsEmpty() {
        if ( nameField.getText().isEmpty()
                || addressField.getText().isEmpty()
                || cityField.getText().isEmpty()
                || countryComboBox.getSelectionModel().getSelectedItem() == null
                || divisionComboBox.getSelectionModel().getSelectedItem() == null
                || postalField.getText().isEmpty()
                || phoneField.getText().isEmpty() ) {
            errorLabel.setText(rb.getString("fieldBlank"));
            return false;
        }
        return true;
    }

    /**
     * addCustomer() executes an update using the DatabaseConnection class that INSERTS a new customer
     * into the customers table in the MySQL database. All data will be validated by the time this is called.
     *
     * @param customer the customer data to insert
     */
    private void addCustomer(Customer customer) {
        List<String> args = Arrays.asList(
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getPhone(),
                customer.getCreateDate().toString(),
                customer.getCreatedBy(),
                customer.getLastUpdate().toString(),
                customer.getLastUpdatedBy(),
                String.valueOf(customer.getDivisionID())
        );
        DatabaseConnection.performUpdate(
                session.getConn(),
                Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "InsertCustomer.sql"),
                args
        );
    }

    /**
     * updateCustomer() executes an update using the DatabaseConnection class that UPDATES an existing customer
     * in the customers table in the MySQL database. All data will be validated by the time this is called.
     *
     * @param customer the customer data to update
     */
    private void updateCustomer(Customer customer) {
        List<String> args = Arrays.asList(
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getPhone(),
                customer.getLastUpdate().toString(),
                customer.getLastUpdatedBy(),
                String.valueOf(customer.getDivisionID()),
                String.valueOf(customer.getCustomerID())
        );
        DatabaseConnection.performUpdate(
                session.getConn(),
                Path.of(Constants.UPDATE_SCRIPT_PATH_BASE + "UpdateCustomer.sql"),
                args
        );
    }

    /**
     * createCustomerData() inserts all the validated data from the form's fields (and the existing customer's
     * data in the case of an update) into a Customer object that will be used to update the MySQL database.
     *
     * @return the Customer object created from the fields' data
     */
    private Customer createCustomerData() {
        int id = -1;
        Timestamp createDate = DateTime.getUTCTimestampNow();
        String createdBy = session.getUsername();

        if (action.equals(Constants.UPDATE)) {
            id = existingCustomer.getCustomerID();
            createDate = existingCustomer.getCreateDate();
            createdBy = existingCustomer.getCreatedBy();
        }

        String name = nameField.getText();
        String address = addressField.getText() + ", " + cityField.getText();
        System.out.println(address);
        String postal = postalField.getText();
        String phone = phoneField.getText();
        Timestamp lastUpdate = DateTime.getUTCTimestampNow();
        String lastUpdatedBy = session.getUsername();

        FirstLevelDivision division = divisionComboBox.getSelectionModel().getSelectedItem();
        int divisionID = division.getDivisionID();

        return new Customer(id, name, address, postal, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionID);
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
     * setExistingCustomer() is used to pass in existing customer data from the main view in the case of an
     * update.
     *
     * @param existingCustomer the existing customer to set
     */
    public void setExistingCustomer(Customer existingCustomer) { this.existingCustomer = existingCustomer; }

    /**
     * cancel() listens for when the "Cancel" button is pressed or is called to close the form after updates.
     * If this is called from the button, then no changes are made to the database.
     */
    public void cancel() { Common.exitWindow(cancelBtn); }
}
