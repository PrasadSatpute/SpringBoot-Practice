package gov.property.common;

public class Constants {

    public static final String REGISTRATION_NUMBER_PREFIX = "REG";
    public static final String TAX_RECEIPT_PREFIX = "TAX";

    // Tax Rates (percentage)
    public static final double STAMP_DUTY_RATE = 5.0;
    public static final double REGISTRATION_FEE_RATE = 1.0;
    public static final double TRANSFER_DUTY_RATE = 2.0;

    // Property Types
    public static final String RESIDENTIAL = "RESIDENTIAL";
    public static final String COMMERCIAL = "COMMERCIAL";
    public static final String AGRICULTURAL = "AGRICULTURAL";
    public static final String INDUSTRIAL = "INDUSTRIAL";

    // Registration Status
    public static final String PENDING = "PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String COMPLETED = "COMPLETED";

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OFFICER = "OFFICER";
    public static final String ROLE_CITIZEN = "CITIZEN";

    // Date Format
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}