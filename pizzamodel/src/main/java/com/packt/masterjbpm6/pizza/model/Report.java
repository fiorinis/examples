package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Report implements Serializable {

	private static final long serialVersionUID = -2273576357648156562L;

	public List<String> getIssues() {
		return issues;
	}

	public void setIssues(List<String> issues) {
		this.issues = issues;
	}

	public int getIssuesCount() {
		return issues.size();
	}

	public int getActionsCount() {
		return actions.size();
	}

	public void addIssue(String desc) {
		issues.add(desc);
	}

	public void addAction(String desc) {
		actions.add(desc);
	}

	private List<String> issues = new LinkedList<String>();
	private List<String> actions = new LinkedList<String>();

}
