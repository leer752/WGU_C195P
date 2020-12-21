package main.models;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * <h1>Session</h1>
 * The Session class is used as a data model to hold information about the current user's session.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class Session {
    private final Connection conn;
    private final User user;
    private final Timestamp loginTime;
    public Session(Connection conn, User user, Timestamp loginTime) {
        this.conn = conn;
        this.user = user;
        this.loginTime = loginTime;
    }

    /**
     * @return the connection
     */
    public Connection getConn() { return conn; }

    /**
     * @return the current user's username; mySQL = "User_Name"
     */
    public String getUsername() { return user.getUsername(); }

    /**
     * @return the current user's ID; mySQL = "User_ID"
     */
    public int getUserID() { return user.getUserID(); }

    /**
     * @return the time that the user successfully logged in
     */
    public Timestamp getLoginTime() { return loginTime; }
}
