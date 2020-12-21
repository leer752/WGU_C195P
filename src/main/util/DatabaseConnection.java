package main.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>DatabaseConnection</h1>
 * DatabaseConnection class is used to hold credentials for the mySQL database and perform any actions that require
 * the database connection. All queries and updates use this class.
 *
 * @author Lee Rhodes
 */
public class DatabaseConnection {
    private static final String dbName = "WJ07xYg";
    private static final String dbURL = "jdbc:mysql://wgudb.ucertify.com:3306/" + dbName;
    private static final String username = "U07xYg";
    private static final String password = "53689163415";


    /**
     * openConnection() passes the database credentials to the database and opens a connection.
     *
     * @return connection that was established with the database; returns null if there was an error.
     */
    public static Connection openConnection() {
        try {
            Connection conn = DriverManager.getConnection(dbURL, username, password);
            System.out.println("Connection successful.");
            return conn;
        } catch (SQLException e) {
            Common.handleException(e);
            return null;
        }
    }

    /**
     * closeConnection() closes a connection that is passed in.
     * This should be called every time the program closes as a clean-up routine.
     *
     * @param conn the connection that will be closed.
     */
    public static void closeConnection(Connection conn) {
        try {
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            Common.handleException(e);
        }
    }

    /**
     * performQuery() sends a query to the mySQL database via the open connection and returns the result from
     * the query.
     * This does not alter the database.
     *
     * @param conn the connection that will be used to execute the query.
     * @param path the path to the script that will be used for the query.
     * @param args the arguments that will fill in the variables in the script; changes the query.
     * @return the result set that is returned from the query.
     */
    public static ResultSet performQuery(Connection conn, Path path, List<String> args) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(Files.readString(path, StandardCharsets.US_ASCII));
            if (args != null) {
                PreparedStatement preparedWithArgs = setArguments(preparedStatement, args);
                return preparedWithArgs.executeQuery();
            } else {
                return preparedStatement.executeQuery();
            }
        } catch (Exception e) {
            Common.handleException(e);
            return null;
        }
    }

    /**
     * performUpdate() sends an update statement to the mySQL database via the open connection.
     * This alters the database.
     *
     * @param conn the connection that will be used to execute the update.
     * @param path the path to the script that will be used for the update.
     * @param args the arguments that will fill in the variables in the script; changes the update.
     */
    public static void performUpdate(Connection conn, Path path, List<String> args) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(Files.readString(path, StandardCharsets.US_ASCII));
            PreparedStatement preparedWithArgs = setArguments(preparedStatement, args);
            preparedWithArgs.executeUpdate();
        } catch (Exception e) {
            Common.handleException(e);
        }
    }

    /**
     * setArguments() loops through a given list of arguments and sets each variable present in the statement.
     * Each variable must be specified by an item in the list or it will error out.
     *
     * @param statement the statement that the variables will be set in.
     * @param args the arguments that will fill in the variables in the statement.
     * @return the statement with all arguments set.
     */
    public static PreparedStatement setArguments(PreparedStatement statement, List<String> args) {
        AtomicInteger i = new AtomicInteger(1);
        args.forEach(arg -> {
            try {
                statement.setString(i.getAndIncrement(), arg);
            } catch (SQLException e) {
                Common.handleException(e);
            }
        });
        return statement;
    }
}
