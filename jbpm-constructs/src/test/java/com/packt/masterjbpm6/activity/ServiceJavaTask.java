package com.packt.masterjbpm6.activity;

import java.io.Serializable;

import com.packt.masterjbpm6.pizza.model.Order;

public class ServiceJavaTask implements Serializable {

	private static final long serialVersionUID = -596537272318696949L;

	public ServiceJavaTask() {
	}

	public Order processOrder(Order order) {
		System.out.println("processing order...");
		order.setNote("processed by servicetask");
		return order;
	}

}