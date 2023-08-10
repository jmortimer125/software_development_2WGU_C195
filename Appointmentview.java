package c195.sw2;

import SqlDB.AppointmentQry;
import SqlDB.AppointmentStr;
import SqlDB.LoginValidator;
import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the Appointment View.
 */
public class Appointmentview implements Initializable {
    public Label masterSchedule;
    public Label TimeFrame;
    @FXML
    Button New;
    @FXML
    Button edit;
    @FXML
    Button delete;
    @FXML
    Button customers;
    @FXML
    Button reports;
    @FXML
    Button logOut;
    @FXML
    Button next;
    @FXML
    Button back;
    @FXML
    RadioButton monthly;
    @FXML
    RadioButton weekly;
    @FXML
    RadioButton all;
    @FXML
    TableView<AppointmentStr> Appointments;
    @FXML
    TableColumn<AppointmentStr, Integer> ApptID;
    @FXML
    TableColumn<AppointmentStr, String> ApptTitle;
    @FXML
    TableColumn<AppointmentStr, String> ApptDescription;
    @FXML
    TableColumn<AppointmentStr, String> ApptLocation;
    @FXML
    TableColumn<AppointmentStr, String> CustomerName;
    @FXML
    TableColumn<AppointmentStr, String> ApptType;
    @FXML
    TableColumn<AppointmentStr, ZonedDateTime> BeginTime;
    @FXML
    TableColumn<AppointmentStr, ZonedDateTime> EndTime;
    @FXML
    TableColumn<AppointmentStr, Integer> CustID;
    @FXML
    ToggleGroup Filters;
    @FXML
    Label selectedTimeLabel;


    ZonedDateTime begin;
    ZonedDateTime end;

