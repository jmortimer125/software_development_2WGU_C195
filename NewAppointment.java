package c195.sw2;

import SqlDB.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * Controller class for adding new appointments in the application.
 */
public class NewAppointment implements Initializable {
    public TextField ApptIDField;
    public TextField ApptTitle;
    public TextArea ApptDescription;
    public TextField ApptLocation;
    public ComboBox ContactSelect;
    public TextField ApptType;
    public ComboBox CustomerSelect;
    public ComboBox UserSelect;
    public DatePicker ApptCalender;
    public TextField ApptStart;
    public TextField ApptEnd;
    public Button Save;
    public Button Clear;
    public Button back;
    public Label Localelabel;
    public Label OpenHours;

    ZoneId zone = ZoneId.systemDefault(); // Initialize with system default time zone


    public void changeScreen (ActionEvent event, String newview) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(newview));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void Save(ActionEvent actionEvent) throws SQLException, IOException {



        Boolean validstart = true;
        Boolean validend = true;
        Boolean validOverlap = true;
        Boolean validhours = true;
        String errorMessage = "";


        String title = ApptTitle.getText();
        String description = ApptDescription.getText();
        String location = ApptLocation.getText();
        String contactName = (String) ContactSelect.getValue();
        String type = ApptType.getText();
        Integer customerID = (Integer) CustomerSelect.getValue();
        Integer userID = (Integer) UserSelect.getValue();
        LocalDate apptDate = ApptCalender.getValue();
        LocalDateTime localEndDate = null;
        LocalDateTime localStartDate = null;
        ZonedDateTime Zoneendtime = null;
        ZonedDateTime zoneStartTime = null;


        Integer contactIDNum = ContactQry.retrieveContactID(contactName);


        DateTimeFormatter date = DateTimeFormatter.ofPattern("HH:mm");


        // Make sure date parse is correct.
        try {
            localStartDate = LocalDateTime.of(ApptCalender.getValue(),
                    LocalTime.parse(ApptStart.getText(), date));
            validstart = true;
        }
        catch(DateTimeParseException error) {
            validstart = false;
            errorMessage += "Improper start time format. Please use 4 digit HH:MM.\n";
        }

        try {
            localEndDate = LocalDateTime.of(ApptCalender.getValue(),
                    LocalTime.parse(ApptEnd.getText(), date));
            validend = true;
        }
        catch(DateTimeParseException error) {
            validend = false;
            errorMessage += "Improper end time format. Please use 4 digit HH:MM.\n";
        }

        // Validate all Fields
        if (title.isBlank() || description.isBlank() || location.isBlank() || contactName == null || type.isBlank() ||
                customerID == null || userID == null || apptDate == null || localEndDate == null ||
                localStartDate == null) {

            errorMessage += "Please enter values in all fields.\n";
            // Throw error
            ButtonType error = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert missingValues = new Alert(Alert.AlertType.WARNING, errorMessage, error);
            missingValues.showAndWait();
            return;

        }

        // INPUT VALIDATION: check that business hours are valid and there are no double booked customers.
        validhours = ValidBusinessHours(localStartDate, localEndDate, apptDate);
        validOverlap = doubleBookings(customerID, localStartDate, localEndDate, apptDate);

        //Set error for invalid information
        if (!validhours) {
            errorMessage += "Invalid Business Hours.(8am to 10pm EST)\n";
        }
        if (!validOverlap) {
            errorMessage += "Invalid Customer Overlap. Cannot double book customers.\n";
        }

        System.out.println(errorMessage);


        if (!validOverlap || !validhours || !validend || !validstart) {
            ButtonType validappt = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert invalidAppt = new Alert(Alert.AlertType.WARNING, errorMessage, validappt);
            invalidAppt.showAndWait();
            return;

        }
        else {


            // Transform local user time into zone time for UTC
            zoneStartTime = ZonedDateTime.of(localStartDate, zone);
            Zoneendtime = ZonedDateTime.of(localEndDate, zone);
            String loggedOnUserName = LoginValidator.getCurrentUser().getUserName();

            // Convert to UTC
            zoneStartTime = zoneStartTime.withZoneSameInstant(ZoneOffset.UTC);
            Zoneendtime = Zoneendtime.withZoneSameInstant(ZoneOffset.UTC);

            // Add appt to DB
            Boolean success = AppointmentQry.addAppointment(title, description, location, type, zoneStartTime,
                    Zoneendtime, loggedOnUserName, loggedOnUserName, customerID, userID, contactIDNum );


            if (success) {
                ButtonType added = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                Alert addSuccessful = new Alert(Alert.AlertType.CONFIRMATION, "Appointment added successfully!", added);
                addSuccessful.showAndWait();
                changeScreen(actionEvent, "AppointmentView.fxml");
            }
            else {
                ButtonType notAdded = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                Alert failedtoAdd = new Alert(Alert.AlertType.WARNING, "Failed to add appointment", notAdded);
                failedtoAdd.showAndWait();
            }

        }

    }

    public void Clear() {
        ApptTitle.clear();
        ApptDescription.clear();
        ApptLocation.clear();
        ApptType.clear();
        ApptStart.clear();
        ApptEnd.clear();
        ContactSelect.getSelectionModel().clearSelection();
        CustomerSelect.getSelectionModel().clearSelection();
        UserSelect.getSelectionModel().clearSelection();
        ApptCalender.getEditor().clear();


    }


    public void Back(ActionEvent actionEvent) throws IOException {
        changeScreen(actionEvent, "AppointmentView.fxml");
    }

    /**
     * Validates whether the appointment time falls within business hours and not overlapping with other appointments.
     *
     * @param startDateTime The start time of the appointment in LocalDateTime format.
     * @param endDateTime   The end time of the appointment in LocalDateTime format.
     * @param apptDate      The date of the appointment in LocalDate format.
     * @return True if the appointment time is valid; otherwise, false.
     */
    public Boolean ValidBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate apptDate) {
        // (8am to 10pm EST, including weekends)
        // evaluate whatever time was entered in usertime zone against EST

        ZonedDateTime zoneTimeBegin = ZonedDateTime.of(startDateTime, zone);
        ZonedDateTime zoneTimeEnd = ZonedDateTime.of(endDateTime, zone);

        ZonedDateTime BusinessOpen = ZonedDateTime.of(apptDate, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime businessClosed = ZonedDateTime.of(apptDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));


        // logical business hour checks
        if (zoneTimeBegin.isBefore(BusinessOpen) | zoneTimeBegin.isAfter(businessClosed) |
                zoneTimeEnd.isBefore(BusinessOpen) | zoneTimeEnd.isAfter(businessClosed) |
                zoneTimeBegin.isAfter(zoneTimeEnd)) {
            return false;

        }
        else {
            return true;
        }

    }

    /**
     * Validates whether the customer has overlapping appointments on the given date.
     *
     * @param inputCustomerID The ID of the customer to check for overlaps.
     * @param startDateTime   The start time of the new appointment in LocalDateTime format.
     * @param endDateTime     The end time of the new appointment in LocalDateTime format.
     * @param apptDate        The date of the new appointment in LocalDate format.
     * @return True if there are no overlaps; otherwise, false.
     * @throws SQLException If an SQL error occurs during the database interaction.
     */
    public Boolean doubleBookings(Integer inputCustomerID, LocalDateTime startDateTime,
                                  LocalDateTime endDateTime, LocalDate apptDate) throws SQLException {

        // Get list of appointments that might have conflicts
        ObservableList<AppointmentStr> possibleConflicts = AppointmentQry.AppointmentsByCustomer(apptDate,
                inputCustomerID);
        // for each possible conflict, evaluate:
        // if conflictApptStart is before newApptstart and conflictApptEnd is after newApptStart(starts before ends after)
        // if conflictApptStart is before newApptEnd & conflictApptStart after newApptStart (startime anywhere in appt)
        // if endtime is before end and endtime is after start (endtime falls anywhere in appt)
        if (possibleConflicts.isEmpty()) {
            return true;
        }
        else {
            for (AppointmentStr conflictAppt : possibleConflicts) {

                LocalDateTime conflictStart = conflictAppt.getApptStart().toLocalDateTime();
                LocalDateTime conflictEnd = conflictAppt.getApptEnd().toLocalDateTime();

                // Conflict starts before and Conflict ends any time after new appt ends - overlap
                if( conflictStart.isBefore(startDateTime) & conflictEnd.isAfter(endDateTime)) {
                    return false;
                }
                // ConflictAppt start time falls anywhere in the new appt
                if (conflictStart.isBefore(endDateTime) & conflictStart.isAfter(startDateTime)) {
                    return false;
                }
                // ConflictAppt end time falls anywhere in the new appt
                if (conflictEnd.isBefore(endDateTime) & conflictEnd.isAfter(startDateTime)) {
                    return false;
                }
                else {
                    return true;
                }

            }
        }
        return true;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ZoneId zone = ZoneId.systemDefault(); // Initialize with system default time zone

        Localelabel.setText("Local Time Zone:" + zone);

        //Lambda Expression Disable past dates

        ApptCalender.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate apptDatePicker, boolean empty) {
                super.updateItem(apptDatePicker, empty);
                setDisable(
                        empty ||
                                apptDatePicker.isBefore(LocalDate.now()));
            }
        });


        try {
            CustomerSelect.setItems(CustomerQry.AllCustomerIds());
            UserSelect.setItems(UserQry.UserIDs());
            ContactSelect.setItems(ContactQry.AllContacts());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
