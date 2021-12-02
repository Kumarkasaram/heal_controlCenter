package com.heal.controlcenter.beans;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserBean {

    private static final String ATTRIBUTE_ACCOUNTS = "";
    private static final String ATTRIBUTE_CONTACT_NUMBER = "";

    private boolean enabled;
    private boolean emailVerified;
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> accounts;
    private Map<String, Object> attributes;
    private Map<String, List<String>> applicationMap;
}

