package com.heal.controlcenter.Common;

public class UIMessages {

    private UIMessages(){
        //Dummy Constructor
    }

    public static final String AGENT_COMMAND_ADD_SUCCESS = "Agent command details added " +
            "successfully";
    public static final String AGENT_COMMAND_ADD_FAILURE = "Trouble adding agent command " +
            "details. Kindly check the logs";
    public static final String INVALID_VALUE = "Invalid parameter received. Parameter name: {0}, value: {1}.";
    public static final String AUTH_KEY_EMPTY = "Authorization Token is empty.";
    public static final String APPLICATION_ID_EMPTY = "Invalid path parameter 'applicationId is empty.";
    public static final String AUTH_KEY_INVALID = "Invalid Authorization Token.";
    public static final String JSON_INVALID = "Invalid JSON.";
    public static final String ACCOUNT_IDENTIFIER_INVALID = "Invalid Account Identifier.";
    public static final String ACCOUNT_EMPTY = "Account Identifier should not be empty.";
    public static final String ACCOUNT_NULL_OR_EMPTY = "Account identifier is null or empty.";
    public static final String SERVICE_ID_IS_NOT_NUMBER = "Service Id is not a number";
    public static final String APPLICATION_ID_IS_NOT_NUMBER = "Application Id is not a number";
    public static final String INSTANCE_EMPTY = "Instance Id should not be empty.";
    public static final String REQUEST_NULL = "Request object is null or empty.";
    public static final String CATEGORY_NAME_NULL = "Invalid Request : Category Name is null or not of specified length.";
    public static final String CATEGORY_IDENTIFIER_INVALID ="Invalid Request : Category identifier is not of specified length.";
    public static final String INVALID_REQUEST = "Invalid request. Kindly check the logs.";
    public static final String INVALID_REQUEST_EXCEPTION_MESSAGE = "Invalid request payload provided in the request.";
    public static final String CATEGORY_ADD_SUCCESS = "Category added successfully to the account.";
    public static final String CATEGORY_UPDATE_SUCCESS = "Category updated successfully for the account.";
    public static final String CATEGORY_GET_SUCCESS = "Categories fetched successfully for the account.";

    public static final String ACTION_ADD_SUCCESS ="New action added successfully.";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error, Kindly contact the Administrator.";
    public static final String ACTION_INVALID_NAME = "Invalid Name : Name cannot be null and it should have 1 - 128 characters only.";
    public static final String INVALID_IDENTIFIER = "Invalid Identifier : Identifier should contain 1 - 128 characters only.";
    public static final String INVALID_VALUES_FOR_ONE_OR_MORE_ATTRIBUTES = "Invalid request. Reason: Invalid values " +
            "for one or more attributes. Refer more details, refer application log file.";
    public static final String ACTION_INVALID_STANDARD_TYPE = "Invalid Standard Type : Standard Type should be either" +
            " 'Custom' or 'OOB'.";
    public static final String INVALID_CATEGORIES = "Invalid  Categories : All the categories should be valid " +
            "for this account";
    public static final String SUCCESS_CATEGORIES = "KPI categories fetched successfully.";
    public static final String SUCCESS_COMPONENT_DETAILS = "Component details fetched successfully.";
    public static final String SUCCESS_SDM_COMPONENTS = "Components fetched successfully.";
    public static final String ACTION_NO_CATEGORIES = "Categories not found : Atleast 1 valid category is required.";
    public static final String ACTION_INVALID_EXECUTION_TYPE = "Invalid Execution Type : Execution Type should be 'Script'.";
    public static final String INVALID_ACTION_TYPE = "Invalid Action Type";
    public static final String ACTION_INVALID_DOWNLOAD_TYPE = "Invalid Download Type : Download Type should be either" +
            " 'PDF' or 'CSV'.";
    public static final String ACTION_INVALID_COMMAND_EXECUTION_TYPE = "Invalid Command Execution Type : " +
            "Command Execution Type should be 'LondPolling'.";
    public static final String COMMAND_NAME_INVALID ="Invalid Name : Name cannot be null ant it should have 1 - 128 characters only. {}";
    public static final String ACTION_COMMAND_ARGS_DETAILS_INVALID="Command Argument's key, value and Default Value " +
            "cannot be null or empty.";
    public static final String COMMAND_INVALID = "Invalid Command : Command name and identifier should be Unique.";
    public static final String COMMAND_DETAILS_INVALID = "Invalid Command Details. {}";
    public static final String COMMAND_TYPE_INVALID = "Invalid Command Details : Command Type is invalid.";
    public static final String COMMAND_OUTPUT_TYPE_INVALID = "Invalid Command Details : Command Output Type is invalid.";
    public static final String COMMAND_DETAILS_NULL = "Invalid Request : 'commandDetails' cannot be null";
    public static final String ERROR_PARSING_AUDIT_VALUE = "Error Parsing Audit value: old or new value is null";
    public static final String ERROR_INVALID_APPLICATION_ID = "Invalid application Id provided: {0} ";
    public static final String ERROR_INVALID_SERVICE_ID = "Invalid service Id provided.";
    public static final String ERROR_INVALID_BIG_FEATURE_ID = "Invalid big feature Id provided: {0} ";
    public static final String ACTION_INVALID = "Invalid Action : Action Name and " +
            "Identifier should be Unique.";

