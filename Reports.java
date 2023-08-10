package c195.sw2;

import SqlDB.AppointmentQry;
import SqlDB.ContactQry;
import SqlDB.UserQry;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.Node;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import static java.lang.Integer.parseInt;


/**
 * The ReportsController class handles generating and displaying various reports related to appointments and contacts.
 */
public class Reports implements Initializable {
    public RadioButton ApptByReport;
    public RadioButton contactsScheduled;

    public Button back;
    public TextArea reportPrint;
    public RadioButton ApptsPerUser;
    public Button clearReports;
    public AnchorPane ancor;

    public void changeScreen (ActionEvent event, String newView) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(newView));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Handles the "Appointments by Report" button press event to generate and display a report of appointments grouped by type and month.
     *
     * @param actionEvent The ActionEvent that triggered the button press.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public void report1(ActionEvent actionEvent) throws SQLException {

        ObservableList<String> monthReport = AppointmentQry.monthlyReport();

        for (String str : monthReport) {
            reportPrint.appendText(str);
        }

    }

    /**
     * Handles the "Contact Schedule Report" button press event to generate and display a report of appointments for each contact.
     *
     * @param actionEvent The ActionEvent that triggered the button press.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public void report2(ActionEvent actionEvent) throws SQLException {

        ObservableList<String> contacts = ContactQry.AllContacts();

        for (String contact : contacts) {
            String contactID = ContactQry.retrieveContactID(contact).toString();
            reportPrint.appendText("Contact Name: " + contact + " ID: " + contactID + "\n");

            ObservableList<String> appts = ContactQry.getContactAppts(contactID);
            if(appts.isEmpty()) {
                reportPrint.appendText("    No appointments for contact \n");
            }
            for (String appt : appts) {
                reportPrint.appendText(appt);
            }

        }
    }

    /**
     * Handles the "Minutes per Contact" button press event to generate and display a report of total minutes scheduled for each contact.
     *
     * @param actionEvent The ActionEvent that triggered the button press.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public void report3(ActionEvent actionEvent) throws SQLException {
        // Implementation for the "Total Appointments per User" report
        ObservableList<Integer> users = UserQry.UserIDs();

        for (Integer userID : users) {
            String userName = UserQry.User(parseInt(String.valueOf(userID)));
            ObservableList<String> totalAppointments = AppointmentQry.ApptsByUserReport(parseInt(String.valueOf(userID)));
            reportPrint.appendText("User Name: " + userName + " (ID: " + userID + ") - Total Appointments: " + totalAppointments + "\n");
        }
    }

    public void back(ActionEvent actionEvent) throws IOException {
        changeScreen(actionEvent, "AppointmentView.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void clearReports(ActionEvent actionEvent) {
        reportPrint.clear();
    }
}
