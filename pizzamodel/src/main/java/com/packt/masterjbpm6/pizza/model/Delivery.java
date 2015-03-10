package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.Date;

public class Delivery implements Serializable {

	private static final long serialVersionUID = 4816546795082502139L;

	public boolean smooth() {
		return inTime() && retries == 0 && isDelivered();
	}

	public boolean inTime() {

		return getDeliveryDate() != null && getDueDate() != null
				&& getDeliveryDate().before(getDueDate());
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	private Date deliveryDate;
	private Date dueDate;
	private String deliveryAddress;
	private String deliveryName;
	private boolean delivered;
	private int retries = 0;
	private Route route;

}
