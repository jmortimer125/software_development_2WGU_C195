package c195.sw2;

import SqlDB.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
 * Controller class for editing appointments in the application.
 */
public class EditAppointments implements Initializable {



    @FXML
    public Button Save;
    @FXML
    public Button Clear;
    @FXML
    public Button back;
    @FXML
    public TextField ApptIDField;
    @FXML
    public Label Localelabel;
    @FXML
    public TextField ApptTitle;
    @FXML
    public TextArea ApptDescription;
    @FXML
    public TextField ApptLocation;
    @FXML
    public ComboBox ContactSelect;
    @FXML
    public TextField ApptType;
    @FXML
    public ComboBox CustomerSelect;
    @FXML
    public ComboBox UserSelect;
    @FXML
    public DatePicker ApptCalender;
    @FXML
    public TextField ApptStart;
    @FXML
    public TextField ApptEnd;
    public Label OpenHours;


    public void ViewChange(ActionEvent event, String view) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(view));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Initializes the fields of the controller with data from the selected appointment.
     *
     * @param selectedAppt The AppointmentStr object containing the appointment data.
     * @throws SQLException If a database access error occurs.
     */
    public void PopData(AppointmentStr selectedAppt) throws SQLException {



        try {
            LocalDate apptDate = selectedAppt.getApptStart().toLocalDateTime().toLocalDate();
        }
        catch (NullPointerException error) {
            ButtonType date = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert nodate = new Alert(Alert.AlertType.WARNING, "Date Not Selected", date);
            nodate.showAndWait();
            return;
        }
        ZonedDateTime UTCBegin = selectedAppt.getApptStart().toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime UTCEnd = selectedAppt.getApptEnd().toInstant().atZone(ZoneOffset.UTC);

        ZonedDateTime UserBegin = UTCBegin.withZoneSameInstant(LoginValidator.getCurrentTimeZone());
        ZonedDateTime UserEnd = UTCEnd.withZoneSameInstant(LoginValidator.getCurrentTimeZone());

        DateTimeFormatter dates = DateTimeFormatter.ofPattern("HH:mm");
        String Userstart = UserBegin.format(dates);
        String Finish = UserEnd.format(dates);


        ApptIDField.setText(selectedAppt.getAppointmentIDNum().toString());
        ApptTitle.setText(selectedAppt.getAppTitle());
        ApptDescription.setText(selectedAppt.getApptDescription());
        ApptLocation.setText(selectedAppt.getApptLocation());
        ContactSelect.setItems(ContactQry.AllContacts());
        ContactSelect.getSelectionModel().select(selectedAppt.getApptContactName());
        ApptType.setText(selectedAppt.getApptType());
        CustomerSelect.setItems(CustomerQry.AllCustomerIds());
        CustomerSelect.getSelectionModel().select(selectedAppt.getCustomerIDNum());
        UserSelect.setItems(UserQry.UserIDs());
        UserSelect.getSelectionModel().select(selectedAppt.getApptUserID());
        ApptCalender.setValue(selectedAppt.getApptStart().toLocalDateTime().toLocalDate());
        ApptStart.setText(Userstart);
        ApptEnd.setText(Finish);



    }

    /**
     * Validates if the provided appointment start and end times fall within business hours.
     *
     * @param ApptStartTime The appointment start date and time.
     * @param ApptEndTime   The appointment end date and time.
     * @param apptDate      The appointment date.
     * @return True if the appointment is within business hours, false otherwise.
     */
    public Boolean CheckOpen(LocalDateTime ApptStartTime, LocalDateTime ApptEndTime, LocalDate apptDate) {
        // (8am to 10pm EST)

        ZonedDateTime UserStartTime = ZonedDateTime.of(ApptStartTime, LoginValidator.getCurrentTimeZone());
        ZonedDateTime UserEndTime = ZonedDateTime.of(ApptEndTime, LoginValidator.getCurrentTimeZone());

        ZonedDateTime Open = ZonedDateTime.of(apptDate, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime Close = ZonedDateTime.of(apptDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));

        if (UserStartTime.isBefore(Open) | UserStartTime.isAfter(Close) |
                UserEndTime.isBefore(Open) | UserEndTime.isAfter(Close) |
                UserStartTime.isAfter(UserEndTime)) {
            return false;

        }
        else {
            return true;
        }

    }

    /**
     * Validates if the selected customer has any conflicting appointments at the specified date and time.
     *
     * @param CustIDNum The ID of the selected customer.
     * @param ApptStart   The appointment start date and time.
     * @param ApptEnd     The appointment end date and time.
     * @param apptDate        The appointment date.
     * @return True if there are no conflicts, false if there are conflicting appointments.
     * @throws SQLException If a database access error occurs.
     */
    public Boolean CheckApptConlficts(Integer CustIDNum, LocalDateTime ApptStart,
                                      LocalDateTime ApptEnd, LocalDate apptDate) throws SQLException {

        ObservableList<AppointmentStr> OverlappedAppts = AppointmentQry.AppointmentsByCustomer(apptDate,
                CustIDNum);

        if (OverlappedAppts.isEmpty()) {
            return true;
        }
        else {
            for (AppointmentStr conflictAppt : OverlappedAppts) {

                LocalDateTime OverlapStart = conflictAppt.getApptStart().toLocalDateTime();
                LocalDateTime OverlapEnd = conflictAppt.getApptEnd().toLocalDateTime();


                if( OverlapStart.isBefore(ApptStart) & OverlapEnd.isAfter(ApptEnd)) {
                    return false;
                }
                if (OverlapStart.isBefore(ApptEnd) & OverlapStart.isAfter(ApptStart)) {
                    return false;
                }
                if (OverlapEnd.isBefore(ApptEnd) & OverlapEnd.isAfter(ApptStart)) {
                    return false;
                }
                else {
                    return true;
                }

            }
        }
        return true;

    }


    /**
     * Handles the action of pressing the "Save" button to update the appointment.
     *
     * @param actionEvent The ActionEvent that triggered the method.
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If an I/O error occurs during navigation.
     */
    public void Save(ActionEvent actionEvent) throws SQLException, IOException {

        Boolean CorrectStart = true;
        Boolean CorrectEnd = true;
        Boolean NoConflicts = true;
        Boolean Open = true;
        String CouldntSave = "";

        Integer apptID = Integer.parseInt(ApptIDField.getText());
        String NewApptTitle = ApptTitle.getText();
        String NewApptDescription = ApptDescription.getText();
        String NewLocation = ApptLocation.getText();
        String NewContactName = (String) ContactSelect.getValue();
        String NewApptType = ApptType.getText();
        Integer NewCustID = (Integer) CustomerSelect.getValue();
        Integer NewUserID = (Integer) UserSelect.getValue();
        LocalDate apptDate = ApptCalender.getValue();
        LocalDateTime NewApptEnd = null;
        LocalDateTime NewApptStart = null;
        ZonedDateTime NewZoneEnd = null;
        ZonedDateTime NewZoneStart = null;

        Integer contactID = ContactQry.retrieveContactID(NewContactName);


        DateTimeFormatter date = DateTimeFormatter.ofPattern("HH:mm");


        try {
            NewApptStart = LocalDateTime.of(ApptCalender.getValue(),
                    LocalTime.parse(ApptStart.getText(), date));
            CorrectStart = true;
        }
        catch(DateTimeParseException formaterror) {
            CorrectStart = false;
            CouldntSave += "Start Time Format is Incorrect. Use HH:MM.\n";
        }

        try {
            NewApptEnd = LocalDateTime.of(ApptCalender.getValue(),
                    LocalTime.parse(ApptEnd.getText(), date));
            CorrectEnd = true;
        }
        catch(DateTimeParseException endformat) {
            CorrectEnd = false;
            CouldntSave += "End Time Format is Incorrect. Use HH:MM.\n";
        }


        if (NewApptTitle.isBlank() || NewApptDescription.isBlank() || NewLocation.isBlank() || NewContactName == null || NewApptType.isBlank() ||
                NewCustID == null || NewUserID == null || apptDate == null || NewApptEnd == null ||
                NewApptStart == null) {

            CouldntSave += "Please check all fields for missing valid information.\n";

            ButtonType valid = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert Valid = new Alert(Alert.AlertType.WARNING, CouldntSave, valid);
            Valid.showAndWait();
            return;

        }


        Open = CheckOpen(NewApptStart, NewApptEnd, apptDate);
        NoConflicts = CheckApptConlficts(NewCustID, NewApptStart, NewApptEnd, apptDate);


        if (!Open) {
            CouldntSave += "Please enter hours between 8am and 10pm EST\n";
        }
        if (!NoConflicts) {
            CouldntSave += "Customer already is scheduled for an appointment during this time.\n";
        }



        if (!NoConflicts || !Open || !CorrectEnd || !CorrectStart) {
            ButtonType valid = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert Valid = new Alert(Alert.AlertType.WARNING, CouldntSave, valid);
            Valid.showAndWait();
            return;

        }
        else {
            NewZoneStart = ZonedDateTime.of(NewApptStart, LoginValidator.getCurrentTimeZone());
            NewZoneEnd = ZonedDateTime.of(NewApptEnd, LoginValidator.getCurrentTimeZone());
            String loggedOnUserName = LoginValidator.getCurrentUser().getUserName();


            NewZoneStart = NewZoneStart.withZoneSameInstant(ZoneOffset.UTC);
            NewZoneEnd = NewZoneEnd.withZoneSameInstant(ZoneOffset.UTC);

            Boolean success = AppointmentQry.updateAppointment(apptID, NewApptTitle, NewApptDescription, NewLocation, NewApptType, NewZoneStart,
                    NewZoneEnd, loggedOnUserName, NewCustID, NewUserID, contactID );


            if (success) {
                ButtonType valid = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                Alert Valid = new Alert(Alert.AlertType.CONFIRMATION, "Appointment successfully updated!", valid);
                Valid.showAndWait();
                ViewChange(actionEvent, "AppointmentView.fxml");
            }
            else {
                ButtonType notvalid = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                Alert NotValid = new Alert(Alert.AlertType.WARNING, "Appointment failed to update", notvalid);
                NotValid.showAndWait();
            }

        }

    }

    public void Clear(ActionEvent actionEvent) {
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
        ViewChange(actionEvent, "AppointmentView.fxml");

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Localelabel.setText(LoginValidator.getCurrentTimeZone().toString());

    }

}
