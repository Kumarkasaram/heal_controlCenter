package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewComponentAttributesPojo {

	  private int attributeId;
	    private String attributeName;
	    private String defaultValue;
	    private int isMandatory;
	    private int componentId;
	    private String componentName;
	    private int componentTypeId;
	    private String componentTypeName;
	    private int commonVersionId;
	    private String commonVersionName;
	    private int componentVersionId;
	    private String componentVersionName;
}
