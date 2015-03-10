package com.packt.masterjbpm6.activity;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.impl.EnvironmentFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;

import bitronix.tm.TransactionManagerServices;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class RuleTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "rule.bpmn", };
	public static final String[] rules = new String[] { "rule.drl", };

	public RuleTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);
	}

	@Test
	public void testRuleWithStorageServer() {
		int id = ksession.getId();
		KieServices ks = KieServices.Factory.get();
		KieStoreServices storeservice = ks.getStoreServices();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		Environment env = EnvironmentFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, super.getEmf());
		env.set(EnvironmentName.TRANSACTION_MANAGER,
				TransactionManagerServices.getTransactionManager());
		ksession = storeservice.newKieSession(kbase, null, env);
		testRule();
		id = ksession.getId();
		ksession.dispose();
		KieSession loadedsession = storeservice.loadKieSession(id, kbase, null,
				env);
		assertEquals(id, loadedsession.getId());
		ksession = loadedsession;
		testRule();
		assertTrue(true);
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

	@Test
	public void testRuleWithChannel() {
		ksession.registerChannel("appChannel", new AppChannel());
		testRule();
	}

	@Test
	public void testRuleWithLogger() {

		KieServices ks = KieServices.Factory.get();
		KieLoggers loggers = ks.getLoggers();
		KieRuntimeLogger logger = loggers.newThreadedFileLogger(ksession,
				"c:/temp/kie_threaded", 1000);

		testRule();
		logger.close();
	}

	private class AppChannel implements Channel {

		public void send(Object object) {
			System.out.println(" Mychannel:" + object);
		}
	}

}