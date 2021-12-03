package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterComponentBean {
	   private int id;
	    private String name;
	    private int isCustom;
	    private int status;
	    private String createdTime;
	    private String updatedTime;
	    private String userDetailsId;
	    private int accountId;
	    private String identifier;
	    private String description;
	    private String componentTypeName;
	    private String componentVersionName;
	    private int componentVersionId;
	    private int componentTypeId;
	    private String commonVersionName;
	    private int commonVersionId;
	    private String discoveryPattern;
}