    public static final String INSTANCE_HEALTH_DETAILS_SUCCESS = "Instance Health Details fetched successfully.";
    public static final String INSTANCE_HEALTH_SERVICE_TIMESTAMP="Error While Fetching Current TimeStamp in GMT";

    public static final String SDM_COMPONENT_DETAILS = "SDM Component Details fetched successfully";
    public static final String CUSTOM_COMPONENT_ADD_SUCCESS = "Component added " +
            "successfully.";
    public static final String CUSTOM_COMPONENT_ADD_FAILURE = "Trouble adding component. " +
            "Kindly check the logs.";
    public static final String ERROR_CUSTOM_COMPONENT_ALREADY_EXISTS_WITH_NAME = "Component " +
            "already" +
            " exists with " +
            "this name";


    public static final String ACTION_COMMAND_ARGS_DETAILS_TYPE_INVALID = "Command Argument Type Invalid.";
    public static final String ACTION_COMMAND_ARGS_DETAILS_VALUE_TYPE_INVALID = "Command Argument Value Type Invalid.";

    public static final String INVALID_REQUEST_BODY = "Invalid request body. Reason: Request body is either NULL or empty.";
    public static final String INVALID_REQUEST_BODY_FIELDS = "Invalid request body fields provided in the request.";
    public static final String REQUEST_BODY_NULL_EMPTY = "Request body is either NULL or empty.";
    public static final String ACCOUNT_IDENTIFIER=":identifier";
    public static final String SERVICE_IDENTIFIER=":serviceId";
    public static final String INVALID_ACCOUNT_MESSAGE="Invalid account id provided.";
    public static final String INVALID_SERVICE_MESSAGE="There is no mapping for given serviceId:- {}";
    public static final String INVALID_SERVICE="Invalid service id provided.";
    public static final String INVALID_SERVICE_ACCOUNT="The service id provided is invalid in this case as it does not exist or is not mapped to the concerned account";
    public static final String INVALID_INSTANCE="Invalid instance id provided.";
    public static final String INVALID_SERVICE_NAME = "Invalid Service name : Name can not be empty and it should have 1 - 128 characters only.";
    public static final String INVALID_SERVICE_IDENTIFIER = "Invalid Service identifier : Identifier should have 1 - 128 characters only.";
    public static final String INVALID_SERVICE_IDENTIFIER_FOR_CONNECTION = "Invalid value found for sourceServiceIdentifier/destinationServiceIdentifier/isDiscovery.";
    public static final String INVALID_SERVICE_TYPE = "Invalid Service Type : It should be 'Kubernetes'.";
    public static final String INVALID_TYPE_NAME = "Invalid Type Name";
    public static final String INVALID_DURATION = "Invalid Duration";
    public static final String INVALID_COMBINATION = "Invalid Combination of Type Id and Type Name";
    public static final String EMPTY_SERVICE="Service id is empty.";
    public static final String EMPTY_SERVICE_IDENTIFIER="Service identifier is empty.";
    public static final String DUPLICATE_SERVICE = "Duplicate Service name or identifier";
    public static final String MAINTENANCE_ID_EMPTY_ERROR = "Maintenance Type cannot be null";
    public static final String RECURRING_TYPE_EMPTY_ERROR = "Recurring Type Cannot be empty";
    public static final String START_HOUR_EMPTY_ERROR = "Start Hour cannot be empty";
    public static final String END_HOUR_EMPTY_ERROR = "End Hour cannot be empty";
    public static final String DAY_EMPTY_ERROR = "Day cannot be empty";
    public static final String WEEK_EMPTY_ERROR = "Week cannot be empty";
    public static final String MONTH_EMPTY_ERROR = "Month cannot be empty";
    public static final String INVALID_TIMESTAMP = "Invalid Timestamp";
    public static final String INVALID_TIME_ENTRY = "End time cannot be lesser than Start time";
    public static final String INVALID_START_TIME = "Start time cannot be lesser than date";
    public static final String INVALID_POST_FACTO_TIME = "Start time cannot be greater than date for Post Facto";
    public static final String INVALID_POST_FACTO = "Post Facto paramater cannot be null or empty";
    public static final String INVALID_POST_FACTO_VALUE = "Invalid Post Facto";
    public static final String DUPLICATE_NOTIFICATION_SETTING = "Duplicate Notification Setting";
    public static final String DUPLICATE_CONNECTION = "Duplicate Connection details";
    public static final String AGENT_CONFIGURED_ERROR="Agents are not configured.";
    public static final String AGENT_TYPE_ERROR="Error occurred while getting the agent type Details page.";
    public static final String AGENT_DATA_ERROR="Requested agent data is not available for selected account and service.";
    public static final String SERVICE_EMPTY_ERROR="Service Id should not be empty or null";
    public static final String SERVICE_EMPTY_ERROR_MESSAGE="Service Id should not be empty or null, serviceId:{}";
    public static final String AGENT_EMPTY_ERROR_MESSAGE="Agent Id should not be empty or null, agentId:{}";
    public static final String AGENT_EMPTY_ERROR="Agent Id should not be empty or null";
    public static final String COMMAND_TRIGGER_STATUS_ERROR_MESSAGE="Requested command trigger data is not available for selected agent.";

