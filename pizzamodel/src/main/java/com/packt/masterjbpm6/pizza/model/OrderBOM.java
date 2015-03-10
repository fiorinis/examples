package com.packt.masterjbpm6.pizza.model;

import java.util.List;

public class OrderBOM extends BoM {

	private static final long serialVersionUID = -7299344970651808160L;
	private String orderRef = null;

	public OrderBOM(String orderRef) {
		this.orderRef = orderRef;
	}

	public void calcBom(List<Pizza> pizzas) {
		for (Pizza pizza : pizzas) {
			for (Material material : pizza.getType().getAllIngredients()) {
				super.addMaterial(material, 1.0);
			}
		}
	}

	public String getOrderRef() {
		return orderRef;
	}

}
