package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BoM implements Serializable {

	private static final long serialVersionUID = -1606822042655690226L;

	public void addMaterial(Material material, double quantity) {
		Double qty = quantities.get(material.getId());
		if (qty == null) {
			qty = new Double(quantity);
		} else {
			qty += quantity;
		}
		quantities.put(material.getId(), qty);
	}

	public Map<String, Double> getQuantities() {
		return quantities;
	}

	public void setQuantities(Map<String, Double> quantities) {
		this.quantities = quantities;
	}

	public double getMaterialQuantity(String materialid) {
		return quantities.get(materialid);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	Map<String, Double> quantities = new HashMap<String, Double>();
	private boolean checked = false;

}
