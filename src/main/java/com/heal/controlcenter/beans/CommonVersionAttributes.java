package com.heal.controlcenter.beans;

import java.util.List;
import com.heal.controlcenter.pojo.IdPojo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonVersionAttributes {
	private int id;
	private String name;
	private List<IdPojo> componentVersion;
	private List<AttributesList> attributes;
}
