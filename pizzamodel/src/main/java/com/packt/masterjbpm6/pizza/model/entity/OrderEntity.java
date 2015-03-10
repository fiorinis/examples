package com.packt.masterjbpm6.pizza.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderEntity implements Serializable {

	private static final long serialVersionUID = 510l;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "description")
	private String desc;
	@Column(name = "amount")
	private double amount;
	@Column(name = "pizza")
	private String pizza;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public OrderEntity() {
	}

	public OrderEntity(String desc) {
		this.setDesc(desc);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return "[OrderEntity Id: " + this.getId() + " desc= " + this.getDesc()
				+ " amount=" + getAmount() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OrderEntity other = (OrderEntity) obj;
		if (this.id != other.id
				&& (this.id == null || !this.id.equals(other.id))) {
			return false;
		}
		if ((this.desc == null) ? (other.desc != null) : !this.desc
				.equals(other.desc)) {
			return false;
		}
		return true;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}