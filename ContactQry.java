package SqlDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The ContactQry class provides methods to query and retrieve data related to contacts and appointments.
 */
public class ContactQry {

    /**
     * Retrieves a list of appointments for a specific contact based on their contact ID.
     *
     * @param Contact The ID of the contact for which to retrieve the appointments.
     * @return An ObservableList of appointment details as formatted strings.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<String> getContactAppts(String Contact) throws SQLException {
        ObservableList<String> apptStr = FXCollections.observableArrayList();
        PreparedStatement contacts = JDBC.connection.prepareStatement(
                "SELECT * " +
                        "FROM appointments " +
                        "WHERE Contact_ID = ?");

        contacts.setString(1, Contact);

        ResultSet contactAppts = contacts.executeQuery();

        while ( contactAppts.next()) {
            String apptID = contactAppts.getString("Appointment_ID");
            String ApptTitle = contactAppts.getString("Title");
            String ApptType = contactAppts.getString("Type");
            String start = contactAppts.getString("Start");
            String end = contactAppts.getString("End");
            String customerIDNum = contactAppts.getString("Customer_ID");

            String contactAppointments = "  AppointmentID: " + apptID + "\n";
            contactAppointments += "        Title: " + ApptTitle + "\n";
            contactAppointments += "        Type: " + ApptType + "\n";
            contactAppointments += "        Start date/time: " + start + " UTC\n";
            contactAppointments += "        End date/time: " + end + " UTC\n";
            contactAppointments += "        CustomerID: " + customerIDNum + "\n";

            apptStr.add(contactAppointments);

        }

        contacts.close();
        return apptStr;

    }

    /**
     * Retrieves a list of all contact names from the database.
     *
     * @return An ObservableList of all contact names.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<String> AllContacts() throws SQLException {
        ObservableList<String> allContacts = FXCollections.observableArrayList();
        PreparedStatement contactNames = JDBC.connection.prepareStatement(
                "SELECT Contact_Name " +
                        "FROM contacts " +
                        "GROUP BY Contact_Name"
        );

        ResultSet names = contactNames.executeQuery();

        while ( names.next() ) {
            allContacts.add(names.getString("Contact_Name"));
        }
        contactNames.close();
        return allContacts;
    }

    /**
     * Finds the contact ID for a specific contact name.
     *
     * @param Name The name of the contact for which to retrieve the contact ID.
     * @return The contact ID corresponding to the given contact name.
     * @throws SQLException If a database access error occurs.
     */
    public static Integer retrieveContactID(String Name) throws SQLException {

        Integer contactID = -1;
        PreparedStatement contact = JDBC.connection.prepareStatement(
                "SELECT Contact_ID, Contact_Name " +
                        "FROM contacts " +
                        "WHERE Contact_Name = ?"
        );

        contact.setString(1, Name);
        ResultSet contactName = contact.executeQuery();

        while (contactName.next()) {

            contactID = contactName.getInt("Contact_ID");
        }
        contact.close();
        return contactID;


    }

    /**
     * Get the count of appointments scheduled for each contact type.
     *
     * @return An ObservableList of strings representing the report data.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public static ObservableList<String> totalApptsByContactType() throws SQLException {
        ObservableList<String> apptsbycontactType = FXCollections.observableArrayList();

        // SQL query to get the count of appointments for each contact type
        String sql = "SELECT c.Contact_Type, COUNT(a.Appointment_ID) AS Appointment_Count " +
                "FROM appointments a " +
                "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                "GROUP BY c.Contact_Type " +
                "ORDER BY c.Contact_Type";

        try (PreparedStatement contactTypes = JDBC.connection.prepareStatement(sql);
             ResultSet apptContactTypes = contactTypes.executeQuery()) {

            while (apptContactTypes.next()) {
                String typeofContact = apptContactTypes.getString("Contact_Type");
                int totalAppointments = apptContactTypes.getInt("Appointment_Count");
                String toReport = "The " + typeofContact + " contacts have " + totalAppointments +" appointments "+ "\n";
                apptsbycontactType.add(toReport);
            }
        }

        return apptsbycontactType;
    }
}

