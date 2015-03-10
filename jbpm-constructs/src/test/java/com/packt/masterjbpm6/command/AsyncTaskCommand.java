package com.packt.masterjbpm6.command;

import org.kie.internal.executor.api.CommandContext;

import com.packt.masterjbpm6.pizza.model.Pizza;

public class AsyncTaskCommand implements org.kie.internal.executor.api.Command {

	public org.kie.internal.executor.api.ExecutionResults execute(
			CommandContext ctx) {
		String param1 = (String) ctx.getData("param1");

		System.out
				.println(String.format(
						"AsyncTaskCommand executed on executor with data {param1 = %s}",
						param1));
		org.kie.internal.executor.api.ExecutionResults executionResults = new org.kie.internal.executor.api.ExecutionResults();
		executionResults.setData("dataOut", "command terminated");

		return executionResults;
	}
}