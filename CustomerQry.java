package SqlDB;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
;

import java.sql.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The CustomerQry class provides methods to query and manipulate customer data in the database.
 */
public class CustomerQry {
    /**
     * Updates an existing customer's information in the database.
     *
     * @param CustomerCity   The CustomerCity to which the customer belongs.
     * @param CustomerName       The CustomerName of the customer.
     * @param CustomerAddress    The CustomerAddress of the customer.
     * @param CustomerZip The postal code of the customer.
     * @param CustomerContactNum   The phone number of the customer.
     * @param customerIDNum The ID of the customer to be updated.
     * @return True if the customer information is successfully updated, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    public static Boolean CustomerUpdate(String CustomerCity, String CustomerName, String CustomerAddress,
                                         String CustomerZip, String CustomerContactNum, Integer customerIDNum) throws SQLException {

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement customerupdate = JDBC.connection.prepareStatement(
                "UPDATE customers " +
                        "SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Update=?, Last_Updated_By=?, Division_ID=? " +
                        "WHERE Customer_ID = ?"
        );


        customerupdate.setString(1, CustomerName);
        customerupdate.setString(2, CustomerAddress);
        customerupdate.setString(3, CustomerZip);
        customerupdate.setString(4, CustomerContactNum);
        customerupdate.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(date));
        customerupdate.setString(6, LoginValidator.getCurrentUser().getUserName());
        customerupdate.setInt(7, CustomerQry.CitysandDivisions(CustomerCity));
        customerupdate.setInt(8, customerIDNum);


        try {
            customerupdate.executeUpdate();
            customerupdate.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            customerupdate.close();
            return false;
        }

    }

    /**
     * Deletes a customer from the database based on the customer ID.
     *
     * @param customerIDNum The ID of the customer to be deleted.
     * @return True if the customer is successfully deleted, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    public static Boolean CustomerDelete(Integer customerIDNum) throws SQLException {
        PreparedStatement customerdelete = JDBC.connection.prepareStatement(
                "DELETE FROM customers " +
                "WHERE Customer_ID = ?");

        customerdelete.setInt(1, customerIDNum);

        try {
            customerdelete.executeUpdate();
            customerdelete.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a new customer to the database.
     *
     * @param CustomerCountry    The CustomerCountry of the customer.
     * @param CustomerCity   The CustomerCity to which the customer belongs.
     * @param CustomerName       The CustomerName of the customer.
     * @param CustomerAddress    The CustomerAddress of the customer.
     * @param CustomerZip The postal code of the customer.
     * @param CustomerPhoneNum   The phone number of the customer.
     * @param CustomerDivision The ID of the CustomerCity to which the customer belongs.
     * @return True if the customer is successfully added, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    public static Boolean addNewCustomer(String CustomerCountry, String CustomerCity, String CustomerName, String CustomerAddress, String CustomerZip,
                                         String CustomerPhoneNum, Integer CustomerDivision) throws SQLException {

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PreparedStatement newcustomer = JDBC.connection.prepareStatement(
                "INSERT INTO customers " +
                        "    (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );


        newcustomer.setString(1, CustomerName);
        newcustomer.setString(2, CustomerAddress);
        newcustomer.setString(3, CustomerZip);
        newcustomer.setString(4, CustomerPhoneNum);
        newcustomer.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(date).toString());
        newcustomer.setString(6, LoginValidator.getCurrentUser().getUserName());
        newcustomer.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(date).toString());
        newcustomer.setString(8, LoginValidator.getCurrentUser().getUserName());
        newcustomer.setInt(9, CustomerDivision);

        try {
            newcustomer.executeUpdate();
            newcustomer.close();
            return true;
        }
        catch (SQLException e) {

            e.printStackTrace();
            newcustomer.close();
            return false;
        }

    }

    /**
     * Retrieves the Divisions ID for a specific Divisions name.
     *
     * @param Divisions The name of the Divisions for which to retrieve the Divisions ID.
     * @return The Divisions ID corresponding to the given Divisions name.
     * @throws SQLException If a database access error occurs.
     */
    public static Integer CitysandDivisions(String Divisions) throws SQLException {
        Integer divisionID = 0;
        PreparedStatement cities = JDBC.connection.prepareStatement(
                "SELECT Division, Division_ID " +
                        "FROM first_level_divisions " +
                        "WHERE Division = ?;"
        );


        cities.setString(1, Divisions);

        ResultSet divisions = cities.executeQuery();

        while ( divisions.next() ) {
            divisionID = divisions.getInt("Division_ID");
        }

        cities.close();
        return divisionID;

    }

