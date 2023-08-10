package SqlDB;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The UserQry class provides methods to retrieve information related to users from the database.
 */
public class UserQry {
    /**
     * Retrieves a list of all user IDs from the database.
     *
     * @return An ObservableList of integers representing all the user IDs in the database.
     * @throws SQLException If an error occurs while querying the database.
     */
    public static ObservableList<Integer> UserIDs() throws SQLException {
        ObservableList<Integer> userids = FXCollections.observableArrayList();
        PreparedStatement listIds = JDBC.connection.prepareStatement(
                "SELECT User_ID" +
                " FROM users GROUP BY User_ID;");

        ResultSet ids = listIds.executeQuery();

        while ( ids.next() ) {
            userids.add(ids.getInt("User_ID"));
        }
        listIds.close();
        return userids;
    }


    /**
     * Retrieves the user name based on the given user ID.
     *
     * @param UserIDNum The ID of the user whose name is to be retrieved.
     * @return The user name as a String.
     * @throws SQLException If an error occurs while querying the database.
     */
    public static String User(int UserIDNum) throws SQLException {
        String user_Name = "";
        PreparedStatement currentuser = JDBC.connection.prepareStatement(
                "SELECT User_Name " +
                "FROM users " +
                "WHERE User_ID = ?");
        currentuser.setInt(1, UserIDNum);
        ResultSet user = currentuser.executeQuery();

        if (user.next()) {
            user_Name = user.getString("User_Name");
        }

        currentuser.close();
        return user_Name;
    }
}
