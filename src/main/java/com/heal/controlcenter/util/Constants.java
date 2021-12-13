package com.heal.controlcenter.util;

public class Constants {

    public static final String TOTAL="total";

    // General
    public static final String DATE_TIME="yyyy-MM-dd HH:mm:ss";

    // ECDSA constants
    public static final String BC_PROVIDER_NAME = "BC";

    public static final String TIME_BRACKET_DEF = "15";


    /**
     * Notification constants
     */
    public static final String NOTIFICATION_TYPE_LITERAL = "NotificationType";
    public static final String LONG = "Open for long";
    public static final String TOO_LONG = "Open for too long";

    // SMS and SMTP
    public static final String SMS_ACTION_ADD = "add";
    public static final String SMS_ACTION_EDIT = "edit";
    public static final String SMS_ACTION_DELETE = "delete";
    public static final String SMS_PROTOCOLS = "SMSGatewayProtocols";
    public static final String SMS_HTTP_METHODS= "HTTPSMSRequestMethods";
    public static final String SMS_PARAMETER_TYPE_NAME= "SMSParameterTypes";
    public static final String SMS_PLACEHOLDERS= "SMSPlaceHolders";
    public static final String SMTP_PROTOCOLS = "SMTP Security";


    public static final String CONTROLLER = "controller";
    public static final String TIME_ZONE_TAG = "Timezone";
    public static final String SIGNAL_SEVERITY_TYPE_LITERAL = "SignalSeverity";
    public static final String SIGNAL_TYPE_LITERAL = "SignalType";
    public static final String SEVERE = "Severe";
    public static final String DEFAULT = "Default";
    public static final String EARLY_WARNING = "Early Warning";
    public static final String PROBLEM = "Problem";
    public static final String INFO = "Info";
    public static final String BATCH = "Batch Job";
    public static final String IMMEDIATELY = "Immediately";
    public static final String APPLICATION_PERCENTILES_DEFAULT_CONFIGURATION = "application.percentiles.default";
    public static final String APPLICATION_PERCENTILES_DEFAULT_VALUES = "50,75,90,95,99";

    //Service Default persistence and suppression
    public static final String SERVICE_START_WITHIN_AN_HOUR_PROPERTY_NAME = "service.startTime.lesserThan.hour";
    public static final String SERVICE_END_WITHIN_AN_HOUR_PROPERTY_NAME = "service.endTime.lesserThan.hour";
    public static final String SERVICE_START_TIME_WITHIN_AN_HOUR = "1";
    public static final String SERVICE_END_TIME_WITHIN_AN_HOUR = "59";
    public static final String SERVICE_SOR_PERSISTENCE_WITHIN_AN_HOUR_PROPERTY_NAME = "service.sor.persistence.lesserThan.hour";
    public static final String SERVICE_SOR_SUPPRESSION_WITHIN_AN_HOUR_PROPERTY_NAME = "service.sor.suppression.lesserThan.hour";
    public static final String SERVICE_SOR_PERSISTENCE_WITHIN_AN_HOUR = "5";
    public static final String SERVICE_SOR_SUPPRESSION_WITHIN_AN_HOUR = "10";

    public static final String SERVICE_NOR_PERSISTENCE_WITHIN_AN_HOUR_PROPERTY_NAME = "service.nor.persistence.lesserThan.hour";
    public static final String SERVICE_NOR_SUPPRESSION_WITHIN_AN_HOUR_PROPERTY_NAME = "service.nor.suppression.lesserThan.hour";
    public static final String SERVICE_NOR_PERSISTENCE_WITHIN_AN_HOUR = "2";
    public static final String SERVICE_NOR_SUPPRESSION_WITHIN_AN_HOUR = "5";

    public static final String SERVICE_START_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.startTime.greaterThan.hour";
    public static final String SERVICE_END_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.endTime.greaterThan.hour";
    public static final String SERVICE_START_TIME_MORE_THAN_AN_HOUR = "60";
    public static final String SERVICE_END_TIME_MORE_THAN_AN_HOUR = "1440";
    public static final String SERVICE_SOR_PERSISTENCE_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.sor.persistence.greaterThan.hour";
    public static final String SERVICE_SOR_SUPPRESSION_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.sor.suppression.greaterThan.hour";
    public static final String SERVICE_SOR_PERSISTENCE_MORE_THAN_AN_HOUR = "2";
    public static final String SERVICE_SOR_SUPPRESSION_MORE_THAN_AN_HOUR = "5";

    public static final String SERVICE_NOR_PERSISTENCE_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.nor.persistence.greaterThan.hour";
    public static final String SERVICE_NOR_SUPPRESSION_MORE_THAN_AN_HOUR_PROPERTY_NAME = "service.nor.suppression.greaterThan.hour";
    public static final String SERVICE_NOR_PERSISTENCE_MORE_THAN_AN_HOUR = "2";
    public static final String SERVICE_NOR_SUPPRESSION_MORE_THAN_AN_HOUR = "5";

    public static final String CONTROLLER_TYPE_NAME_DEFAULT = "ControllerType";
    public static final String SERVICES_CONTROLLER_TYPE = "Services";
    public static final String CONTROLLER_TAG = "Controller";
    public static final String COMP_INSTANCE_TABLE = "comp_instance";

    /*QueryParam */
    public static final String REQUEST_PARAM_FROM_TIME = "fromTime";
    public static final String REQUEST_PARAM_TO_TIME = "toTime";
    public static final String AUDIT_PARAM_SERVICE_NAME = "serviceId";
    public static final String AUDIT_PARAM_APPLICATION_NAME = "applicationId";
    public static final String AUDIT_PARAM_USER_NAME = "userId";
    public static final String AUDIT_PARAM_ACTIVITY_TYPE = "activityTypeId";

    //TimeZone
    public static final String DEFAULT_TIME_ZONE="+00:00";
    public static final String TIME_ZONE_FORMAT="%s%02d:%02d";
    public static final int DEFAULT_VALUE1 = 0;
    public static final int DEFAULT_VALUE2 = 3600000;
    public static final int DEFAULT_VALUE3 = 60000;
    public static final int DEFAULT_VALUE4 = 60;
    public static final String DB_CONDITION=") and ";




    //validation msg
    public static final String INVALID_TO_TIME = "invalid toTime provided";
    public static final String INVALID_FROM_TIME = "invalid fromTime provided";



}
