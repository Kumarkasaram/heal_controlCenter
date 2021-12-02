package com.heal.controlcenter.util;

public class UIMessages {

    private UIMessages(){
        //Dummy Constructor
    }

    /**
     * Client validations related
     */
    public static final String AUTH_KEY_EMPTY = "Authorization token is empty.";
    public static final String AUTH_KEY_INVALID = "Invalid Authorization Token.";
    public static final String ACCOUNT_IDENTIFIER_EMPTY = "Account identifier should not be empty.";
    public static final String ACCOUNT_IDENTIFIER_INVALID = "Invalid account identifier provided.";

    public static final String REQUEST_BODY_NULL = "Request body is null or empty.";
    public static final String JSON_INVALID = "Invalid JSON.";

    /**
     * Notifications related
     */
    public static final String DUPLICATE_NOTIFICATION_SETTING = "Duplicate Notification Settings found.";
    public static final String INVALID_TYPE_NAME = "Invalid Type Name.";
    public static final String INVALID_DURATION = "Invalid Duration.";
    public static final String INVALID_COMBINATION = "Invalid Combination of Type Id and Type Name.";

    // SMS and SMTP
    public static final String INVALID_ADDRESS = "Address is either NULL, empty or its length is greater than 128 characters.";
    public static final String INVALID_PORT = "Port is invalid. It should be an integer greater than 0.";
    public static final String INVALID_COUNTRY_CODE = "Country code length is greater than 32 characters.";
    public static final String INVALID_SMS_PROTOCOL = "Protocol is either NULL or empty. It should be one of HTTP, HTTPS or TCP.";
    public static final String INVALID_HTTP_METHOD = "HTTP method is either NULL or empty. It should be either GET or POST.";
    public static final String INVALID_HTTP_URL = "HTTP URL is either NULL, empty or its length is greater than 512 characters.";
    public static final String INVALID_STATUS = "Multi request is invalid. Value should be either 0 or 1.";
    public static final String INVALID_SMS_PARAMETER_NAME = "Parameter name is either NULL, empty or its length is more than 128 characters.";
    public static final String INVALID_SMS_PARAMETER_VALUE = "Parameter value is either NULL, empty or its length is more than 128 characters.";
    public static final String INVALID_SMS_PARAM_PLACEHOLDER = "SMS PARAMETER placeholder is NULL or EMPTY.";
    public static final String INVALID_SMS_PARAM_ACTION = "SMS Parameter is invalid. It can be ‘add’, ‘edit’ or ‘delete’.";
    public static final String DUPLICATE_SMS_PARAMETER = "Duplicate SMS parameter.";
    public static final String SMTP_INVALID_ADDRESS = "Address is either NULL, empty or its length is greater than 256 characters.";
    public static final String INVALID_SMTP_SECURITY_TYPE = "SMTP security type is either NULL or empty. It should be one of TLS, SSL or NONE.";
    public static final String INVALID_SMTP_FROM_RECEIPT = "SMTP from recipient is either NULL, empty or its length is more than 256 characters.";
    public static final String INVALID_SMTP_USERNAME = "SMTP username length is more than 256 characters.";
    public static final String INVALID_SMTP_PWD = "SMTP password length is more than 256 characters.";

}
