package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "order")
public class Order implements Serializable {

	private static final long serialVersionUID = -2273576357648156562L;

	private Delivery delivery;
	private Report report;
	private String note;
	private String id;
	private OrderBOM bom;
	private double cost;
	private double deliveryFee;
	private List<Pizza> pizzas = new LinkedList<Pizza>();

	public Order() {
		delivery = new Delivery();
		report = new Report();
		id = String.format("%d", System.currentTimeMillis());
		bom = new OrderBOM(getId());
		System.out.println("create order with id=" + id);
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(double deliveryFee) {
		this.deliveryFee = deliveryFee;
		calcTotalCost();
	}

	public OrderBOM getBom() {
		return bom;
	}

	public String getId() {
		return id;
	}

	public int getPizzaCount() {
		return pizzas.size();
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<Pizza> getPizzas() {
		return pizzas;
	}

	public void setPizzas(List<Pizza> pizzas) {
		this.pizzas = pizzas;
		calcTotalCost();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void calcTotalCost() {
		cost = 0;
		for (Pizza pizza : pizzas) {
			cost += pizza.getPrice();
		}
		cost += deliveryFee;
	}

	public void buildBOM() {
		bom.calcBom(pizzas);
	}

	// calc the best route, based on other orders status and delivery address
	public void planRoute() {
		Route route = new Route();
		route.setDistance(2000 * new Random().nextDouble());
		getDelivery().setRoute(route);
	}

	public boolean isRouteSet() {
		return getDelivery() != null && getDelivery().getRoute() != null;
	}

	public String toString() {
		return "order: note=" + note + " cost=" + cost;
	}
}
