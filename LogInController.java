package c195.sw2;

import SqlDB.AppointmentStr;
import SqlDB.LoginValidator;
import helper.JDBC;
import helper.logHistory;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;



/**
 * Controller class for handling login functionality and displaying the login screen.
 * Lambda expression for the clearbutton makes a more concise code.
 */
public class LogInController implements Initializable {


    private final LoginValidator loginValidator;
    public Label Title;
    public Label zone;
    public TextField enterUserName;
    public TextField enterPassword;
    public Button login;
    public Button Clear;
    public Button Exit;
    public Label userName;
    public Label password;

    /**
     * Default constructor for the LogInController.
     * Creates a new instance of LoginValidator for login validation.
     * enterusername lambda expression changes focus of buttons and fields when enter is pressed
     */
    public LogInController() {
        this.loginValidator = new LoginValidator();
    }

    public void changeScreen(ActionEvent event, String newscreen) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(newscreen));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Appointmentview Appointmentview = loader.getController();
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.show();
    }


    /**
     * Handles the action of pressing the "Login" button to validate user credentials.
     *
     * @param event The ActionEvent that triggered the method.
     * @throws IOException  If an I/O error occurs during navigation.
     * @throws SQLException If a database access error occurs.
     */
        public void Login(ActionEvent event) throws IOException, SQLException {
            String userName = enterUserName.getText();
            String password = enterPassword.getText();
            boolean isValid = loginValidator.validateCredentials(userName, password);

            // append login history.txt file
            boolean logon = loginValidator.validateCredentials(userName, password);
            logHistory.auditLogin(userName, logon);

            if (isValid) {
                // alert to all appointments within 15 minutes.
                ObservableList<AppointmentStr> upcomingAppts = SqlDB.AppointmentQry.upcomingAppts();

                if (!upcomingAppts.isEmpty()) {
                    for (AppointmentStr upcoming : upcomingAppts) {

                        String message = "Upcoming appointmentID: " + upcoming.getAppointmentIDNum() + " Start: " +
                                upcoming.getApptStart().toString();
                        ButtonType clickOkay = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        Alert invalidInput = new Alert(Alert.AlertType.WARNING, message, clickOkay);
                        invalidInput.showAndWait();

                    }

                }
                // check for appointments in 15 minutes or less.
                else {
                    ButtonType clickOkay = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                    Alert invalidInput = new Alert(Alert.AlertType.CONFIRMATION, "No Appointments within 15 Minutes", clickOkay);
                    invalidInput.showAndWait();

                }
                // Credentials are valid, perform the necessary actions (e.g., open a new window, show a success message)
                System.out.println("Valid credentials");
                changeScreen(event, "AppointmentView.fxml");
            }
       else
       {
                    // Credentials are invalid, show an error message or perform appropriate actions
                    System.out.println("Invalid credentials");
                    Locale currentLocale = Locale.getDefault();
                    ResourceBundle errorMessages = ResourceBundle.getBundle("Nationale", currentLocale);
                    String errorMessage = errorMessages.getString("invalidCredentials");
                    showErrorAlert(errorMessage);
                }

            }


    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Locale userLocation = Locale.getDefault();
        zone.setText(Locale.getDefault().toString());
        ResourceBundle natlog = ResourceBundle.getBundle("Nationale");
        Title.setText(natlog.getString("titleLabel"));
        userName.setText(natlog.getString("userNameLabel"));
        password.setText(natlog.getString("passwordLabel"));
        login.setText(natlog.getString("loginButton"));
        Clear.setText(natlog.getString("clearButton"));
        Exit.setText(natlog.getString("exitButton"));

        // Using lambda expression for the "Clear" button action
        Clear.setOnAction(event -> {
            try {
                clearLogin(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        login.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    Login(new ActionEvent()); // Call the Login method when Enter is pressed on the loginButton
                } catch (IOException | SQLException e) {
                    e.printStackTrace(); // Handle exceptions appropriately
                }
                event.consume(); // Consume the event to prevent further processing
            }
        });
    }




    public void clearLogin(ActionEvent event)  throws IOException {
        enterUserName.clear();
        enterPassword.clear();

    }

    public void closeApp(ActionEvent event) throws IOException {
        //LogonSession.logOff();
        JDBC.closeConnection();
        System.exit(0);

    }


    public void enterUserName(ActionEvent actionEvent) {
        // This method will be invoked when Enter is pressed in the usernameField
        // We want to move focus to the passwordTextBox

        // Set a key pressed event handler for the usernameField
        enterUserName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Move focus to the passwordTextBox
                enterPassword.requestFocus();
                event.consume(); // Consume the event to prevent further processing
            }
        });
    }

    public void enterPassword(ActionEvent actionEvent) {
        enterPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Move focus to the loginButton
                login.requestFocus();
                event.consume(); // Consume the event to prevent further processing
            }
        });
    }


}
