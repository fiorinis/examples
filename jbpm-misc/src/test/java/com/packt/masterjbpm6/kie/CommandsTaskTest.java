package com.packt.masterjbpm6.kie;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

/**
 * 
 * @author simo
 * 
 * */
public class CommandsTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "rule/rule.bpmn", };
	public static final String[] rules = new String[] { "rule/rule.drl", };

	public CommandsTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);
	}

	@Test
	public void testRule() {
		StringBuffer orderdesc = new StringBuffer();
		ksession.setGlobal("newnote", orderdesc);
		Order order = new Order();
		order.setCost(200);
		order.setNote("");
		ksession.setGlobal("orderglobal", order);
		FactHandle handle = ksession.insert(order);

		ProcessInstance processInstance = ksession.startProcess("rule");
		waitUserInput("type something to get global var");
		ksession.fireAllRules();

		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		assertEquals("URGENT", ordermodified.getNote());

	}

	/**
	 * executes the Rule test by Commands
	 */
	@Test
	public void testRuleWithCommand() {

		StringBuffer orderdesc = new StringBuffer();
		List<Command> batchcmds = new ArrayList<Command>();
		batchcmds.add(CommandFactory.newSetGlobal("newnote", orderdesc));
		Order order = new Order();
		order.setCost(200);
		batchcmds.add(CommandFactory.newInsert(order));
		batchcmds.add(CommandFactory.newSetGlobal("orderglobal", order));

		batchcmds.add(CommandFactory.newStartProcess("rule"));
		ExecutionResults results = ksession.execute(CommandFactory
				.newBatchExecution(batchcmds));
		waitUserInput("type something to get global var");
		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		assertEquals("URGENT", ordermodified.getNote());
	}

}