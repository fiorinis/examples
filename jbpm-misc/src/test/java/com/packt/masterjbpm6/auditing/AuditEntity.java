package com.packt.masterjbpm6.auditing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AuditEntity")
public class AuditEntity implements Serializable {
	private static final long serialVersionUID = -4782260018051887397L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = 0L;
	@Column(name = "description")
	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
