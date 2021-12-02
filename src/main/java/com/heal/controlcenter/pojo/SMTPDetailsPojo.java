package com.heal.controlcenter.pojo;

import com.heal.controlcenter.util.UIMessages;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMTPDetailsPojo {

    private int id;
    private String address;
    private int port;
    private String username;
    private String password;
    private String security;
    private String fromRecipient;

    private static final int FIELD_LENGTH = 256;

    public Map<String, String> validate() {
        Map<String, String> error = new HashMap<>();

        if (address.isEmpty() || address.length() > FIELD_LENGTH) {
            log.error(UIMessages.SMTP_INVALID_ADDRESS);
            error.put("SMTP address", UIMessages.SMTP_INVALID_ADDRESS);
        }

        if (port <= 0) {
            log.error(UIMessages.INVALID_PORT);
            error.put("SMTP port", UIMessages.INVALID_PORT);
        }

        if (security.isEmpty()) {
            log.error(UIMessages.INVALID_SMTP_SECURITY_TYPE);
            error.put("SMTP security type", UIMessages.INVALID_SMTP_SECURITY_TYPE);
        }

        if (fromRecipient.isEmpty() || fromRecipient.length() > FIELD_LENGTH) {
            log.error(UIMessages.INVALID_SMTP_FROM_RECEIPT);
            error.put("SMTP recipient", UIMessages.INVALID_SMTP_FROM_RECEIPT);
        }

        if (!username.isEmpty() && username.length() > FIELD_LENGTH) {
            log.error(UIMessages.INVALID_SMTP_USERNAME);
            error.put("SMTP username", UIMessages.INVALID_SMTP_USERNAME);
        }

        if (!password.isEmpty() && password.length() > FIELD_LENGTH) {
            log.error(UIMessages.INVALID_SMTP_PWD);
            error.put("SMTP password", UIMessages.INVALID_SMTP_PWD);
        }
        return error;
    }
}
