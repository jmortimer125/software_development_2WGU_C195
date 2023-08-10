package c195.sw2;

import SqlDB.CustomerQry;
import SqlDB.CustomerStr;
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
import java.util.ResourceBundle;

/**
 * Controller class for editing customer information in the application.
 */
public class EditCustomers implements Initializable {
    @FXML
    public TextField customerIDNum;
    @FXML
    public ComboBox customerCountry;
    @FXML
    public ComboBox customerCity;
    @FXML
    public TextField customerName;
    @FXML
    public TextField customerAddress;
    @FXML
    public TextField customerZip;
    @FXML
    public TextField customerPhoneNum;
    @FXML
    public Button save;
    @FXML
    public Button clear;
    @FXML
    public Button back;

    /**
     * Initializes the fields of the controller with data from the selected customer.
     *
     * @param selectedCustomer The CustomerStr object containing the customer data.
     * @throws SQLException If a database access error occurs.
     */
    public void popData(CustomerStr selectedCustomer) throws SQLException {

        customerCountry.setItems(CustomerQry.CustomerCountries());
        customerCountry.getSelectionModel().select(selectedCustomer.getCountry());
        customerCity.setItems(CustomerQry.DivisionsByCountry(selectedCustomer.getCountry()));
        customerCity.getSelectionModel().select(selectedCustomer.getDivision());

        customerIDNum.setText(selectedCustomer.getCustomerID().toString());
        customerName.setText(selectedCustomer.getName());
        customerAddress.setText(selectedCustomer.getAddress());
        customerZip.setText(selectedCustomer.getPostalCode());
        customerPhoneNum.setText(selectedCustomer.getPhoneNumber());


    }

    public void switchScreen(ActionEvent event, String switchPath) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(switchPath));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }


    /**
     * Handles the action of pressing the "Save" button to update customer information.
     *
     * @param actionEvent The ActionEvent that triggered the method.
     * @throws IOException  If an I/O error occurs during navigation.
     * @throws SQLException If a database access error occurs.
     */
    public void pressSave(ActionEvent actionEvent) throws IOException, SQLException {
        // INPUT VALIDATION - check for nulls
        String country = (String) customerCountry.getValue();
        String division = (String) customerCity.getValue();
        String name = customerName.getText();
        String address = customerAddress.getText();
        String postalCode = customerZip.getText();
        String phone = customerPhoneNum.getText();
        Integer customerID = Integer.parseInt(customerIDNum.getText());

        if (country.isBlank() || division.isBlank() || name.isBlank() || address.isBlank() || postalCode.isBlank() ||
                phone.isBlank()) {

            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert emptyVal = new Alert(Alert.AlertType.WARNING, "Please ensure all fields are completed.",
                    clickOkay);
            emptyVal.showAndWait();
            return;

        }

        // CustomerDB updateCustomer
        Boolean success = CustomerQry.CustomerUpdate(division, name, address, postalCode, phone, customerID);

        if (success) {
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer updated successfully!", clickOkay);
            alert.showAndWait();
            switchScreen(actionEvent, "CustomerView.fxml");
        }
        else {
            ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
            Alert invalidInput = new Alert(Alert.AlertType.WARNING, "failed to Update Customer", clickOkay);
            invalidInput.showAndWait();
        }
    }

    public void pressClear(ActionEvent actionEvent) {
        customerCountry.getSelectionModel().clearSelection();
        customerCity.getSelectionModel().clearSelection();
        customerName.clear();
        customerAddress.clear();
        customerZip.clear();
        customerPhoneNum.clear();
    }

    public void pressBack(ActionEvent actionEvent) throws IOException {
        switchScreen(actionEvent, "CustomerView.fxml");

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Listener for combo box change
        customerCountry.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                customerCity.getItems().clear();
                customerCity.setDisable(true);

            }
            else {
                customerCity.setDisable(false);
                try {
                    customerCity.setItems(CustomerQry.DivisionsByCountry((String) customerCountry.getValue()));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        });

    }
}

