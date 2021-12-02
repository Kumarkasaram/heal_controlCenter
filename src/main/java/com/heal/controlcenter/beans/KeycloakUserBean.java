package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KeycloakUserBean {

    private String email;
    private String enabled;
    private String firstName;
    private String lastName;
    private String username;
    private List<Credential> credentials;

    @Builder
    @Data
    public static class Credential {
        private String type;
        private String value;
        private String temporary;

    }
}
