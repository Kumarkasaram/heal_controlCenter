package com.heal.controlcenter.beans;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMTPDetailsBean {

    private int id;
    private String address;
    private int port;
    private String username;
    private String password;
    private int securityId;
    private int accountId;
    private String fromRecipient;
    private String lastModifiedBy;
    private String createdTime;
    private String updatedTime;
    private int status;

}