    public static final String TIMEZONE_INVALID_ID = "Invalid timezone id";

    public static final String INVALID_NOTIFICATION_TYPE="Invalid Notification Type Id provided.";
    public static final String INVALID_SEVERITY="Invalid combination of SeverityType and SeverityId provided.";
    public static final String INVALID_SIGNAL="Invalid combination of SignalType and SignalId provided.";
    public static final String INVALID_APPLICATION="Invalid Application Id provided";


    public static final String APPLICATIONS_FETCH_SUCCESS ="Application List fetched Successfully.";
    public static final String APP_NO_SERVICES = "Services not found : Atleast 1 valid service is required.";
    public static final String APPLICATION_ADD_SUCCESS ="New Application added successfully.";
    public static final String APPLICATION_EDIT_SUCCESS ="Application edited successfully.";
    public static final String APPLICATION_REM_SUCCESS ="Application(s) removed successfully.";
    public static final String INVALID_COMMAND_ID = "Invalid commandTypeId provided. Reason: It can be either 'Start', 'Stop', or 'Restart'";
    public static final String INVALID_PHYSICAL_AGENT_ID = "Invalid physicalAgentId. Reason: physicalAgentId not mapped to given service.";

    public static final String SERVICE_ADD_SUCCESS ="New Service(s) added successfully.";
    public static final String NOTIFICATION_SETTINGS_SUCCESS ="Notification Setting(s) updated successfully.";

