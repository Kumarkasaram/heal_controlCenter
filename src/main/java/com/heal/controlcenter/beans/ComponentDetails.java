package com.heal.controlcenter.beans;

import java.util.Set;

import com.heal.controlcenter.pojo.IdPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentDetails {
	  private int id;
	    private String name;
	    private Set<IdPojo> componentTypes;
	    private Set<VersionBean> commonVersions;
}
