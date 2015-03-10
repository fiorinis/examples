package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;

public class Route implements Serializable {

	private static final long serialVersionUID = 7244226961596037941L;

	private double distance;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
