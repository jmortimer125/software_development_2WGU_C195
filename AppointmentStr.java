package SqlDB;

    import java.sql.Timestamp;

/**
 * The AppointmentStr class represents an appointment and its details.
 */
    public class AppointmentStr {

    private String appTitle;
    private Integer ContactIDNum;
    private Integer CustomerIDNum;
    private String ApptType;
    private String ApptCreatedBy;
    private String ApptDescription;
    private Integer ApptUserID;
    private Timestamp ApptStart;
    private Timestamp ApptEnd;
    private Timestamp UpdatedLast;
    private String ApptLocation;
    private Integer appointmentIDNum;
    private Timestamp CreatedDate;
    private String updatedLastBy;
    private String ApptContactName;


    public Integer getAppointmentIDNum() {
            return appointmentIDNum;
        }

        public void setAppointmentIDNum(Integer appointmentIDNum) {
            this.appointmentIDNum = appointmentIDNum;
        }

        public String getAppTitle() {
            return appTitle;
        }

        public void setAppTitle(String appTitle) {
            this.appTitle = appTitle;
        }

        public String getApptDescription() {
            return ApptDescription;
        }

        public void setApptDescription(String apptDescription) {
            this.ApptDescription = apptDescription;
        }

        public String getApptLocation() {
            return ApptLocation;
        }

        public void setApptLocation(String apptLocation) {
            this.ApptLocation = apptLocation;
        }

        public String getApptType() {
            return ApptType;
        }

        public void setApptType(String apptType) {
            this.ApptType = apptType;
        }

        public Timestamp getApptStart() {
            return ApptStart;
        }

        public void setApptStart(Timestamp apptStart) {
            this.ApptStart = apptStart;
        }

        public Timestamp getApptEnd() {
            return ApptEnd;
        }

        public void setApptEnd(Timestamp apptEnd) {
            this.ApptEnd = apptEnd;
        }

        public Timestamp getCreatedDate() {
            return CreatedDate;
        }

        public void setCreatedDate(Timestamp createdDate) {
            this.CreatedDate = createdDate;
        }

        public String getApptCreatedBy() {
            return ApptCreatedBy;
        }

        public void setApptCreatedBy(String apptCreatedBy) {
            this.ApptCreatedBy = apptCreatedBy;
        }

        public Timestamp getUpdatedLast() {
            return UpdatedLast;
        }

        public void setUpdatedLast(Timestamp updatedLast) {
            this.UpdatedLast = updatedLast;
        }

        public String getUpdatedLastBy() {
            return updatedLastBy;
        }

        public void setUpdatedLastBy(String updatedLastBy) {
            this.updatedLastBy = updatedLastBy;
        }

        public Integer getCustomerIDNum() {
            return CustomerIDNum;
        }

        public void setCustomerIDNum(Integer customerIDNum) {
            this.CustomerIDNum = customerIDNum;
        }

        public Integer getApptUserID() {
            return ApptUserID;
        }

        public void setApptUserID(Integer apptUserID) {
            this.ApptUserID = apptUserID;
        }

        public Integer getContactIDNum() {
            return ContactIDNum;
        }

        public void setContactIDNum(Integer contactIDNum) {
            this.ContactIDNum = contactIDNum;
        }

        public String getApptContactName() {
            return ApptContactName;
        }

        public void setApptContactName(String apptContactName) {
            this.ApptContactName = apptContactName;
        }

    /**
     * Constructs a new AppointmentStr object with the specified parameters.
     *
     * @param AppointmentIDNum    The ID of the appointment.
     * @param ApptName            The title of the appointment.
     * @param ApptDescription      The description of the appointment.
     * @param ApptLocation         The location of the appointment.
     * @param ApptType             The type of the appointment.
     * @param ApptStart    The start date and time of the appointment.
     * @param ApptEnd      The end date and time of the appointment.
     * @param CreatedOn      The creation date of the appointment.
     * @param CreatedBy         The creator of the appointment.
     * @param UpdatedLast  The last update date and time of the appointment.
     * @param UpdatedLastBy     The user who last updated the appointment.
     * @param CustomerIDNum       The ID of the customer associated with the appointment.
     * @param UserIDNum           The ID of the user associated with the appointment.
     * @param CustomerIDNum        The ID of the contact associated with the appointment.
     * @param Name      The name of the contact associated with the appointment.
     */
        public AppointmentStr(Integer AppointmentIDNum, String ApptName, String ApptDescription, String ApptLocation,
                           String ApptType, Timestamp ApptStart, Timestamp ApptEnd,
                           Timestamp CreatedOn, String CreatedBy, Timestamp UpdatedLast,
                           String UpdatedLastBy, Integer CustomerIDNum, Integer UserIDNum, Integer ContactIDNum,
                           String Name) {

            appointmentIDNum = AppointmentIDNum;
            appTitle = ApptName;
            this.ApptDescription = ApptDescription;
            this.ApptLocation = ApptLocation;
            this.ApptType = ApptType;
            this.ApptStart = ApptStart;
            this.ApptEnd = ApptEnd;
            CreatedDate = CreatedOn;
            ApptCreatedBy = CreatedBy;
            this.UpdatedLast = UpdatedLast;
            updatedLastBy = UpdatedLastBy;
            this.CustomerIDNum = CustomerIDNum;
            ApptUserID = UserIDNum;
            this.ContactIDNum = CustomerIDNum;
            ApptContactName = Name;

        }
}
