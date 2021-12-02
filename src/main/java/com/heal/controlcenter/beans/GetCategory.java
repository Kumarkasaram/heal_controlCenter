package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetCategory {
	  private int id;
	    private String name;
	    private int workLoad;
}
