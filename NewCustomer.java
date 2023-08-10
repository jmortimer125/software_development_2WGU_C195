package c195.sw2;

import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import SqlDB.CustomerQry;
import java.io.IOException;
import javafx.fxml.Initializable;


/**
 * Controller class for the Add Customer view.
 */

public class NewCustomer implements Initializable {
    public ComboBox customerCountry;
    public TextField customerIDNum;
    public TextField customerAddress;
    public TextField customerZip;
    public ComboBox customerCity;
    public Button clear;
    public TextField customerName;
    public Button back;
    public Button save;
    public TextField customerPhoneNum;



    /**
     * Switches the scene to the one specified by the newView.
     *
     * @param event      The ActionEvent triggering the switch.
     * @param newView The path to the FXML file of the destination view.
     * @throws IOException If an I/O error occurs during loading the FXML file.
     */
    public void changeView(ActionEvent event, String newView) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(newView));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    /**
     * Handles the action when the "Save" button is pressed.
     * Validates the input fields, adds a new customer to the database,
     * and shows appropriate alerts for success or failure.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If an I/O error occurs during switching the scene.
     */
    public void saveCustomer(ActionEvent actionEvent) throws SQLException, IOException {

        String CustomerAddress = customerAddress.getText();
        String CustomerCity = (String) customerCity.getValue();
        String CustomerZip = customerZip.getText();
        String CustomerPhone = customerPhoneNum.getText();
        String CustomerName = customerName.getText();
        String CustomerCountry = (String) customerCountry.getValue();


        if (CustomerAddress.isEmpty() || CustomerCity.isEmpty()|| CustomerZip.isEmpty() || CustomerCountry.isEmpty() || CustomerName.isEmpty() ||
                CustomerPhone.isEmpty()) {

            ButtonType missingField = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert missingValue = new Alert(Alert.AlertType.WARNING, "Please complete all fields.",
                    missingField);
            missingValue.showAndWait();
            return;

        }


        Boolean addNewCustomer = CustomerQry.addNewCustomer(CustomerCountry, CustomerCity, CustomerName, CustomerAddress, CustomerZip, CustomerPhone,
                CustomerQry.CitysandDivisions(CustomerCity));

        // failure/addNewCustomer notification.
        if (addNewCustomer) {
            ButtonType added = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Dialog<Void> addedCustomer = new Dialog<>();
            addedCustomer.getDialogPane().setContentText("Customer successfully added!");
            addedCustomer.getDialogPane().getButtonTypes().addAll(added);

            addedCustomer.showAndWait();
            clear(actionEvent);
            changeView(actionEvent, "CustomerView.fxml");
        }
        else {
            ButtonType failed = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Dialog<Void> failedtoAdd = new Dialog<>();
            failedtoAdd.getDialogPane().setContentText("Failed to add Customer");
            failedtoAdd.getDialogPane().getButtonTypes().addAll(failed);

            failedtoAdd.showAndWait();
            return;
        }

    }


    public void back(ActionEvent actionEvent)  throws IOException {
        changeView(actionEvent, "CustomerView.fxml");

    }
    public void clear(ActionEvent actionEvent) {
        customerCity.getSelectionModel().clearSelection();
        customerPhoneNum.clear();
        customerAddress.clear();
        customerCountry.getSelectionModel().clearSelection();
        customerName.clear();
        customerZip.clear();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCustomerCountryComboBox();
        initializeCustomerCityComboBox();
    }

    private void initializeCustomerCountryComboBox() {
        try {
            customerCountry.setItems(CustomerQry.CustomerCountries());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initializeCustomerCityComboBox() {
        customerCountry.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                clearAndDisableCustomerCity();
            } else {
                enableCustomerCity();
                populateCustomerCityComboBox((String) newVal);
            }
        });
    }

    private void clearAndDisableCustomerCity() {
        customerCity.getItems().clear();
        customerCity.setDisable(true);
    }

    private void enableCustomerCity() {
        customerCity.setDisable(false);
    }

    private void populateCustomerCityComboBox(String selectedCountry) {
        try {
            customerCity.setItems(CustomerQry.DivisionsByCountry(selectedCountry));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

