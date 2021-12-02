package com.heal.controlcenter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyCloakUserDetails {
    private String id;
    private String username;
    private Boolean enabled;
    private String email;
    private Map<String,Object> attributes;

}
