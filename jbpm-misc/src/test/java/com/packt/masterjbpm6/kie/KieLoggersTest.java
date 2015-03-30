package com.packt.masterjbpm6.kie;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;

import com.packt.masterjbpm6.pizza.model.Order;

/**
 * 
 * @author simo
 * 
 *         NOTE: check the kmodule.xml in the project
 *         src/test/resources/META-INF for KBase definitions
 */
public class KieLoggersTest extends KieTest {
	//

	@Test
	public void testLoggers() {
		KieLoggers loggers = ks.getLoggers();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbaseLane");
		KieSession ksession = kbase.newKieSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		KieRuntimeLogger logger = loggers.newFileLogger(ksession,
				"c:/temp/kielogger");
		ksession.startProcess("lane");
		// ksession.signalEvent("signal", "signaldata");
		logger.close();
	}

	@Test
	public void testRuleWithThreadedLogger() {

		KieLoggers loggers = ks.getLoggers();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kbase.newKieSession();

		KieRuntimeLogger logger = loggers.newThreadedFileLogger(ksession,
				"c:/temp/kie_threaded", 1000);

		testRule(ksession);
		logger.close();
	}

	@Test
	public void testRuleWithConsoleLogger() {

		KieLoggers loggers = ks.getLoggers();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kbase.newKieSession();

		KieRuntimeLogger logger = loggers.newConsoleLogger(ksession);

		testRule(ksession);
		logger.close();
	}
	private void testRule(KieSession ksession) {
		StringBuffer orderdesc = new StringBuffer();
		ksession.setGlobal("newnote", orderdesc);
		Order order = new Order();
		order.setCost(200);
		order.setNote("");
		ksession.setGlobal("orderglobal", order);
		FactHandle handle = ksession.insert(order);

		ProcessInstance processInstance = ksession.startProcess("rule");
		ksession.fireAllRules();

		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		assertEquals("URGENT", ordermodified.getNote());
	}
	

}
