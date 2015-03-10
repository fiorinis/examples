package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.List;

public class Pizza implements Serializable {

	private static final long serialVersionUID = 711389982863906404L;

	private String desc;
	private PizzaType type;
	private PizzaSize size;

	public PizzaType getType() {
		return type;
	}

	public void setType(PizzaType type) {
		this.type = type;
	}

	public String getName() {
		return type.getType().toString();
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Pizza(PizzaType type, String desc) {
		setType(type);
		setDesc(desc);
		setSize(PizzaSize.MEDIUM);
	}

	public Pizza(PizzaType type, PizzaSize size, String desc) {
		this(type, desc);
		setSize(size);
	}

	public Pizza(PizzaType type, PizzaSize size, List<Ingredient> moreToppings,
			String desc) {
		this(type, size, desc);
		type.setMoreToppings(moreToppings);
	}

	public double getPrice() {
		double baseprice = type.getPrice();
		double price = baseprice * (1 + size.ordinal());
		return price;
	}

	public PizzaSize getSize() {
		return size;
	}

	public void setSize(PizzaSize size) {
		this.size = size;
	}
}
