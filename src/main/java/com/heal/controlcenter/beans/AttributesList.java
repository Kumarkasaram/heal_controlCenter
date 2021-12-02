
package com.heal.controlcenter.beans;

		import lombok.Builder;
		import lombok.Data;

@Data
@Builder
public class AttributesList {
	private int id;
	private String name;
	private String defaultValue;
	private int isMandatory;
}