    public void changeView(ActionEvent event, String newScreen) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(newScreen));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }


    /**
     * Initializes the ToggleGroup for the filter buttons and sets the default toggle.
     * Also, prints the user's time zone to the console.
     */
    public void Tg() {

        Filters = new ToggleGroup();

        all.setToggleGroup(Filters);
        weekly.setToggleGroup(Filters);
        monthly.setToggleGroup(Filters);
        System.out.println(LoginValidator.getCurrentTimeZone());

    }

    /**
     * Handles the action when the "Month Filter" button is pressed.
     * Filters appointments by the current month and updates the table and label accordingly.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws SQLException If a database access error occurs.
     */
    public void Monthly(ActionEvent actionEvent)throws SQLException {
        weekly.setSelected(false);
        all.setSelected(false);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZoneId zone = ZoneId.systemDefault();

        ObservableList<AppointmentStr> apptRanges = FXCollections.observableArrayList();
        begin = ZonedDateTime.now(zone);
        end = begin.plusMonths(1);


        ZonedDateTime Start = begin.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime End = end.withZoneSameInstant(ZoneOffset.UTC);


        apptRanges = AppointmentQry.ApptsByDate(Start, End);

        if (apptRanges.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Appointments");
            alert.setHeaderText(null);
            alert.setContentText("There are no appointments scheduled for this Month.");
            alert.showAndWait();
        } else {
            ShowAppts(apptRanges);
        }

        selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                LoginValidator.getCurrentTimeZone());



    }

    /**
     * Handles the action when the "Week Filter" button is pressed.
     * Filters appointments by the current week and updates the table and label accordingly.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws SQLException If a database access error occurs.
     */
    public void Weekly(ActionEvent actionEvent) throws SQLException {

        monthly.setSelected(false);
        all.setSelected(false);
        ZoneId zone = ZoneId.systemDefault();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ObservableList<AppointmentStr> ApptRanges = FXCollections.observableArrayList();
        begin = ZonedDateTime.now(zone);
        end = begin.plusWeeks(1);


        ZonedDateTime Start = begin.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime End = end.withZoneSameInstant(ZoneOffset.UTC);


        ApptRanges = AppointmentQry.ApptsByDate(Start, End);

        if (ApptRanges.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Appointments");
            alert.setHeaderText(null);
            alert.setContentText("There are no appointments scheduled for this week.");
            alert.showAndWait();
        } else {
            ShowAppts(ApptRanges);
        }

        selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                LoginValidator.getCurrentTimeZone());

    }


    /**
     * Handles the action when the "No Filter" button is pressed.
     * Shows all appointments without any filtering and updates the table and label accordingly.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     */
    public void AllAppts(ActionEvent actionEvent) {

            monthly.setSelected(false);
            weekly.setSelected(false);

            ObservableList<AppointmentStr> allAppts;
            try {
                allAppts = AppointmentQry.ViewAllAppts();
            }
            catch (SQLException error){

                error.printStackTrace();
                JDBC.openConnection();
                try {
                    allAppts = AppointmentQry.ViewAllAppts();
                } catch (SQLException anotherError) {
                    anotherError.printStackTrace();
                    ButtonType connect = new ButtonType("Try Again", ButtonBar.ButtonData.OK_DONE);
                    Alert didnotconnect = new Alert(Alert.AlertType.WARNING, "Failed to connect to database", connect);
                    didnotconnect.showAndWait();
                    return;
                }

            }
            ShowAppts(allAppts);
            selectedTimeLabel.setText("All Appointments");
            begin = null;


        }


    /**
     * Handles the action when the "Back" button is pressed.
     * Navigates to the previous time frame based on the selected filter.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws SQLException If a database access error occurs.
     */
    public void GoBack(ActionEvent actionEvent) throws SQLException {


        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ObservableList<AppointmentStr> Apptrange = FXCollections.observableArrayList();

        if (Filters.getSelectedToggle() == weekly) {

            ZonedDateTime Start = begin.minusWeeks(1);
            ZonedDateTime End = end.minusWeeks(1);


            begin = Start;
            end = End;


            Start = Start.withZoneSameInstant(ZoneOffset.UTC);
            End = End.withZoneSameInstant(ZoneOffset.UTC);

            Apptrange = AppointmentQry.ApptsByDate(Start, End);

            ShowAppts(Apptrange);


            selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                    LoginValidator.getCurrentTimeZone());

        }
        if (Filters.getSelectedToggle() == monthly) {

            ZonedDateTime Start = begin.minusMonths(1);
            ZonedDateTime End = end.minusMonths(1);


            begin = Start;
            end = End;


            Start = Start.withZoneSameInstant(ZoneOffset.UTC);
            End = End.withZoneSameInstant(ZoneOffset.UTC);

            Apptrange = AppointmentQry.ApptsByDate(Start, End);

            ShowAppts(Apptrange);


            selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                    LoginValidator.getCurrentTimeZone());
        }

    }

    /**
     * Handles the action when the "Next" button is pressed.
     * Navigates to the next time frame based on the selected filter.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws SQLException If a database access error occurs.
     */
    public void PressNext(ActionEvent actionEvent) throws SQLException {

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ObservableList<AppointmentStr> ApptRange = FXCollections.observableArrayList();

        if (Filters.getSelectedToggle() == weekly) {

            ZonedDateTime Start = begin.plusWeeks(1);
            ZonedDateTime End = end.plusWeeks(1);


            begin = Start;
            end = End;
            Start = Start.withZoneSameInstant(ZoneOffset.UTC);
            End = End.withZoneSameInstant(ZoneOffset.UTC);

            ApptRange = AppointmentQry.ApptsByDate(Start, End);

            ShowAppts(ApptRange);

            selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                    LoginValidator.getCurrentTimeZone());

        }
        if (Filters.getSelectedToggle() == monthly) {

            ZonedDateTime Start = begin.plusMonths(1);
            ZonedDateTime End = end.plusMonths(1);

            begin = Start;
            end = End;

            Start = Start.withZoneSameInstant(ZoneOffset.UTC);
            End = End.withZoneSameInstant(ZoneOffset.UTC);

            ApptRange = AppointmentQry.ApptsByDate(Start, End);

            ShowAppts(ApptRange);


            selectedTimeLabel.setText(begin.format(date) + " - " + end.format(date) + " " +
                    LoginValidator.getCurrentTimeZone());

        }

    }

    /**
     * Handles the action when the "New" button is pressed.
     * Changes the screen to the New Appointment view.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during switching the scene.
     */
    public void PressNew(ActionEvent actionEvent) throws IOException {
        changeView(actionEvent, "NewAppointment.fxml");

    }

    /**
     * Handles the action when the "Edit" button is pressed.
     * Changes the screen to the Edit Appointment view and passes the selected appointment data.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during switching the scene.
     * @throws SQLException If a database access error occurs.
     */
    public void PressEdit(ActionEvent actionEvent) throws IOException, SQLException {

        AppointmentStr selectedAppt = Appointments.getSelectionModel().getSelectedItem();

        if (selectedAppt == null) {
            ButtonType oops = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert noselection = new Alert(Alert.AlertType.WARNING, "Appointment not selected", oops);
            noselection.showAndWait();
            return;
        }

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("EditAppointment.fxml"));
                Parent parent = loader.load();
                Scene scene = new Scene(parent);
                EditAppointments controller = loader.getController();
                controller.PopData(selectedAppt);
                Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                window.setScene(scene);

            }

    /**
     * Handles the action when the "Customers" button is pressed.
     * Changes the screen to the Customer View.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during switching the scene.
     */
    public void PressCustomers(ActionEvent actionEvent) throws IOException {

        changeView(actionEvent, "CustomerView.fxml");

    }

    /**
     * Handles the action when the "Log Out" button is pressed.
     * Shows a confirmation dialog before logging out and changing the screen to the Login view.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during switching the scene.
     */
    public void PressLogOff(ActionEvent actionEvent) throws IOException {
        ButtonType Confirm = ButtonType.YES;
        ButtonType Deny = ButtonType.NO;
        Alert logOff = new Alert(Alert.AlertType.WARNING, "Do you want to end your session?", Confirm, Deny);
        Optional<ButtonType> choice = logOff.showAndWait();

        if (choice.get() == ButtonType.YES) {
            LoginValidator.LogOut();
            changeView(actionEvent, "LogIn.fxml");
        }
        else {
            return;
        }


    }

    /**
     * Handles the action when the "Delete" button is pressed.
     * Deletes the selected appointment and updates the table accordingly.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during the deletion process.
     * @throws SQLException If a database access error occurs.
     */
    public void PressDelete(ActionEvent actionEvent)throws IOException, SQLException {

        AppointmentStr selectedAppt = Appointments.getSelectionModel().getSelectedItem();

        if (selectedAppt == null) {
            ButtonType oops = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert tryagain = new Alert(Alert.AlertType.WARNING, "Appointment not selected", oops);
            tryagain.showAndWait();
            return;
        }
        else {
            ButtonType confirm = ButtonType.YES;
            ButtonType deny = ButtonType.NO;
            Alert confirmDelete = new Alert(Alert.AlertType.WARNING, "Press OK to Confirm You'd Like to Delete This Appointment: "
                    + selectedAppt.getAppointmentIDNum() + " ?", confirm, deny);
            Optional<ButtonType> choice = confirmDelete.showAndWait();


            if (choice.get() == ButtonType.YES) {
                Boolean approve = AppointmentQry.deleteAppt(selectedAppt.getAppointmentIDNum());


                if (approve) {
                    ButtonType success = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    Alert deletesuccess = new Alert(Alert.AlertType.CONFIRMATION, "Successfully deleted appointment.", success);
                    deletesuccess.showAndWait();

                }
                else {
                    ButtonType Failed = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    Alert Faileddelete = new Alert(Alert.AlertType.WARNING, "Appointment failed to be deleted.", Failed);
                    Faileddelete.showAndWait();

                }

                // Re-load appointments on screen
                try {
                    ShowAppts(AppointmentQry.ViewAllAppts());
                }
                catch (SQLException error){
                    error.printStackTrace();
                }

            }
            else {
                return;
            }
        }
    }

    /**
     * Handles the action when the "Reports" button is pressed.
     * Changes the screen to the Reports view.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during switching the scene.
     */
    public void PressReports(ActionEvent actionEvent) throws IOException {
        changeView(actionEvent, "Reports.fxml");

    }

    /**
     * Populates the appointment table with the provided list of appointments.
     *
     * @param ApptValues The ObservableList of AppointmentStr containing appointments to be displayed.
     */
    public void ShowAppts(ObservableList<AppointmentStr> ApptValues) {
        // Takes an observable list of appointments and populates them on screen.

        ApptID.setCellValueFactory(new PropertyValueFactory<AppointmentStr, Integer>("appointmentID"));
        ApptTitle.setCellValueFactory(new PropertyValueFactory<AppointmentStr, String>("title"));
        ApptDescription.setCellValueFactory(new PropertyValueFactory<AppointmentStr, String>("description"));
        ApptLocation.setCellValueFactory(new PropertyValueFactory<AppointmentStr, String>("location"));
        ApptType.setCellValueFactory(new PropertyValueFactory<AppointmentStr, String>("type"));
        CustomerName.setCellValueFactory(new PropertyValueFactory<AppointmentStr, String>("contactName"));
        BeginTime.setCellValueFactory(new PropertyValueFactory<AppointmentStr, ZonedDateTime>("startDateTime"));
        EndTime.setCellValueFactory(new PropertyValueFactory<AppointmentStr, ZonedDateTime>("endDateTime"));
        CustID.setCellValueFactory(new PropertyValueFactory<AppointmentStr, Integer>("customerID"));
        Appointments.setItems(ApptValues);

    }


    /**
     * Checks for canceled appointments in the given list and shows a warning for each canceled appointment.
     *
     * @param apptvalues The ObservableList of AppointmentStr to check for canceled appointments.
     */
    public void checkCanceled(ObservableList<AppointmentStr> apptvalues) {

        apptvalues.forEach((appt) -> {
            if (appt.getApptType().equalsIgnoreCase("canceled")) {
                ButtonType canceled = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                Alert apptcancled = new Alert(Alert.AlertType.WARNING, "The Appointment " + appt.getAppointmentIDNum() +
                        " has been canceled.", canceled);
                apptcancled.showAndWait();
            }
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        all.setSelected(true);
        Tg();


        ObservableList<AppointmentStr> allAppts = null;
        try {
            allAppts = AppointmentQry.ViewAllAppts();
        }
        catch (SQLException error){
            error.printStackTrace();
            JDBC.openConnection();
            try {
                allAppts = AppointmentQry.ViewAllAppts();
            } catch (SQLException anotherError) {
                anotherError.printStackTrace();
                ButtonType oops = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                Alert disconnect = new Alert(Alert.AlertType.WARNING, "Failed to connect to the database.", oops);
                disconnect.showAndWait();
                return;
            }

        }
        ShowAppts(allAppts);
        checkCanceled(allAppts);


    }

}
