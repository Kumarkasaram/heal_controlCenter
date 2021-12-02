package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSParameterBean {

    private int id;
    private String parameterName;
    private String parameterValue;
    private int parameterTypeId;
    private String lastModifiedBy;
    private String createdTime;
    private String updatedTime;
    private int isPlaceholder;
    private int smsDetailsId;

}
