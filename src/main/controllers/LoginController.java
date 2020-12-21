package main.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.models.Session;
import main.models.User;
import main.util.Common;
import main.util.Constants;
import main.util.DatabaseConnection;
import main.util.DateTime;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <h1>LoginController</h1>
 * The LoginController is the first window that opens when the program runs.
 * It is responsible for taking a username and password and verifying them against the MySQL users table.
 * <p>
 * All login attempts are recorded to a text file called "login_activity.txt" in the root folder.
 *
 * @author Lee Rhodes
 */
public class LoginController {
    private Connection conn;
    private int userID;
    private final ResourceBundle rb = ResourceBundle.getBundle(Constants.PROPERTIES_PATH_BASE + "login_" + Locale.getDefault().getLanguage());
    @FXML
    private Label titleLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private Label errorLabel;
    @FXML
    private Label localeTitleLabel;
    @FXML
    private Label localeLabel;

    /**
     * initialize() is responsible for the entry point for the user to the scheduling program. It sets up a log-in
     * screen for the user to enter credentials in for validation.
     */
    public void initialize() {
        this.conn = DatabaseConnection.openConnection();

        Platform.runLater(() -> {
            initializeDisplayText();
            setCharLimitOnFields();
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
        titleLabel.setText(rb.getString("titleLabel"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        passwordLabel.setText(rb.getString("passwordLabel"));
        localeTitleLabel.setText(rb.getString("localeTitle"));
        localeLabel.setText(Locale.getDefault().getDisplayCountry());

        // Button labels
        Common.scaleButton(loginBtn, rb.getString("loginBtn"));
        Common.scaleButton(cancelBtn, rb.getString("cancelBtn"));
    }

    /**
     * setCharLimitOnFields() sends each TextField/TextArea and its maximum length to the Common method setCharLimit().
     * That method adds a listener to the field that limits how many characters the user can enter.
     * <p>
     * Prevents a value being entered that's longer than the MySQL field length.
     */
    private void setCharLimitOnFields() {
        Common.setCharLimit(usernameField, Constants.CHAR_LIMIT_NORMAL);
    }


    /**
     * attemptLogin() is called whenever the "Login" button is clicked. The login credentials that were entered
     * (username and password) are checked against the MySQL database users table. If the given username has a
     * matching password result, then the session is created and the user is taken to the main menu.
     */
    @FXML
    public void attemptLogin() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String pwd = passwordField.getText();

        if (username.isEmpty() || username.isBlank() || pwd.isEmpty() || pwd.isBlank()) {
            errorLabel.setText(rb.getString("credentialsEmpty"));
        } else if (credentialsValid(username,pwd)) {
            try {
                Session session = new Session(conn, new User(userID, username), DateTime.getUTCTimestampNow());

                Stage currStage = (Stage) loginBtn.getScene().getWindow();
                currStage.hide();

                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH_BASE + "Scheduler.fxml"));
                Parent parent = loader.load();
                SchedulerController controller = loader.getController();
                controller.setSession(session);

                Scene scene = new Scene(parent);

                stage.setMinWidth(1097);
                stage.setMinHeight(898);
                stage.setScene(scene);
                stage.showAndWait();
                usernameField.clear();
                passwordField.clear();
                currStage.show();
            } catch (Exception e) {
                Common.handleException(e);
            }
        }
    }

    /**
     * credentialsValid() executes an SQL query to find any passwords that are linked with the username that was
     * entered by the user. If there is a password that matches the password that the user entered, the credentials
     * are valid and they can move into the main menu.
     *
     * @param username the username that was entered
     * @param pwd the password that was entered
     * @return whether or not the username and password entered were valid
     */
    private boolean credentialsValid(String username, String pwd) {
        ResultSet rs = DatabaseConnection.performQuery(
                conn,
                Path.of(Constants.QUERY_SCRIPT_PATH_BASE + "SelectUserByUsername.sql"),
                Collections.singletonList(username)
        );

        try {
            boolean foundUser = false;
            if (rs != null) {
                while (rs.next()) {
                    if (rs.getString("Password").equals(pwd)) {
                        this.userID = rs.getInt("User_ID");
                        logAttempt(username, Constants.SUCCESS, Constants.LOGIN_SUCCESS_MSG);
                        return true;
                    } else {
                        foundUser = true;
                    }
                }
                if ( foundUser ) {
                    logAttempt(username, Constants.FAILURE, Constants.WRONG_PASSWORD_MSG);
                } else {
                    logAttempt(username, Constants.FAILURE, Constants.USER_NOT_FOUND_MSG);
                }
            }
        } catch (Exception e) {
            Common.handleException(e);
        }
        errorLabel.setText(rb.getString("credentialsIncorrect"));
        return false;
    }

    /**
     * logAttempt() writes to a file named "login_activity" with details about the log-in attempt. It records the
     * username entered, the time the log-in was attempted, the result of the attempt (success or failure), and what
     * happened (logged in, no user found, or incorrect password).
     *
     * @param username the username that was entered
     * @param result the result from the attempt (success or failure)
     * @param message the details about the result (logged in, no user found, or incorrect password)
     */
    private void logAttempt(String username, String result, String message) {
        String log = "[" + result + "] <" + username + "> @ " + DateTime.getUTCTimestampNow() + "; Message: " + message;

        try {
            final Path logPath = Paths.get(Constants.LOGIN_ACTIVITY_PATH);
            Files.write(logPath, Collections.singletonList(log), StandardCharsets.UTF_8,
                    Files.exists(logPath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (Exception e) {
            Common.handleException(e);
        }

    }

    /**
     * cancel() listens for when the "Cancel" button is pressed. Once it is, it closes the database connection and also
     * the main window which will end the program.
     */
    public void cancel() {
        DatabaseConnection.closeConnection(conn);
        Common.exitWindow(cancelBtn);
    }
}