    public static final String SERVICE_REM_SUCCESS ="Service and respective connections removed successfully.";
    public static final String EMPTY_COMP_INSTANCE_IDENTIFIER="Service identifier is empty.";
    public static final String COMP_INSTANCE_ADD_SUCCESS ="Component instance(s) added successfully.";
    public static final String COMP_INSTANCE_REM_SUCCESS ="Component instance(s) removed successfully.";
    public static final String COMP_INSTANCE_UPDATE_SUCCESS ="Component instance updated successfully.";
    public static final String EMPTY_SRC_IDENTIFIER="Source identifier is empty.";
    public static final String EMPTY_DEST_IDENTIFIER="Destination identifier is empty.";
    public static final String SAME_SRC_DEST_IDENTIFIER="Source and Destination identifier is same.";
    public static final String CONNECTION_REM_SUCCESS ="Connection(s) removed successfully.";
    public static final String CONNECTION_FETCH_SUCCESS ="Connection(s) fetched successfully.";
    public static final String CONNECTION_ADD_SUCCESS ="Connection(s) added successfully.";
    public static final String HOST_FETCH_SUCCESS ="Host(s) fetched successfully.";
    public static final String INSTANCE_UPDATE_SUCCESS ="Instance(s) details updated successfully.";
    public static final String AGENT_TYPE_FETCH_SUCCESS ="Agent type(s) fetched successfully";
    public static final String COMPONENT_ATTRIBUTES_FETCH_SUCCESS ="Component-Attributes List fetched successfully.";
    public static final String SERVICE_CLUSTER_MAP_FETCH_SUCCESS ="Services and Clusters fetched successfully.";
    public static final String INSTANCE_FETCH_SUCCESS ="Component Instance(s) fetched successfully.";
    public static final String INVALID_KPI_TYPE ="Invalid kpiType. Reason: kpiType is undefined in the request.";
    public static final String INVALID_THRESHOLD_TYPE ="Invalid thresholdType. Reason: thresholdType is undefined in the request.";
    public static final String INVALID_WEBHOOK_URL ="Invalid WebHook URL provided.";
    public static final String EMPTY_WEBHOOK_URL ="No WebHook URL provided.";
    public static final String WEBHOOK_GET_SUCCESS ="WebHook URL fetched successfully";
    public static final String WEBHOOK_ADD_SUCCESS ="WebHook URL added successfully";
    public static final String WEBHOOK_UPDATE_SUCCESS ="WebHook URL updated successfully";
    public static final String WEBHOOK_REM_SUCCESS ="WebHook URL removed successfully";
    public static final String DUPLICATE_COMPONENT_INSTANCE = "Duplicate Component Instance identifier or name";
    public static final String HOST_INSTANCE_NO_HOST_IDENTIFIER = "Host instance can't have host instance identifier";
    public static final String INVALID_TAG_DETAILS ="Invalid tag details.";
    public static final String EMPTY_AGENT_IDENTIFIER="Agent identifier is empty.";
    public static final String AGENT_REM_SUCCESS ="Agent removed successfully.";
    public static final String NOTIFICATION_TYPE_ERROR="Error occurred while getting the notification type details.";
    public static final String NOTIFICATION_DATA_ERROR="Requested notification data is not available for selected account.";
    public static final String INVALID_NOTIFICATION_MESSAGE="There is no type for given accountId:- {}";