    /**
     * Retrieves a list of all customers based on IDs from the database.
     *
     * @return An ObservableList of all customer IDs.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<Integer> AllCustomerIds() throws SQLException {

        ObservableList<Integer> allIDs = FXCollections.observableArrayList();
        PreparedStatement customerIDS = JDBC.connection.prepareStatement(
                "SELECT DISTINCT Customer_ID " +
                        "FROM customers;"
        );

        ResultSet customers = customerIDS.executeQuery();

        while ( customers.next() ) {
            allIDs.add(customers.getInt("Customer_ID"));
        }
        customerIDS.close();
        return allIDs;
    }

    /**
     * Retrieves a list of divisions filtered by the specified country.
     *
     * @param CustomerCountry The name of the country for which to filter the divisions.
     * @return An ObservableList of division names filtered by the specified country.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<String> DivisionsByCountry(String CustomerCountry) throws SQLException {

        ObservableList<String> divisions = FXCollections.observableArrayList();
        PreparedStatement divisionsbycountry = JDBC.connection.prepareStatement(
                "SELECT c.Country, c.Country_ID, d.Division_ID, d.Division " +
                        "FROM countries AS c " +
                        "RIGHT OUTER JOIN first_level_divisions AS d ON c.Country_ID = d.Country_ID " +
                        "WHERE c.Country = ?;"
        );


        divisionsbycountry.setString(1, CustomerCountry);
        ResultSet cities = divisionsbycountry.executeQuery();

        while (cities.next()) {
            divisions.add(cities.getString("Division"));
        }

        divisionsbycountry.close();
        return divisions;

    }

    /**
     * Retrieves a list of all countries from the database.
     *
     * @return An ObservableList of all countries.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<String> CustomerCountries() throws SQLException {

        ObservableList<String> customerCountries = FXCollections.observableArrayList();
        PreparedStatement countries = JDBC.connection.prepareStatement(
                "SELECT Country " +
                        "FROM countries " +
                        "GROUP BY Country"
        );

        ResultSet country = countries.executeQuery();

        while (country.next()) {
            customerCountries.add(country.getString("Country"));
        }
        countries.close();
        return customerCountries;

    }

    /**
     * Retrieves a list of all customers with their details from the database.
     *
     * @return An ObservableList of CustomerStr objects representing all customers.
     * @throws SQLException If a database access error occurs.
     */
    public static ObservableList<CustomerStr> allCustomers() throws SQLException {

        ObservableList<CustomerStr> customers = FXCollections.observableArrayList();
        PreparedStatement allcustomers = JDBC.connection.prepareStatement(
                "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, " +
                        "       d.Division_ID, d.Division, d.Country_ID, co.Country " +
                        "FROM customers AS c " +
                        "INNER JOIN first_level_divisions AS d ON c.Division_ID = d.Division_ID " +
                        "INNER JOIN countries AS co ON d.Country_ID = co.Country_ID"
        );

        ResultSet all = allcustomers.executeQuery();


        while( all.next() ) {

            Integer CustomerIDNum = all.getInt("Customer_ID");
            String CustomerName = all.getString("Customer_Name");
            String CustomerAddress = all.getString("Address");
            String CustomerZip = all.getString("Postal_Code");
            String CustomerPhoneNum = all.getString("Phone");
            String CustomerCity = all.getString("Division");
            Integer CustomerDivision = all.getInt("Division_ID");
            String CustomerCountry = all.getString("Country");


            CustomerStr NewCustomer = new CustomerStr(CustomerIDNum, CustomerName, CustomerAddress, CustomerZip, CustomerPhoneNum, CustomerCity,
                    CustomerDivision, CustomerCountry);


            customers.add(NewCustomer);

        }
        allcustomers.close();
        return customers;

    }
}
