package SqlDB;


import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The AppointmentQry class contains methods for querying and manipulating appointments in the database.
 */
public class AppointmentQry {

    /**
     * Queries for appointments within the specified date range.
     *
     * @param first The start date and time of the range.
     * @param last   The end date and time of the range.
     * @return An ObservableList of AppointmentStr objects representing the filtered appointments.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<AppointmentStr> ApptsByDate(ZonedDateTime first, ZonedDateTime last)
            throws SQLException {

        ObservableList<AppointmentStr> filteredAppts = FXCollections.observableArrayList();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement allappointments = JDBC.connection.prepareStatement(
                "SELECT a.*, c.Contact_Name FROM appointments AS a LEFT OUTER JOIN contacts AS c ON a.Contact_ID = c.Contact_ID WHERE a.Start BETWEEN ? AND ?"
        );


        String startday = first.format(date);
        String lastday = last.format(date);

        allappointments.setString(1, startday);
        allappointments.setString(2, lastday);

        ResultSet Days = allappointments.executeQuery();

        while( Days.next() ) {
            Integer ApptID = Days.getInt("Appointment_ID");
            String AptName = Days.getString("Title");
            String Notes = Days.getString("Description");
            String location = Days.getString("Location");
            String type = Days.getString("Type");
            Timestamp begin = Days.getTimestamp("Start");
            Timestamp end = Days.getTimestamp("End");
            Timestamp scheduled = Days.getTimestamp("Create_Date");
            String scheduledby = Days.getString("Created_by");
            Timestamp updated = Days.getTimestamp("Last_Update");
            String updatedby = Days.getString("Last_Updated_By");
            Integer customerNum = Days.getInt("Customer_ID");
            Integer userNum = Days.getInt("User_ID");
            Integer contactNum = Days.getInt("Contact_ID");
            String Name = Days.getString("Contact_Name");


            AppointmentStr newAppt = new AppointmentStr(
                    ApptID, AptName, Notes, location, type, begin, end, scheduled,
                    scheduledby, updated, updatedby, customerNum, userNum, contactNum, Name
            );
            filteredAppts.add(newAppt);
        }

        allappointments.close();
        return filteredAppts;

    }

    /**
     * Generates a report of total appointment counts by type and month.
     *
     * @return An ObservableList of Strings representing the report lines.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<String> monthlyReport() throws SQLException {
        ObservableList<String> Monthlyreport = FXCollections.observableArrayList();


        Monthlyreport.add("Total amount of appointments scheduled arranged by type and month:\n");


        PreparedStatement ApptType = JDBC.connection.prepareStatement(
                "SELECT Type, COUNT(Type) as \"Total\" FROM appointments GROUP BY Type");

        PreparedStatement ApptMonth = JDBC.connection.prepareStatement(
                "SELECT DATE_FORMAT(Start, '%Y-%m') AS Month, COUNT(*) AS Total " +
                        "FROM appointments " +
                        "GROUP BY DATE_FORMAT(Start, '%Y-%m')"
        );

        ResultSet typeResults = ApptType.executeQuery();
        ResultSet monthResults = ApptMonth.executeQuery();

        while (typeResults.next()) {
            String apptType = "Type: " + typeResults.getString("Type") + " Count: " +
                    typeResults.getString("Total") + "\n";
            Monthlyreport.add(apptType);

        }

        while (monthResults.next()) {
            String Monthtotal = "Month: " + monthResults.getString("Month") + " Count: " +
                    monthResults.getString("Total") + "\n";
            Monthlyreport.add(Monthtotal);

        }

        ApptMonth.close();
        ApptType.close();

        return Monthlyreport;

    }

    /**
     * Queries for appointments for a specific customer on a given date.
     *
     * @param apptDate         The date for which appointments are queried.
     * @param customerIDNumber  The ID of the customer for whom the appointments are queried.
     * @return An ObservableList of AppointmentStr objects representing the filtered appointments.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<AppointmentStr> AppointmentsByCustomer(
            LocalDate apptDate, Integer customerIDNumber) throws SQLException {

        ObservableList<AppointmentStr> filteredAppts = FXCollections.observableArrayList();

        PreparedStatement CustomerAppts = JDBC.connection.prepareStatement(
                "SELECT a.*, c.* " +
                        "FROM appointments AS a " +
                        "LEFT OUTER JOIN contacts AS c ON a.Contact_ID = c.Contact_ID " +
                        "WHERE DATE(a.Start) = ? AND a.Customer_ID = ?"
        );


        CustomerAppts.setInt(2, customerIDNumber);

        CustomerAppts.setString(1, apptDate.toString());

        ResultSet customersAppts = CustomerAppts.executeQuery();

        while( customersAppts.next() ) {
            Integer ApptID = customersAppts.getInt("Appointment_ID");
            String AptName = customersAppts.getString("Title");
            String Notes = customersAppts.getString("Description");
            String location = customersAppts.getString("Location");
            String type = customersAppts.getString("Type");
            Timestamp begin = customersAppts.getTimestamp("Start");
            Timestamp end = customersAppts.getTimestamp("End");
            Timestamp scheduled = customersAppts.getTimestamp("Create_Date");
            String scheduledby = customersAppts.getString("Created_by");
            Timestamp updated = customersAppts.getTimestamp("Last_Update");
            String updatedby = customersAppts.getString("Last_Updated_By");
            Integer customerNum = customersAppts.getInt("Customer_ID");
            Integer userNum = customersAppts.getInt("User_ID");
            Integer contactNum = customersAppts.getInt("Contact_ID");
            String Name = customersAppts.getString("Contact_Name");


            AppointmentStr newAppt = new AppointmentStr(
                    ApptID, AptName, Notes, location, type, begin, end, scheduled,
                    scheduledby, updated, updatedby, customerNum, userNum, contactNum, Name
            );
            filteredAppts.add(newAppt);
        }

        CustomerAppts.close();
        return filteredAppts;

    }

    /**
     * Updates an existing appointment in the database.
     *
     * @param apptID        The ID of the appointment to update.
     * @param apptTitle         The updated title of the appointment.
     * @param Notes   The updated description of the appointment.
     * @param Location      The updated location of the appointment.
     * @param apptType          The updated type of the appointment.
     * @param ApptStart         The updated start date and time of the appointment.
     * @param ApptEnd           The updated end date and time of the appointment.
     * @param UpdatedLast  The username of the user performing the update.
     * @param CustomerID    The ID of the customer associated with the appointment.
     * @param UserIDNum        The ID of the user associated with the appointment.
     * @param ContactIDNum     The ID of the contact associated with the appointment.
     * @return True if the update is successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static Boolean updateAppointment(Integer apptID, String apptTitle, String Notes,
                                            String Location, String apptType, ZonedDateTime ApptStart,
                                            ZonedDateTime ApptEnd, String UpdatedLast, Integer CustomerID,
                                            Integer UserIDNum, Integer ContactIDNum) throws SQLException {

        PreparedStatement ApptUpdates = JDBC.connection.prepareStatement(
                "UPDATE appointments " +
                        "SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                        "Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
                        "Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                        "WHERE Appointment_ID = ?"
        );


        // Format ApptStart and ApptEnd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String inputStartString = ApptStart.format(formatter).toString();
        String inputEndString = ApptEnd.format(formatter).toString();

        ApptUpdates.setString(1,apptTitle);
        ApptUpdates.setString(2, Notes);
        ApptUpdates.setString(3, Location);
        ApptUpdates.setString(4, apptType);
        ApptUpdates.setString(5, inputStartString);
        ApptUpdates.setString(6, inputEndString);
        ApptUpdates.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter).toString());
        ApptUpdates.setString(8, UpdatedLast);
        ApptUpdates.setInt(9, CustomerID);
        ApptUpdates.setInt(10, UserIDNum);
        ApptUpdates.setInt(11, ContactIDNum);
        ApptUpdates.setInt(12, apptID);

        try {
            ApptUpdates.executeUpdate();
            ApptUpdates.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            ApptUpdates.close();
            return false;
        }

    }

    /**
     * Adds a new appointment to the database.
     *
     * @param ApptTitle        The title of the appointment.
     * @param ApptNotes  The description of the appointment.
     * @param ApptLocation     The location of the appointment.
     * @param ApptType         The type of the appointment.
     * @param ApptStart        The start date and time of the appointment.
     * @param ApptEnd          The end date and time of the appointment.
     * @param CreatedBy    The username of the user who created the appointment.
     * @param UpdatedBy The username of the user performing the last update.
     * @param CustomerIDNum   The ID of the customer associated with the appointment.
     * @param UserIDNum       The ID of the user associated with the appointment.
     * @param ContactIDNum    The ID of the contact associated with the appointment.
     * @return True if the addition is successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static Boolean addAppointment(String ApptTitle, String ApptNotes,
                                         String ApptLocation, String ApptType, ZonedDateTime ApptStart,
                                         ZonedDateTime ApptEnd, String CreatedBy,
                                         String UpdatedBy, Integer CustomerIDNum,
                                         Integer UserIDNum, Integer ContactIDNum) throws SQLException {

        PreparedStatement AddAppts = JDBC.connection.prepareStatement(
                "INSERT INTO appointments (" +
                        "    Title, Description, Location, Type, Start, End, Create_date, " +
                        "    Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );


        DateTimeFormatter apptTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String begin = ApptStart.format(apptTime).toString();
        String end = ApptEnd.format(apptTime).toString();


        AddAppts.setString(1, ApptTitle);
        AddAppts.setString(2, ApptNotes);
        AddAppts.setString(3, ApptLocation);
        AddAppts.setString(4, ApptType);
        AddAppts.setString(5, begin);
        AddAppts.setString(6, end);
        AddAppts.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(apptTime).toString());
        AddAppts.setString(8, CreatedBy);
        AddAppts.setString(9, ZonedDateTime.now(ZoneOffset.UTC).format(apptTime).toString());
        AddAppts.setString(10, UpdatedBy);
        AddAppts.setInt(11, CustomerIDNum);
        AddAppts.setInt(12, UserIDNum);
        AddAppts.setInt(13, ContactIDNum);


        try {
            AddAppts.executeUpdate();
            AddAppts.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            return false;
        }

    }

    /**
     * Deletes an appointment from the database.
     *
     * @param ApptID The ID of the appointment to delete.
     * @return True if the deletion is successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static Boolean deleteAppt(Integer ApptID) throws SQLException {

        PreparedStatement delete = JDBC.connection.prepareStatement(
                "DELETE FROM appointments " +
                        "WHERE Appointment_ID = ?"
        );


        delete.setInt(1, ApptID);

        try {
            delete.executeUpdate();
            delete.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            return false;
        }

    }

    /**
     * Deletes all appointments associated with a specific customer.
     *
     * @param CustomerIDNum The ID of the customer for whom appointments are to be deleted.
     * @return True if the deletion is successful, false otherwise.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static Boolean DeleteCustomerAssociatedAppts(Integer CustomerIDNum) throws SQLException {

        PreparedStatement deleteByID = JDBC.connection.prepareStatement(
                "DELETE FROM appointments " +
                        "WHERE Customer_ID = ?"
        );


        deleteByID.setInt(1, CustomerIDNum);

        try {
            deleteByID.executeUpdate();
            deleteByID.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            return false;
        }

    }

    /**
     * Retrieves a list of all appointments from the database.
     *
     * @return An ObservableList of AppointmentStr objects representing all appointments.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<AppointmentStr> ViewAllAppts() throws SQLException {


        ObservableList<AppointmentStr> AllAppts = FXCollections.observableArrayList();
        PreparedStatement allAppts = JDBC.connection.prepareStatement(
                "SELECT a.*, c.* " +
                        "FROM appointments a " +
                        "LEFT OUTER JOIN contacts c ON a.Contact_ID = c.Contact_ID;"
        );

        ResultSet allappts = allAppts.executeQuery();


        while( allappts.next() ) {

            Integer ApptID = allappts.getInt("Appointment_ID");
            String AptName = allappts.getString("Title");
            String Notes = allappts.getString("Description");
            String location = allappts.getString("Location");
            String type = allappts.getString("Type");
            Timestamp begin = allappts.getTimestamp("Start");
            Timestamp end = allappts.getTimestamp("End");
            Timestamp scheduled = allappts.getTimestamp("Create_Date");
            String scheduledby = allappts.getString("Created_by");
            Timestamp updated = allappts.getTimestamp("Last_Update");
            String updatedby = allappts.getString("Last_Updated_By");
            Integer customerNum = allappts.getInt("Customer_ID");
            Integer userNum = allappts.getInt("User_ID");
            Integer contactNum = allappts.getInt("Contact_ID");
            String Name = allappts.getString("Contact_Name");


            AppointmentStr newAppt = new AppointmentStr(
                    ApptID, AptName, Notes, location, type, begin, end, scheduled,
                    scheduledby, updated, updatedby, customerNum, userNum, contactNum, Name
            );

            // see all appts with new appt
            AllAppts.add(newAppt);

        }
        allAppts.close();
        return AllAppts;

    }

    /**
     * Retrieves the total number of appointments for each user from the database.
     *
     * @return An ObservableList of strings representing the total number of appointments for each user.
     * Each string will be in the format "User Name: [userName], Total Appointments: [totalAppointments]".
     * @throws SQLException If an error occurs while querying the database.
     */
    public static ObservableList<String> ApptsByUserReport(int i) throws SQLException {
        ObservableList<String> ApptsbyUser = FXCollections.observableArrayList();


        String userAppts = "SELECT u.User_ID, u.User_Name, COUNT(*) AS Total_Appointments " +
                "FROM appointments a " +
                "JOIN users u ON a.User_ID = u.User_ID " +
                "GROUP BY u.User_ID, u.User_Name " +
                "ORDER BY u.User_Name";

        try (PreparedStatement UserReport = JDBC.connection.prepareStatement(userAppts);
             ResultSet userapptsReport = UserReport.executeQuery()) {

            while (userapptsReport.next()) {
                int UserIDNum = userapptsReport.getInt("User_ID");
                String User = userapptsReport.getString("User_Name");
                int ApptsTotal = userapptsReport.getInt("Total_Appointments");

                String reportLine = "The User : " + User + ", has a total of  " + ApptsTotal +" scheduled appointments."+ "\n";
                ApptsbyUser.add(reportLine);
            }
        }

        return ApptsbyUser;
    }

