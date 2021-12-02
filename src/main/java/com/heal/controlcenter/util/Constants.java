package com.heal.controlcenter.util;

public class Constants {

    public static final String TOTAL="total";

    // General
    public static final String DATE_TIME="yyyy-MM-dd HH:mm:ss";

    // ECDSA constants
    public static final String BC_PROVIDER_NAME = "BC";

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

}