    /*SMS Errors*/
    public static final String SMS_ADD_SUCCESS = "New SMS configuration added successfully";
    public static final String SMS_EDIT_SUCCESS = "SMS configuration is successfully updated";
    public static final String SMTP_ADD_SUCCESS = "New Email configuration added successfully";
    public static final String SMTP_DEL_SUCCESS = "Email configuration is successfully updated";
    public static final String SMTP_UP_SUCCESS = "Email configuration is successfully updated";
    public static final String SMTP_GET_SUCCESS = "Email configuration fetched successfully";
    public static final String SMS_GET_SUCCESS = "SMS configuration fetched successfully";
    public static final String DUPLICATE_SMS_PARAMETER = "Duplicate SMS parameter";
    public static final String INVALID_ADDRESS = "Address is either NULL, empty or its length is greater than 128 characters";
    public static final String SMTP_INVALID_ADDRESS = "Address is either NULL, empty or its length is greater than 256 characters";
    public static final String INVALID_STATUS = "{} is invalid. Value should be either 0 or 1.";
    public static final String INVALID_PORT = "Port is invalid. It should be an integer greater than 0";
    public static final String INVALID_COUNTRY_CODE = "Country code length is greater than 32 characters";
    public static final String INVALID_HTTP_METHOD = "HTTP method is either NULL or empty. It should be either GET or POST";
    public static final String INVALID_HTTP_URL = "HTTP URL is either NULL, empty or its length is greater than 512 characters";
    public static final String INVALID_SMS_PROTOCOL = "Protocol is either NULL or empty. It should be one of HTTP, HTTPS or TCP";
    public static final String INVALID_SMS_PARAMETER_NAME = "Parameter name is either NULL, empty or its length is more than 128 characters.";
    public static final String INVALID_SMS_PARAMETER_VALUE = "Parameter value is either NULL, empty or its length is more than 128 characters.";
    public static final String INVALID_SMS_PARAMETER_TYPE = "Parameter type id is invalid. It should be one of 165, 166 or 167";
    public static final String INVALID_SMS_PARAM_PLACEHOLDER = "SMS PARAMETER placeholder is NULL or EMPTY";
    public static final String INVALID_SMTP_USERNAME = "SMTP username length is more than 256 characters.";
    public static final String INVALID_SMTP_PWD = "SMTP pwd length is more than 256 characters.";
    public static final String INVALID_SMTP_SECURITY_TYPE = "SMTP security type is either NULL or empty. It should be one of TLS, SSL or NONE";
    public static final String INVALID_SMTP_FROM_RECEIPT = "SMTP from recipient is either NULL, empty or its length is more than 256 characters.";
    public static final String INVALID_SMS_PARAM_ACTION = "SMS Parameter is invalid. It can be ‘add’, ‘edit’ or ‘delete’";
    public static final String INVALID_SMS_PARAM_ADD = "SMS parameter is already present for the account:";
    /*Notification Messages*/
    public static final String ADD_NOTIFICATION_ERROR="error in adding user notification details";
    public static final String LOG_NOTIFICATION_ERROR="user notification list have some problem please check the log";
    public static final String LIST_NOTIFICATION_ERROR="notification list have some problem please see the log";
    public static final String NOTIFICATION_DETAILS_ERROR="error in adding notification details";
    public static final String NOTIFICATION_ACCOUNT_ERROR="user does not have access to given account please contact admin.";
    public static final String USER_ERROR="user should not be empty or NULL";

    //Batch process details related messages
    public static final String PROCESS_ADD_SUCCESS = "Batch process added successfully";
    public static final String PROCESS_GET_SUCCESS = "Batch processes fetched successfully";
    public static final String PROCESS_EDIT_SUCCESS = "Batch process updated successfully";
    public static final String PROCESS_DELETE_SUCCESS = "Batch process deleted successfully";
    public static final String BATCH_NAME_INVALID = "Batch name is NULL or empty or contains more than 256 characters";
    public static final String PROCESS_DETAILS_ID_NULL_OR_EMPTY = "Process details id is NULL or empty";

    // User Related Messages
    public static final String FETCH_USERS_SUCCESS ="User List fetched successfully.";
    public static final String FETCH_ROLES_SUCCESS ="Roles fetched successfully.";
    public static final String ADD_USER_SUCCESS ="User added successfully.";
    public static final String EDIT_USER_SUCCESS ="User details updated successfully.";

    public static final String FETCH_PROFILES_SUCCESS ="Profiles fetched successfully.";
    public static final String FETCH_USER_SUCCESS ="User Details fetched successfully.";

    public static final String INVALID_USER = "Invalid user identifier.";

    public static final String USER_DELETE_SUCCESS = "User deleted successfully.";
    public static final String SUPERVISOR_DUPLICATE_MESSAGE="Supervisor already present";
    public static final String SUPERVISOR_IDENTIFIER_INVALID_MESSAGE="Supervisor identifier is invalid.";

    public static final String INVALID_USER_ACCESS_DETAILS="access details is not available";
    public static final String INVALID_TIMEZONE = "Time zone is unavailable for the account";

    //Object reference modification
    public static final String OBJECT_MOD_SUCCESS = "Records updated successfully";
    public static final String INVALID_TRANSACTION_NAME = "Transaction name is null or length is lesser than 3 characters or length is greater than 256 characters";
    public static final String INVALID_TRANSACTION_ID = "Transaction id is 0";

    public static final String BATCH_ADD_SUCCESS = "New Batch created Successfully.";
    public static final String BATCH_GET_SUCCESS = "Batch fetched Successfully.";
    public static final String BATCH_UPD_SUCCESS = "Batch updated Successfully.";

    public static final String WHITELIST_DUPLICATE_MESSAGE="[%s] already whitelisted";
    public static final String WHITELIST_NOT_FOUND_MESSAGE="[%s] whitelist does not exist";
}
