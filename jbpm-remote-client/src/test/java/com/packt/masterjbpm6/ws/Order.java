package com.packt.masterjbpm6.ws;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "order")
public class Order implements Serializable {

	private static final long serialVersionUID = -2273576357648156562L;

	private Delivery delivery;
	private Report report;
	private String note;
	private String id;
	private OrderBOM bom;
	private double cost;
	private List<Pizza> pizzas = new LinkedList<Pizza>();

	public Order() {
		delivery = new Delivery();
		report = new Report();
		id = String.format("%d", System.currentTimeMillis());
		bom = new OrderBOM(getId());
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public OrderBOM getBom() {
		return bom;
	}

	public String getId() {
		return id;
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
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() {
		return "order: note=" + note + " cost=" + cost;
	}
}