    /**
     * Retrieves a list of appointments scheduled within the next 15 minutes for the currently logged-in user.
     *
     * @return An ObservableList of AppointmentStr objects representing the appointments within the next 15 minutes.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<AppointmentStr> upcomingAppts() throws SQLException{

        ObservableList<AppointmentStr> allAppts = FXCollections.observableArrayList();

        DateTimeFormatter appTimes = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // UTC from local
        LocalDateTime current = LocalDateTime.now();
        ZonedDateTime userTime = current.atZone(LoginValidator.getCurrentTimeZone());
        ZonedDateTime currentUTC = userTime.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime UTCwithrange = currentUTC.plusMinutes(15);


        String beginTime = currentUTC.format(appTimes).toString();
        String endTime = UTCwithrange.format(appTimes).toString();
        Integer logonUserID = LoginValidator.getCurrentUser().getUserNameID();


        PreparedStatement fifteenfromNow = JDBC.connection.prepareStatement(
                "SELECT a.*, c.* " +
                        "FROM appointments a, contacts c " +
                        "WHERE a.Contact_ID = c.Contact_ID " +
                        "AND a.Start BETWEEN ? AND ? " +
                        "AND a.User_ID = ?"
        );


        fifteenfromNow.setString(1, beginTime);
        fifteenfromNow.setString(2, endTime);
        fifteenfromNow.setInt(3, logonUserID);

        ResultSet upcoming = fifteenfromNow.executeQuery();

        while( upcoming.next() ) {

            Integer ApptID = upcoming.getInt("Appointment_ID");
            String AptName = upcoming.getString("Title");
            String Notes = upcoming.getString("Description");
            String location = upcoming.getString("Location");
            String type = upcoming.getString("Type");
            Timestamp begin = upcoming.getTimestamp("Start");
            Timestamp end = upcoming.getTimestamp("End");
            Timestamp scheduled = upcoming.getTimestamp("Create_Date");
            String scheduledby = upcoming.getString("Created_by");
            Timestamp updated = upcoming.getTimestamp("Last_Update");
            String updatedby = upcoming.getString("Last_Updated_By");
            Integer customerNum = upcoming.getInt("Customer_ID");
            Integer userNum = upcoming.getInt("User_ID");
            Integer contactNum = upcoming.getInt("Contact_ID");
            String Name = upcoming.getString("Contact_Name");


            AppointmentStr newAppt = new AppointmentStr(
                    ApptID, AptName, Notes, location, type, begin, end, scheduled,
                    scheduledby, updated, updatedby, customerNum, userNum, contactNum, Name
            );

            // update observable list
            allAppts.add(newAppt);

        }
        return allAppts;

    }
}
