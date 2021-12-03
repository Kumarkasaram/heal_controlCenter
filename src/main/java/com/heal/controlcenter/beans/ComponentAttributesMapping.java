package com.heal.controlcenter.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComponentAttributesMapping {
		private int id;
	    private String name;
	    private String type;
	    private List<CommonVersionAttributes> commonVersion;
}
