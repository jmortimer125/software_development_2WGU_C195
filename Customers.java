package c195.sw2;

import SqlDB.AppointmentQry;
import SqlDB.CustomerQry;
import SqlDB.CustomerStr;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the Customer View.

 * the lambda expressions are used to directly handle the button actions within the initialize method.
 * The @FXML annotation is used to indicate that the methods are associated with the FXML elements defined in the corresponding FXML file.
 * The lambda expressions allow us to write more concise and inline code for the button actions.
 */
public class Customers implements Initializable {

    @FXML
    private TableView<CustomerStr> customerTable;

    @FXML
    private TableColumn<CustomerStr, Integer> CustomerID;

    @FXML
    private TableColumn<CustomerStr, String> CustName;

    @FXML
    private TableColumn<CustomerStr, String> CustomerCountry;

    @FXML
    private TableColumn<CustomerStr, String> CustomerDivision;

    @FXML
    private TableColumn<CustomerStr, String> CustomerAddress;

    @FXML
    private TableColumn<CustomerStr, String> CustomerZip;

    @FXML
    private TableColumn<CustomerStr, String> CustomerPhone;

    @FXML
    private Button Add;

    @FXML
    private Button Edit;

    @FXML
    private Button Delete;

    @FXML
    private Button Back;

    public void View(ActionEvent event, String NewView) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(NewView));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void fillCustomers(ObservableList<CustomerStr> CustomerValues) {
        CustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        CustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        CustomerCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        CustomerDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        CustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        CustomerZip.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        CustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        customerTable.setItems(CustomerValues);
    }

    @FXML
    public void Add(ActionEvent actionEvent) throws IOException {
        View(actionEvent, "AddCustomer.fxml");
    }

    @FXML
    public void EditCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        CustomerStr selection = customerTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            ButtonType customer = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert noselection = new Alert(Alert.AlertType.WARNING, "No selection made", customer);
            noselection.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EditCustomer.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        EditCustomers controller = loader.getController();
        controller.popData(selection);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    /**
     * Handles the action when the "Delete" button is pressed.
     * Deletes the selected customer and all related appointments, and updates the table accordingly.
     *
     * @param actionEvent The ActionEvent triggering the method call.
     * @throws IOException If an I/O error occurs during the deletion process.
     * @throws SQLException If a database access error occurs.
     */
    public void DeleteCustomer(ActionEvent actionEvent)  throws IOException, SQLException {

        CustomerStr selection = customerTable.getSelectionModel().getSelectedItem();


        if (selection == null) {
            ButtonType deleteselection = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Alert delete = new Alert(Alert.AlertType.WARNING, "No customer has been selected", deleteselection);
            delete.showAndWait();
            return;
        }
        else {

            ButtonType confirm = ButtonType.YES;
            ButtonType deny = ButtonType.NO;
            Alert choicedelete = new Alert(Alert.AlertType.WARNING, "The following customer: "
                    + selection.getCustomerID() + " and all related appointments will be deleted.", confirm, deny);
            Optional<ButtonType> deletesuccess = choicedelete.showAndWait();


            if (deletesuccess.get() == ButtonType.YES) {
                Boolean customerApptSuccess = AppointmentQry.DeleteCustomerAssociatedAppts(selection.getCustomerID());

                Boolean customerSuccess = CustomerQry.CustomerDelete(selection.getCustomerID());


                if (customerSuccess && customerApptSuccess) {
                    ButtonType success = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    Alert deleted = new Alert(Alert.AlertType.CONFIRMATION,
                            "Customer information and related appointments were successfully deleted", success);
                    deleted.showAndWait();

                }
                else {

                    ButtonType oops = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                    Alert nodelete = new Alert(Alert.AlertType.WARNING,
                            "Customer and appointments failed to be deleted ", oops);
                    nodelete.showAndWait();

                }


                try {
                    fillCustomers(CustomerQry.allCustomers());
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

    @FXML
    public void Back(ActionEvent actionEvent) throws IOException {
        View(actionEvent, "AppointmentView.fxml");
    }

    @Override
    /**
     * the lambda expressions are used to directly handle the button actions within the initialize method.
     * The @FXML annotation is used to indicate that the methods are associated with the FXML elements defined in the corresponding FXML file.
     * The lambda expressions allow us to write more concise and inline code for the button actions.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillCustomers(CustomerQry.allCustomers());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Add lambda expressions to handle button actions
        Add.setOnAction(event -> {
            try {
                Add(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Edit.setOnAction(event -> {
            try {
                EditCustomer(event);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });

        Delete.setOnAction(event -> {
            try {
                DeleteCustomer(event);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });

        Back.setOnAction(event -> {
            try {
                Back(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
