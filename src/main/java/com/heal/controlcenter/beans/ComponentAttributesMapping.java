package com.heal.controlcenter.beans;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComponentAttributesMapping {
		private int id;
	    private String name;
	    private String type;
	    private List<CommonVersionAttributes> commonVersion;
}
