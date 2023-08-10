package SqlDB;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.sql.ResultSet;

/**
 * The LoginValidator class is responsible for validating user login credentials and maintaining user-related data,
 * such as the logged-on user, user locale, and user time zone.
 */
public class LoginValidator {

    public static User getCurrentUser() {
        return CurrentUser;
    }

    public static void setCurrentUser(User currentUser) {
        LoginValidator.CurrentUser = currentUser;
    }

    public static Locale getCurrentLocation() {
        return CurrentLocation;
    }

    public static void setCurrentLocation(Locale currentLocation) {
        LoginValidator.CurrentLocation = currentLocation;
    }

    public static ZoneId getCurrentTimeZone() {
        return CurrentTimeZone;
    }

    /**
     * Sets the current user timezone or defaults to UTC for application use.
     *
     * @param loggedInTimeZone the current users local timezone.
     */
    public static void setCurrentTimeZone(ZoneId loggedInTimeZone) {
        if (loggedInTimeZone != null) {
            LoginValidator.CurrentTimeZone = loggedInTimeZone;
        } else {
            LoginValidator.CurrentTimeZone = ZoneId.of("UTC");
        }
    }


    public static void LogOut() {
        CurrentUser = null;
        CurrentLocation = null;
        CurrentTimeZone = null;
    }


    private static User CurrentUser;
    private static Locale CurrentLocation;
    private static ZoneId CurrentTimeZone;

    /**
     * Validates the given user credentials by querying the database and setting the logged-on user and related properties
     * if the credentials are valid.
     *
     * @param validUser The user name provided during login.
     * @param ValidPassword The ValidPassword provided during login.
     * @return True if the credentials are valid, and the user is successfully logged on; otherwise, false.
     */
    public boolean validateCredentials(String validUser, String ValidPassword) {
        // connect to database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/client_schedule", "sqlUser", "Passw0rd!")) {

            String login = "SELECT User_ID " +
                    "FROM users " +
                    "WHERE User_Name = ? AND Password = ?";
            try (PreparedStatement LogIN = connection.prepareStatement(login)) {

                LogIN.setString(1, validUser);
                LogIN.setString(2, ValidPassword);


                try (ResultSet validusers = LogIN.executeQuery()) {
                    // if a valid combo of username and password
                    if (validusers.next()) {

                        CurrentUser = new User(validUser, validusers.getInt("User_ID"));
                        CurrentLocation = Locale.getDefault();
                        CurrentTimeZone = ZoneId.systemDefault();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // check for valid credentials or return error
    }



    /**
     * The User class represents a user with a user name and user ID.
     */
    public static class User {
        private String UserName;
        private Integer UserNameID;


        /**
         * Constructs a new User object with the provided user name and user ID.
         *
         * @param currentUserName The user name of the user.
         * @param CurrentUserID   The ID of the user.
         */
        public User(String currentUserName, Integer CurrentUserID) {
            UserName = currentUserName;
            UserNameID = CurrentUserID;

        }



        public String getUserName() {
            return UserName;
        }


        public Integer getUserNameID() {
            return UserNameID;
        }
    }
}




