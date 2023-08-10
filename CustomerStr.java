package SqlDB;

/**
 * The CustomerStr class represents a customer's data with various attributes such as ID, name, address, postal code,
 * phone number, division, division ID, and country.
 */
public class CustomerStr {

    private String address;
    private String division;
    private String postalCode;
    private Integer divisionID;
    private Integer ID;
    private String phoneNumber;
    private String name;
    private String country;


    /**
     * Constructs a new CustomerStr object with the provided customer information.
     *
     * @param CustID    The ID of the customer.
     * @param CustomerName          The name of the customer.
     * @param CustomerAddress       The address of the customer.
     * @param CustomerZip    The postal code of the customer.
     * @param CustomerPhoneNum   The phone number of the customer.
     * @param CustomerCity      The name of the division to which the customer belongs.
     * @param CustomerDivID         The ID of the division to which the customer belongs.
     * @param CustomerCountry       The country of the customer.
     */
    public CustomerStr(Integer CustID, String CustomerName, String CustomerAddress, String CustomerZip,
                    String CustomerPhoneNum, String CustomerCity, Integer CustomerDivID, String CustomerCountry) {
        ID = CustID;
        name = CustomerName;
        address = CustomerAddress;
        postalCode = CustomerZip;
        phoneNumber = CustomerPhoneNum;
        division = CustomerCity;
        divisionID = CustomerDivID;
        country = CustomerCountry;

    }



    public Integer getCustomerID() {
        return ID;
    }


    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }


    public String getPostalCode() {
        return postalCode;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getDivision() {
        return division;
    }


    public String getCountry() {
        return country;
    }


    public Integer getDivisionID() {
        return divisionID;
    }
}