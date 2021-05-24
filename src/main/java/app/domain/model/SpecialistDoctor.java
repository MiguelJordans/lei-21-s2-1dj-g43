package app.domain.model;

import app.domain.shared.Constants;
import auth.AuthFacade;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Class that represents an Specialist Doctor
 */
public class SpecialistDoctor extends Employee {

    private String DoctorIndexNumber;
    private Role role;
    private String name;
    private String address;
    private String phonenumber;
    private String email;
    private String SOC;
    private String EmployeeID;

    /**
     * Constructor of the Employee which is an subclass of Employee , it calls methods in order to validate the parameters
     *
     * @param EmployeeID        unique ID generated by the system for the Specialist Doctor
     * @param name              name of the Specialist Doctor
     * @param address           address of the Specialist Doctor
     * @param phonenumber       Phone number of the Specialist Doctor
     * @param email             email of the Employee
     * @param SOC               standard occupation code of the Specialist Doctor
     * @param DoctorIndexNumber Doctor Index Number of the Specialist Doctor
     * @param role              role of the Specialist Doctor
     */
    public SpecialistDoctor(String EmployeeID, String name, String address, String phonenumber, String email, String SOC, String DoctorIndexNumber, Role role) {
        super(EmployeeID, name, address, phonenumber, email, SOC, role);

        checkDoctorIndexNumberRules(DoctorIndexNumber);

        this.DoctorIndexNumber = DoctorIndexNumber;


    }

    /**
     * Checks if the string that is received meets the requirements of the Doctor Index Number, if not throws Exceptions
     *
     * @param DoctorIndexNumber Doctor Index Number of the client
     */
    private void checkDoctorIndexNumberRules(String DoctorIndexNumber) {
        if (StringUtils.isBlank(DoctorIndexNumber))
            throw new IllegalArgumentException("Doctor Index Number cannot be blank.");

        DoctorIndexNumber = DoctorIndexNumber.toLowerCase();
        char[] charArray = DoctorIndexNumber.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (!(c >= '0' && c <= '9')) {
                throw new IllegalArgumentException("Doctor Index Number only accepts numbers");
            }
        }
    }

    /**
     * Adds a new user to the system with the role of the Specialist Doctor using the getPassword method to create the user's password
     *
     * @param company instance of company class in order to be able to get the AuthFacade class that is associated with the system
     * @return a boolean value representing the success of the operation
     */

    public boolean addUserWithRole(Company company) {
        boolean success = false;
        String password = super.getPassword();
        AuthFacade authFacade = company.getAuthFacade();
        success = authFacade.addUserWithRole(this.name, this.email, super.getPassword(), Constants.ROLE_SPECIALISTDOCTOR);
        if (success) {
            Email mail = new Email(this.email, getPassword());

        }
        return success;
    }

    /**
     * @return A string with the format "Employee: ID=  employeeID, name= name, address=  address, phonenumber= phonenumber, email= email, SOC= SOC, Role= role, DoctorIndexNumber= DoctorIndexNumber"
     */
    @Override
    public String toString() {
        return super.toString() + ", DoctorIndexNumber=" + DoctorIndexNumber;
    }
}
