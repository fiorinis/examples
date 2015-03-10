package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;

public abstract class Material implements Serializable {

	private static final long serialVersionUID = 4041867770662433519L;

	public abstract String getId();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private String name;
	private String desc;

}
