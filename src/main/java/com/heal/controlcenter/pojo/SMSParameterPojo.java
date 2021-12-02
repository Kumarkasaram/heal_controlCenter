package com.heal.controlcenter.pojo;

import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.UIMessages;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Builder
public class SMSParameterPojo {

    private int parameterId;
    private String parameterName;
    private String parameterValue;
    private String parameterType;
    private String action;
    private Boolean isPlaceholder;

    private static final int PARAMETER_LENGTH = 128;

    public Map<String, String> validate() {
        Map<String, String> error = new HashMap<>();
        if (parameterName.isEmpty() || parameterName.length() > PARAMETER_LENGTH) {
            log.error(UIMessages.INVALID_SMS_PARAMETER_NAME);
            error.put("SMS parameter name", UIMessages.INVALID_SMS_PARAMETER_NAME);
        }

        if (parameterValue.isEmpty() || parameterValue.length() > PARAMETER_LENGTH) {
            log.error(UIMessages.INVALID_SMS_PARAMETER_VALUE);
            error.put("SMS parameter Value", UIMessages.INVALID_SMS_PARAMETER_VALUE);
        }

        if (isPlaceholder == null) {
            log.error(UIMessages.INVALID_SMS_PARAM_PLACEHOLDER);
            error.put("SMS parameter placeholder", UIMessages.INVALID_SMS_PARAM_PLACEHOLDER);
        }

        if (action.isEmpty() || !(action.equalsIgnoreCase(Constants.SMS_ACTION_ADD)
                || action.equalsIgnoreCase(Constants.SMS_ACTION_EDIT)
                || action.equalsIgnoreCase(Constants.SMS_ACTION_DELETE))) {
            log.error(UIMessages.INVALID_SMS_PARAM_ACTION);
            error.put("SMS parameter action", UIMessages.INVALID_SMS_PARAM_ACTION);
        }
        return error;
    }
}
