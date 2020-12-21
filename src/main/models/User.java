package main.models;

/**
 * <h1>User</h1>
 * The User class is used as a data model for passing data from the MySQL users table into the
 * table views in this program. This model is also used when building data for a user record.
 * <p>
 * Setters are not included as there is no reason for the data to be modified.
 *
 * @author Lee Rhodes
 */
public class User {
    private final int userID;
    private final String username;
    public User(int userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    /**
     * @return the user id; mySQL = "User_ID" PK
     */
    public int getUserID() { return userID; }

    /**
     * @return the username; mySQL = "User_Name"
     */
    public String getUsername() { return username; }
}
