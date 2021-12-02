package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class TagDetailsBean {
    private int id;
    private String name;
    private String refSelectColumnName;
    private String refWhereColumnName;
    private int tagTypeId;
    private int isPredefined;
    private String refTable;
    private String createdTime;
    private String updatedTime;
    private int accountId;
    private String userDetailsId;
}
