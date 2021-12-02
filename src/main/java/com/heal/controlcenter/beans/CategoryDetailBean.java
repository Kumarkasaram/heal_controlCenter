package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class CategoryDetailBean {
	 private int id;
	    private int accountId;
	    private int status;
	    private int isWorkLoad;
	    private int isInformative;
	    private boolean infoModified;
	    private int isCustom;
	    @NonNull
	    private String name;
	    private String description;
	    private String createdTime;
	    private String updatedTime;
	    private String userDetailsId;
	    private String identifier;
}
