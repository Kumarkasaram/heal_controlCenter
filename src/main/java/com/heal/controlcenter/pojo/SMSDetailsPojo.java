package com.heal.controlcenter.pojo;

import com.heal.controlcenter.util.UIMessages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSDetailsPojo {

    private int id;
    private String address;
    private int port;
    private String countryCode;
    private String protocolName;
    private String httpMethod;
    private String httpRelativeUrl;
    private String postData;
    private int isMultiRequest = 0;
    private List<SMSParameterPojo> parameters;

    private static final int ADDRESS_LENGTH = 128;
    private static final int COUNTRY_CODE_LENGTH = 32;
    private static final int HTTP_URL_LENGTH = 512;
    private static final String HTTP = "HTTP";

    public Map<String, String> validate() {
        Map<String, String> error = new HashMap<>();
        if (address.isEmpty() || address.length() > ADDRESS_LENGTH) {
            log.error(UIMessages.INVALID_ADDRESS);
            error.put("SMS address", UIMessages.INVALID_ADDRESS);
        }

        if (port <= 0) {
            log.error(UIMessages.INVALID_PORT);
            error.put("SMS Port", UIMessages.INVALID_PORT);
        }

        if (!countryCode.isEmpty() && countryCode.length() > COUNTRY_CODE_LENGTH) {
            log.error(UIMessages.INVALID_COUNTRY_CODE);
            error.put("SMS country", UIMessages.INVALID_COUNTRY_CODE);
        }

        if (protocolName.isEmpty()) {
            log.error(UIMessages.INVALID_SMS_PROTOCOL);
            error.put("SMS protocol", UIMessages.INVALID_SMS_PROTOCOL);
        }

        if (HTTP.equalsIgnoreCase(protocolName) && httpMethod.isEmpty()) {
            log.error(UIMessages.INVALID_HTTP_METHOD);
            error.put("SMS http method", UIMessages.INVALID_HTTP_METHOD);
        }

        if (HTTP.equalsIgnoreCase(protocolName) && (!httpRelativeUrl.isEmpty() && httpRelativeUrl.length() > HTTP_URL_LENGTH)) {
            log.error(UIMessages.INVALID_HTTP_URL);
            error.put("SMS http URL", UIMessages.INVALID_HTTP_URL);
        }

        if (isMultiRequest < 0 || isMultiRequest > 1) {
            log.error(UIMessages.INVALID_STATUS);
            error.put("Multi request", UIMessages.INVALID_STATUS);
        }

        for (SMSParameterPojo smsParameter : parameters) {
            Map<String, String> smsParameterError = smsParameter.validate();
            if (!smsParameterError.isEmpty()) {
                error.putAll(smsParameterError);
            }
        }

        Set<SMSParameterPojo> parameterSet = new HashSet<>(parameters);
        if (parameterSet.size() < parameters.size()) {
            log.error(UIMessages.DUPLICATE_SMS_PARAMETER);
            error.put("SMS parameters", UIMessages.DUPLICATE_SMS_PARAMETER);
        }
        return error;
    }
}
