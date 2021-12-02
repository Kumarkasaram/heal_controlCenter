package com.heal.controlcenter.beans;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSDetailsBean {

    private int id;
    private String address;
    private int port;
    private String countryCode;
    private int protocolId;
    private String httpMethod;
    private String httpRelativeUrl;
    private int accountId;
    private String postData;
    private Integer postDataFlag;
    private String lastModifiedBy;
    private String createdTime;
    private String updatedTime;
    private int status;
    private int isMultiRequest;

}
