package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterPageActionBean {
    private int id;
    private String name;
    private int status;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;
}
